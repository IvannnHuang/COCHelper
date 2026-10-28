package com.cochelper.core.network

import com.cochelper.core.model.Player
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.URLEncoder
import javax.inject.Inject
import javax.inject.Singleton

/** Read-only client for the official Clash of Clans API (https://developer.clashofclans.com). */
@Singleton
class CocApi @Inject constructor(
    private val client: OkHttpClient,
    private val json: Json,
) {

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
                throw CocApiException(response.code, error?.reason, error?.message ?: body.take(200))
            }
            json.decodeFromString<Player>(body)
        }
    }
}

/** `reason` is the API's error code, e.g. "accessDenied.invalidIp" or "notFound". */
class CocApiException(val code: Int, val reason: String?, message: String?) :
    Exception("API $code: ${reason ?: message}")

@Serializable
private data class ApiError(val reason: String? = null, val message: String? = null)
