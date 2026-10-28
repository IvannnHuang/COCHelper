package com.cochelper.core.data

import com.cochelper.core.database.PlayerDao
import com.cochelper.core.database.PlayerEntity
import com.cochelper.core.datastore.SettingsStore
import com.cochelper.core.model.Player
import com.cochelper.core.model.PlayerTag
import com.cochelper.core.network.CocApi
import com.cochelper.core.network.FetchError
import com.cochelper.core.network.toFetchError
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

data class CachedPlayer(val player: Player, val fetchedAtMillis: Long)

sealed interface RefreshResult {
    data object Updated : RefreshResult

    /** The cache was fresh enough, so the API wasn't called. */
    data object Skipped : RefreshResult
    data class Failed(val error: FetchError) : RefreshResult
}

/**
 * The player profile: served from the Room cache, refreshed from the API at most every
 * [AUTO_REFRESH_MILLIS] (or [MIN_FORCED_REFRESH_MILLIS] when the user asks), so we never hammer the API.
 */
@Singleton
class PlayerRepository @Inject constructor(
    private val settingsStore: SettingsStore,
    private val api: CocApi,
    private val dao: PlayerDao,
    private val json: Json,
) {

    @OptIn(ExperimentalCoroutinesApi::class)
    val player: Flow<CachedPlayer?> = settingsStore.settings
        .map { PlayerTag.normalize(it.playerTag) }
        .distinctUntilChanged()
        .flatMapLatest { tag -> dao.observe(tag) }
        .map { entity -> entity?.let(::decode) }

    suspend fun refresh(force: Boolean = false): RefreshResult {
        val settings = settingsStore.settings.first()
        if (!PlayerTag.isValid(settings.playerTag) || settings.apiToken.isBlank()) {
            return RefreshResult.Failed(FetchError.MISSING_SETTINGS)
        }
        val tag = PlayerTag.normalize(settings.playerTag)
        val now = System.currentTimeMillis()
        val cached = dao.get(tag)
        if (cached != null && isFresh(cached.fetchedAtMillis, now, force)) return RefreshResult.Skipped

        return try {
            val player = api.player(settings.baseUrl, settings.apiToken, tag)
            dao.upsert(PlayerEntity(tag, json.encodeToString(Player.serializer(), player), now))
            RefreshResult.Updated
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            RefreshResult.Failed(e.toFetchError())
        }
    }

    // A cache written by an older model version that no longer decodes is treated as missing.
    private fun decode(entity: PlayerEntity): CachedPlayer? =
        runCatching { CachedPlayer(json.decodeFromString(Player.serializer(), entity.json), entity.fetchedAtMillis) }
            .getOrNull()

    companion object {
        const val AUTO_REFRESH_MILLIS = 10 * 60 * 1000L
        const val MIN_FORCED_REFRESH_MILLIS = 30 * 1000L

        fun isFresh(fetchedAtMillis: Long, nowMillis: Long, force: Boolean): Boolean {
            val age = nowMillis - fetchedAtMillis
            return age in 0 until if (force) MIN_FORCED_REFRESH_MILLIS else AUTO_REFRESH_MILLIS
        }
    }
}
