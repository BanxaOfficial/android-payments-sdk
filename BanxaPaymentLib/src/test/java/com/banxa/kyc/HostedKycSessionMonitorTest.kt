package com.banxa.kyc

import com.banxa.nativepaymentssdk.data.model.BanxaKycResult
import com.banxa.nativepaymentssdk.data.model.KycCompletionChannel
import com.banxa.nativepaymentssdk.data.model.KycSession
import com.banxa.nativepaymentssdk.kyc.HostedKycEvent
import com.banxa.nativepaymentssdk.kyc.HostedKycSessionMonitor
import com.banxa.nativepaymentssdk.kyc.NavigationDecision
import com.banxa.nativepaymentssdk.kyc.RejectionReason
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class HostedKycSessionMonitorTest {

    private val session = KycSession(
        redirectUrl = "https://kyc.banxa-sandbox.com/hosted/abc123?lang=en",
        expiresAt = "2026-09-23T11:00:00+00:00"
    )

    // 2026-09-23T10:00:00Z, an hour before the session expires.
    private val beforeExpiry = 1790157600000L

    /**
     * Creates a monitor for the test session.
     *
     * @param returnUrl
     * @param allowedOrigins
     * @param now
     * @return
     */
    private fun monitor(
        returnUrl: String? = "https://partner.example/kyc/done",
        allowedOrigins: List<String> = emptyList(),
        now: Long = beforeExpiry
    ) = HostedKycSessionMonitor(session, returnUrl, allowedOrigins) { now }

    private val banxaOrigin = "https://kyc.banxa-sandbox.com"
    private val complete = """{"type":"banxa:kyc-complete","status":"complete"}"""

    @Test
    fun `accepts the session url origin plus normalised extra origins`() {
        val monitor = monitor(allowedOrigins = listOf("https://Checkout.Banxa-Sandbox.com/", "  "))

        assertEquals(
            listOf(banxaOrigin, "https://checkout.banxa-sandbox.com"),
            monitor.acceptedOrigins
        )
    }

    @Test
    fun `completes on a main frame message from the session origin`() {
        val event = monitor().handleMessage(complete, banxaOrigin, isMainFrame = true)

        assertEquals(
            HostedKycEvent.Complete(BanxaKycResult("complete", KycCompletionChannel.MESSAGE)),
            event
        )
    }

    @Test
    fun `defaults the status when the completion message omits it`() {
        val event = monitor().handleMessage(
            """{"type":"banxa:kyc-complete"}""",
            banxaOrigin,
            isMainFrame = true
        )

        assertEquals(
            HostedKycEvent.Complete(BanxaKycResult("complete", KycCompletionChannel.MESSAGE)),
            event
        )
    }

    @Test
    fun `rejects a message from an origin that is not accepted`() {
        val event = monitor().handleMessage(complete, "https://evil.example", isMainFrame = true)

        assertEquals(
            HostedKycEvent.Rejected(RejectionReason.ORIGIN, "banxa:kyc-complete", "https://evil.example"),
            event
        )
    }

    @Test
    fun `rejects a message from a sub frame even on an accepted origin`() {
        val event = monitor().handleMessage(complete, banxaOrigin, isMainFrame = false)

        assertEquals(
            HostedKycEvent.Rejected(RejectionReason.FRAME, "banxa:kyc-complete", banxaOrigin),
            event
        )
    }

    @Test
    fun `accepts a message from a partner supplied origin`() {
        val event = monitor(allowedOrigins = listOf("https://checkout.banxa-sandbox.com/"))
            .handleMessage(complete, "https://checkout.banxa-sandbox.com", isMainFrame = true)

        assertTrue(event is HostedKycEvent.Complete)
    }

    @Test
    fun `relays error loading and ready messages`() {
        val monitor = monitor()

        assertEquals(
            HostedKycEvent.Error("kyc_failed", "Document rejected"),
            monitor.handleMessage(
                """{"type":"banxa:error","code":"kyc_failed","message":"Document rejected"}""",
                banxaOrigin,
                isMainFrame = true
            )
        )
        assertEquals(
            HostedKycEvent.Error("500", ""),
            monitor.handleMessage("""{"type":"banxa:error","code":500}""", banxaOrigin, true)
        )
        assertEquals(
            HostedKycEvent.Loading,
            monitor.handleMessage("""{"type":"banxa:loading","status":"loading"}""", banxaOrigin, true)
        )
        assertEquals(
            HostedKycEvent.Ready,
            monitor.handleMessage("""{"type":"banxa:ready","width":1,"height":1}""", banxaOrigin, true)
        )
    }

    @Test
    fun `ignores messages that are not banxa objects`() {
        val monitor = monitor()

        assertNull(monitor.handleMessage("""{"type":"other:thing"}""", banxaOrigin, true))
        assertNull(monitor.handleMessage("""{"status":"complete"}""", banxaOrigin, true))
        assertNull(monitor.handleMessage("""["banxa:kyc-complete"]""", banxaOrigin, true))
        assertNull(monitor.handleMessage("not json", banxaOrigin, true))
        assertNull(monitor.handleMessage("""{"type":"banxa:payment-complete"}""", banxaOrigin, true))
    }

    @Test
    fun `intercepts and completes on the https return url, query string included`() {
        val decision = monitor().handleNavigation("https://partner.example/kyc/done?customer=42")

        assertEquals(
            NavigationDecision.Intercept(
                HostedKycEvent.Complete(BanxaKycResult("complete", KycCompletionChannel.RETURN_URL))
            ),
            decision
        )
    }

    @Test
    fun `intercepts a deep link return url`() {
        val decision = monitor(returnUrl = "banxasample://kyc-return")
            .handleNavigation("banxasample://kyc-return?status=done")

        assertTrue(decision is NavigationDecision.Intercept)
    }

    @Test
    fun `lets unrelated and look alike navigations through`() {
        val monitor = monitor()

        assertEquals(NavigationDecision.Allow, monitor.handleNavigation("https://kyc.banxa-sandbox.com/step/2"))
        assertEquals(NavigationDecision.Allow, monitor.handleNavigation("https://partner.example/kyc/done-later"))
        assertEquals(NavigationDecision.Allow, monitor.handleNavigation("https://partner.example.evil.com/kyc/done"))
        assertEquals(NavigationDecision.Allow, monitor.handleNavigation("http://partner.example/kyc/done"))
        assertEquals(NavigationDecision.Allow, monitor.handleNavigation("::not a url::"))
    }

    @Test
    fun `allows every navigation when there is no return url`() {
        assertEquals(
            NavigationDecision.Allow,
            monitor(returnUrl = null).handleNavigation("https://partner.example/kyc/done")
        )
    }

    @Test
    fun `completes only once across both channels`() {
        val monitor = monitor()

        assertTrue(monitor.handleMessage(complete, banxaOrigin, true) is HostedKycEvent.Complete)
        assertNull(monitor.handleMessage(complete, banxaOrigin, true))
        // The redirect that follows the message must still be cancelled, without completing again.
        assertEquals(
            NavigationDecision.Intercept(null),
            monitor.handleNavigation("https://partner.example/kyc/done")
        )
    }

    @Test
    fun `reports expiry against the injected clock`() {
        assertFalse(monitor(now = beforeExpiry).isExpired)
        assertTrue(monitor(now = 1790161200000L).isExpired)
    }

    @Test
    fun `treats an unreadable expiry as not expired`() {
        val monitor = HostedKycSessionMonitor(session.copy(expiresAt = "soon"), null) { Long.MAX_VALUE }

        assertFalse(monitor.isExpired)
    }

    @Test
    fun `grants media capture only while the main frame is on an accepted origin`() {
        val monitor = monitor()

        assertTrue(monitor.shouldGrantMediaCapture("https://kyc.banxa-sandbox.com/verify/documents"))
        assertFalse(monitor.shouldGrantMediaCapture("https://partner.example/kyc/done"))
        assertFalse(monitor.shouldGrantMediaCapture(null))
    }
}
