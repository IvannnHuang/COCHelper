package com.cochelper.core.network

import org.junit.Assert.assertEquals
import org.junit.Test
import java.net.UnknownHostException

class FetchErrorTest {

    @Test
    fun invalidIpReason_beatsGeneric403() {
        assertEquals(FetchError.INVALID_IP, CocApiException(403, "accessDenied.invalidIp", null).toFetchError())
        assertEquals(FetchError.INVALID_TOKEN, CocApiException(403, "accessDenied", null).toFetchError())
    }

    @Test
    fun statusCodes_mapToErrors() {
        assertEquals(FetchError.NOT_FOUND, CocApiException(404, "notFound", null).toFetchError())
        assertEquals(FetchError.RATE_LIMITED, CocApiException(429, "requestThrottled", null).toFetchError())
        assertEquals(FetchError.MAINTENANCE, CocApiException(503, "inMaintenance", null).toFetchError())
        assertEquals(FetchError.UNKNOWN, CocApiException(500, null, "boom").toFetchError())
    }

    @Test
    fun ioFailure_isNetworkError() {
        assertEquals(FetchError.NETWORK, UnknownHostException("cocproxy.royaleapi.dev").toFetchError())
    }
}
