package com.banxa.kyc

import android.util.Log
import com.banxa.nativepaymentssdk.core.Environment
import com.banxa.nativepaymentssdk.data.model.CreateKycSessionRequest
import com.banxa.nativepaymentssdk.data.model.KycSession
import com.banxa.nativepaymentssdk.data.model.KycTier
import com.banxa.nativepaymentssdk.data.repo.BanxaRepository
import com.banxa.nativepaymentssdk.di.RetrofitClient
import com.google.gson.Gson
import com.google.gson.JsonObject
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.SocketPolicy
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Runs the real Retrofit client and Gson config against a local HTTP server, so the request the
 * SDK actually sends is what gets asserted.
 */
class CreateKycSessionTest {

    private lateinit var server: MockWebServer
    private lateinit var repository: BanxaRepository

    /**
     * Starts the local server and a repository pointed at it.
     *
     * @return
     */
    @Before
    fun setup() {
        // RetrofitClient logs its base URL, and android.util.Log isn't available on the JVM.
        mockkStatic(Log::class)
        every { Log.i(any(), any()) } returns 0

        server = MockWebServer()
        server.start()
        repository = BanxaRepository(RetrofitClient.getApi(server.url("/").toString(), Environment.SANDBOX))
    }

    /**
     * Stops the local server and restores logging.
     *
     * @return
     */
    @After
    fun tearDown() {
        server.shutdown()
        unmockkStatic(Log::class)
    }

    /**
     * Returns the 201 response PAPI sends for a created session.
     *
     * @return
     */
    private fun createdResponse() = MockResponse()
        .setResponseCode(201)
        .setBody("""{"redirectUrl":"https://kyc.banxa-sandbox.com/hosted/abc","expiresAt":"2026-09-23T11:00:00+00:00"}""")

    @Test
    fun `posts the request to the partner kyc sessions endpoint`() = runTest {
        server.enqueue(createdResponse())

        val result = repository.createKycSession(
            partner = "demo-partner",
            apiKey = "secret-key",
            request = CreateKycSessionRequest(
                externalCustomerId = "customer-42",
                tier = KycTier.ENHANCED,
                country = "AU",
                returnUrl = "myapp://kyc-return"
            )
        )

        val recorded = server.takeRequest()
        assertEquals("POST", recorded.method)
        assertEquals("/demo-partner/v2/kyc/sessions", recorded.path)
        assertEquals("secret-key", recorded.getHeader("x-api-key"))

        val body = Gson().fromJson(recorded.body.readUtf8(), JsonObject::class.java)
        assertEquals("customer-42", body["externalCustomerId"].asString)
        assertEquals("enhanced", body["tier"].asString)
        assertEquals("AU", body["country"].asString)
        assertEquals("myapp://kyc-return", body["returnUrl"].asString)

        assertEquals(
            KycSession("https://kyc.banxa-sandbox.com/hosted/abc", "2026-09-23T11:00:00+00:00"),
            result.getOrNull()
        )
    }

    @Test
    fun `leaves out fields the partner did not supply instead of sending null`() = runTest {
        server.enqueue(createdResponse())

        repository.createKycSession("demo-partner", "secret-key", CreateKycSessionRequest("customer-42"))

        val body = Gson().fromJson(server.takeRequest().body.readUtf8(), JsonObject::class.java)
        assertEquals(setOf("externalCustomerId"), body.keySet())
    }

    @Test
    fun `fails with the status code when the api refuses the request`() = runTest {
        server.enqueue(MockResponse().setResponseCode(403).setBody("""{"message":"Forbidden"}"""))

        val result = repository.createKycSession("demo-partner", "secret-key", CreateKycSessionRequest("c"))

        assertTrue(result.isFailure)
        assertEquals("403", result.exceptionOrNull()?.message)
    }

    @Test
    fun `fails when the response has no redirect url`() = runTest {
        server.enqueue(MockResponse().setResponseCode(201).setBody("""{"expiresAt":"2026-09-23T11:00:00+00:00"}"""))

        val result = repository.createKycSession("demo-partner", "secret-key", CreateKycSessionRequest("c"))

        assertFalse(result.isSuccess)
    }

    @Test
    fun `fails when the network drops`() = runTest {
        server.enqueue(MockResponse().setSocketPolicy(SocketPolicy.DISCONNECT_AT_START))

        val result = repository.createKycSession("demo-partner", "secret-key", CreateKycSessionRequest("c"))

        assertTrue(result.isFailure)
    }
}
