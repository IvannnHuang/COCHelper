package com.cochelper.core.network

import kotlinx.serialization.SerializationException
import java.io.IOException

/** Why a fetch failed, in terms the user can act on. */
enum class FetchError {
    /** The key's allowed IP doesn't match the proxy (45.79.218.79). */
    INVALID_IP,

    /** The token is wrong, expired or revoked. */
    INVALID_TOKEN,
    NOT_FOUND,
    RATE_LIMITED,
    MAINTENANCE,
    NETWORK,
    MISSING_SETTINGS,
    UNKNOWN,
}

fun Throwable.toFetchError(): FetchError = when (this) {
    is CocApiException -> when {
        reason == "accessDenied.invalidIp" -> FetchError.INVALID_IP
        code == 403 -> FetchError.INVALID_TOKEN
        code == 404 -> FetchError.NOT_FOUND
        code == 429 -> FetchError.RATE_LIMITED
        code == 503 -> FetchError.MAINTENANCE
        else -> FetchError.UNKNOWN
    }
    is IOException -> FetchError.NETWORK
    is SerializationException -> FetchError.UNKNOWN
    else -> FetchError.UNKNOWN
}
