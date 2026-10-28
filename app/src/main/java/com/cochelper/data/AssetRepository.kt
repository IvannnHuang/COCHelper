package com.cochelper.data

import android.content.Context
import kotlinx.serialization.json.Json

/** Bundled, editable game knowledge in app/src/main/assets. */
class AssetRepository(private val context: Context) {

    private val json = Json { ignoreUnknownKeys = true }

    private inline fun <reified T> load(file: String): T =
        context.assets.open(file).bufferedReader().use { json.decodeFromString<T>(it.readText()) }

    val layouts: List<Layout> by lazy { load("layouts.json") }
    val strategies: List<Strategy> by lazy { load("strategies.json") }
    val unitIds: UnitIds by lazy { load("unit_ids.json") }
}
