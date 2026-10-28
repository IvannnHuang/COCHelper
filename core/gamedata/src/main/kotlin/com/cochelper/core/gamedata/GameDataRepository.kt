package com.cochelper.core.gamedata

import android.content.Context
import com.cochelper.core.model.GameIds
import com.cochelper.core.model.Layout
import com.cochelper.core.model.Strategy
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Bundled game knowledge in src/main/assets. Sourced from public community data and our own
 * observations only, never from the game files (PLAN.md §1).
 */
@Singleton
class GameDataRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val json: Json,
) {

    val gameIds: GameIds by lazy { load("game_ids.json") }
    val layouts: List<Layout> by lazy { load("layouts.json") }
    val strategies: List<Strategy> by lazy { load("strategies.json") }

    private inline fun <reified T> load(file: String): T =
        context.assets.open(file).bufferedReader().use { json.decodeFromString<T>(it.readText()) }
}
