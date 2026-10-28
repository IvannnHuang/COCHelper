package com.cochelper.core.data

import com.cochelper.core.data.PlayerRepository.Companion.isFresh
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RefreshPolicyTest {

    private val now = 1_000_000_000L
    private val minute = 60_000L

    @Test
    fun autoRefresh_skipsWithinTenMinutes() {
        assertTrue(isFresh(now - 9 * minute, now, force = false))
        assertFalse(isFresh(now - 11 * minute, now, force = false))
    }

    @Test
    fun forcedRefresh_onlyThrottledForThirtySeconds() {
        assertTrue(isFresh(now - 10_000, now, force = true))
        assertFalse(isFresh(now - 2 * minute, now, force = true))
    }

    @Test
    fun timestampInTheFuture_isNotFresh() {
        assertFalse(isFresh(now + minute, now, force = false))
    }
}
