package com.cochelper.core.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.cochelper.core.model.ApiConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore("settings")

data class UserSettings(
    val playerTag: String,
    val apiToken: String,
    val baseUrl: String,
)

/** Account settings. Unset values fall back to the dev defaults from local.properties. */
@Singleton
class SettingsStore @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    val settings: Flow<UserSettings> = context.dataStore.data.map { prefs ->
        UserSettings(
            playerTag = prefs[PLAYER_TAG] ?: BuildConfig.DEFAULT_PLAYER_TAG,
            apiToken = prefs[API_TOKEN] ?: BuildConfig.DEFAULT_API_TOKEN,
            baseUrl = prefs[BASE_URL] ?: ApiConfig.DEFAULT_BASE_URL,
        )
    }

    suspend fun save(settings: UserSettings) {
        context.dataStore.edit {
            it[PLAYER_TAG] = settings.playerTag
            it[API_TOKEN] = settings.apiToken
            it[BASE_URL] = settings.baseUrl
        }
    }

    private companion object {
        val PLAYER_TAG = stringPreferencesKey("player_tag")
        val API_TOKEN = stringPreferencesKey("api_token")
        val BASE_URL = stringPreferencesKey("base_url")
    }
}
