package com.banxa.kyc

import com.banxa.nativepaymentssdk.data.model.KycSession
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class KycSessionTest {

    /**
     * Returns the expiry a session with [value] reports.
     *
     * @param value
     * @return
     */
    private fun expiry(value: String) = KycSession("https://kyc.banxa.com/x", value).expirationMillis

    @Test
    fun `parses an offset timestamp`() {
        assertEquals(1790161200000L, expiry("2026-09-23T11:00:00+00:00"))
        assertEquals(1790161200000L, expiry("2026-09-23T19:00:00+08:00"))
    }

    @Test
    fun `parses a zulu timestamp`() {
        assertEquals(1790161200000L, expiry("2026-09-23T11:00:00Z"))
    }

    @Test
    fun `parses fractional seconds of any precision`() {
        assertEquals(1790161200000L, expiry("2026-09-23T11:00:00.123456+00:00"))
        assertEquals(1790161200000L, expiry("2026-09-23T11:00:00.5Z"))
    }

    @Test
    fun `returns null rather than failing on an unreadable timestamp`() {
        assertNull(expiry("soon"))
        assertNull(expiry("2026-09-23T11:00:00"))
        assertNull(expiry(""))
    }
}
