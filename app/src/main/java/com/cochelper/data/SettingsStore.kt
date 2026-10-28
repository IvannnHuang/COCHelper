package com.cochelper.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val Context.dataStore by preferencesDataStore("settings")

data class Settings(
    val playerTag: String = "",
    val apiToken: String = "",
    val baseUrl: String = CocApi.DEFAULT_BASE_URL,
    val userLayouts: List<Layout> = emptyList(),
)

class SettingsStore(private val context: Context) {

    private val json = Json { ignoreUnknownKeys = true }

    val settings: Flow<Settings> = context.dataStore.data.map { prefs ->
        Settings(
            playerTag = prefs[TAG].orEmpty(),
            apiToken = prefs[TOKEN].orEmpty(),
            baseUrl = prefs[BASE_URL] ?: CocApi.DEFAULT_BASE_URL,
            userLayouts = prefs[LAYOUTS]?.let { runCatching { json.decodeFromString<List<Layout>>(it) }.getOrNull() }
                .orEmpty(),
        )
    }

    suspend fun saveAccount(tag: String, token: String, baseUrl: String) {
        context.dataStore.edit {
            it[TAG] = tag
            it[TOKEN] = token
            it[BASE_URL] = baseUrl
        }
    }

    suspend fun saveUserLayouts(layouts: List<Layout>) {
        context.dataStore.edit { it[LAYOUTS] = json.encodeToString(layouts) }
    }

    private companion object {
        val TAG = stringPreferencesKey("player_tag")
        val TOKEN = stringPreferencesKey("api_token")
        val BASE_URL = stringPreferencesKey("base_url")
        val LAYOUTS = stringPreferencesKey("user_layouts")
    }
}
