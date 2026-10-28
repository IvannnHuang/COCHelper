package com.cochelper.core.model

object ApiConfig {
    /**
     * Official API keys are locked to an IP address, which a phone doesn't have a stable one of.
     * The RoyaleAPI proxy forwards requests from 45.79.218.79, so keys are created with that IP.
     */
    const val DEFAULT_BASE_URL = "https://cocproxy.royaleapi.dev/v1"
}
