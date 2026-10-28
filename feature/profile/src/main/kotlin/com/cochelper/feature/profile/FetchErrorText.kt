package com.cochelper.feature.profile

import com.cochelper.core.network.FetchError

internal fun FetchError.message(): String = when (this) {
    FetchError.INVALID_IP ->
        "Your API key doesn't allow the proxy's IP. On developer.clashofclans.com, set the key's allowed IP to 45.79.218.79."
    FetchError.INVALID_TOKEN -> "The API token was rejected. Check it in Settings, or create a new key."
    FetchError.NOT_FOUND -> "No player has this tag. Check it in Settings."
    FetchError.RATE_LIMITED -> "Too many requests. Try again in a minute."
    FetchError.MAINTENANCE -> "Clash of Clans is in maintenance. Try again later."
    FetchError.NETWORK -> "Can't reach the server. Check your internet connection."
    FetchError.MISSING_SETTINGS -> "Add your player tag and API token in Settings."
    FetchError.UNKNOWN -> "Something went wrong while loading your profile."
}

/** Errors the user fixes by editing settings, so the error card offers a Settings button. */
internal val FetchError.isSettingsProblem: Boolean
    get() = this in setOf(FetchError.INVALID_IP, FetchError.INVALID_TOKEN, FetchError.NOT_FOUND, FetchError.MISSING_SETTINGS)
