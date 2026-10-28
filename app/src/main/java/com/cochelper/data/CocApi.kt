package com.cochelper.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.URLEncoder

/**
 * Read-only client for the official Clash of Clans API (https://developer.clashofclans.com).
 *
 * Official keys are locked to an IP address, which a phone doesn't have a stable one of.
 * The default base URL is the RoyaleAPI proxy: create your key with IP 45.79.218.79 whitelisted.
 */
class CocApi(private val client: OkHttpClient = OkHttpClient()) {

    private val json = Json { ignoreUnknownKeys = true }

    suspend fun player(baseUrl: String, token: String, tag: String): Player = withContext(Dispatchers.IO) {
        val normalized = "#" + tag.trim().removePrefix("#").uppercase()
        val request = Request.Builder()
            .url("${baseUrl.trimEnd('/')}/players/${URLEncoder.encode(normalized, "UTF-8")}")
            .header("Authorization", "Bearer ${token.trim()}")
            .header("Accept", "application/json")
            .build()
        client.newCall(request).execute().use { response ->
            val body = response.body?.string().orEmpty()
            if (!response.isSuccessful) {
                val error = runCatching { json.decodeFromString<ApiError>(body) }.getOrNull()
                throw IllegalStateException("API ${response.code}: ${error?.reason ?: error?.message ?: body.take(200)}")
            }
            json.decodeFromString<Player>(body)
        }
    }

    companion object {
        const val DEFAULT_BASE_URL = "https://cocproxy.royaleapi.dev/v1"
    }
}
