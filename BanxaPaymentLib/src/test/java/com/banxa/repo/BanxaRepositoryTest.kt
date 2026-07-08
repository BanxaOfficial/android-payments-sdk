package com.banxa.repo

import com.banxa.nativepaymentssdk.data.api.BanxaApiService
import com.banxa.nativepaymentssdk.data.model.BuyResponse
import com.banxa.nativepaymentssdk.data.model.CreateBuyOrderRequest
import com.banxa.nativepaymentssdk.data.model.EligibilityResponse
import com.banxa.nativepaymentssdk.data.repo.BanxaRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class BanxaRepositoryTest {

    private lateinit var repository: BanxaRepository
    private val apiService: BanxaApiService = mockk()

    @Before
    fun setup() {
        repository = BanxaRepository(apiService)
    }

    @Test
    fun `checkEligibility returns success when api responds successfully`() = runTest {

        val response = mockk<EligibilityResponse>()

        coEvery {
            apiService.checkEligibility(
                any(),
                any(),
                any()
            )
        } returns Response.success(response)

        val result = repository.checkEligibility(
            request1 = eligibilityRequest(),
            partner = "partner",
            apiKey = "apikey"
        )

        assertTrue(result.isSuccess)
        assertEquals(response, result.getOrNull())
    }

    @Test
    fun `checkEligibility returns failure when api returns error`() = runTest {

        coEvery {
            apiService.checkEligibility(
                any(),
                any(),
                any()
            )
        } returns Response.error(
            400,
            "Bad Request".toResponseBody()
        )

        val result = repository.checkEligibility(
            request1 = eligibilityRequest(),
            partner = "partner",
            apiKey = "apikey"
        )

        assertTrue(result.isFailure)
    }

    @Test
    fun `checkEligibility returns failure when api throws exception`() = runTest {

        coEvery {
            apiService.checkEligibility(
                any(),
                any(),
                any()
            )
        } throws RuntimeException("Timeout")

        val result = repository.checkEligibility(
            request1 = eligibilityRequest(),
            partner = "partner",
            apiKey = "apikey"
        )

        assertTrue(result.isFailure)
    }

    @Test
    fun `createOrder returns success when api responds successfully`() = runTest {

        val response = mockk<BuyResponse>()

        coEvery {
            apiService.createBuyOrder(
                any(),
                any(),
                any()
            )
        } returns Response.success(response)

        val result = repository.createOrderAndShowPrimerCheckout(
            request = buyRequest(),
            partner = "partner",
            apiKey = "apikey"
        )

        assertTrue(result.isSuccess)
        assertEquals(response, result.getOrNull())
    }

    @Test
    fun `createOrder returns failure when api returns error`() = runTest {

        coEvery {
            apiService.createBuyOrder(
                any(),
                any(),
                any()
            )
        } returns Response.error(
            500,
            "Internal Server Error".toResponseBody()
        )

        val result = repository.createOrderAndShowPrimerCheckout(
            request = buyRequest(),
            partner = "partner",
            apiKey = "apikey"
        )

        assertTrue(result.isFailure)
    }

    @Test
    fun `createOrder returns failure when api throws exception`() = runTest {

        coEvery {
            apiService.createBuyOrder(
                any(),
                any(),
                any()
            )
        } throws RuntimeException("Network Failure")

        val result = repository.createOrderAndShowPrimerCheckout(
            request = buyRequest(),
            partner = "partner",
            apiKey = "apikey"
        )

        assertTrue(result.isFailure)
    }

    private fun eligibilityRequest() =
        CreateBuyOrderRequest(
            crypto = "BTC",
            fiat = "USD",
            fiatAmount = "100",
            walletAddress = "wallet-address",
            paymentMethodId = "card",
            redirectUrl = "banxa://redirect",
            email = "user@test.com"
        )

    private fun buyRequest() =
        CreateBuyOrderRequest(
            externalCustomerId = "customer-123",
            fiat = "USD",
            crypto = "BTC",
            fiatAmount = "100",
            walletAddress = "wallet-address",
            paymentMethodId = "card",
            redirectUrl = "banxa://redirect",
            email = "user@test.com",
            walletAddressTag = null,
            metadata = null,
            subPartnerId = null,
            discountCode = null,
            externalOrderId = null
        )
}