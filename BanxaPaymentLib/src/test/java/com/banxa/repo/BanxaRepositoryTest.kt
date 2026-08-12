package com.banxa.repo

import com.banxa.nativepaymentssdk.data.api.BanxaApiService
import com.banxa.nativepaymentssdk.data.model.Blockchain
import com.banxa.nativepaymentssdk.data.model.BuyResponse
import com.banxa.nativepaymentssdk.data.model.Country
import com.banxa.nativepaymentssdk.data.model.CountryState
import com.banxa.nativepaymentssdk.data.model.CreateOrderRequest
import com.banxa.nativepaymentssdk.data.model.Crypto
import com.banxa.nativepaymentssdk.data.model.EligibilityResponse
import com.banxa.nativepaymentssdk.data.model.Fiat
import com.banxa.nativepaymentssdk.data.model.FiatPaymentMethod
import com.banxa.nativepaymentssdk.data.model.PaymentMethod
import com.banxa.nativepaymentssdk.data.model.Quote
import com.banxa.nativepaymentssdk.data.model.QuoteRequest
import com.banxa.nativepaymentssdk.data.repo.BanxaRepository
import io.mockk.coEvery
import io.mockk.coVerify
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

    @Test
    fun `getPaymentMethods returns success when api responds successfully`() = runTest {
        val paymentMethods = listOf(
            PaymentMethod(
                id = "apple-pay",
                name = "Apple Pay",
                description = "Buy with Apple Pay",
                supportedFiats = listOf("USD", "EUR")
            )
        )

        coEvery {
            apiService.getPaymentMethods(
                partner = "partner",
                orderType = "buy",
                apiKey = "apikey",
                fiat = null
            )
        } returns Response.success(paymentMethods)

        val result = repository.getPaymentMethods(
            partner = "partner",
            apiKey = "apikey",
            orderType = "buy"
        )

        assertTrue(result.isSuccess)
        assertEquals(paymentMethods, result.getOrNull())
    }

    @Test
    fun `getPaymentMethods passes optional fiat filter to api`() = runTest {
        coEvery {
            apiService.getPaymentMethods(
                partner = "partner",
                orderType = "buy",
                apiKey = "apikey",
                fiat = "USD"
            )
        } returns Response.success(emptyList())

        val result = repository.getPaymentMethods(
            partner = "partner",
            apiKey = "apikey",
            orderType = "buy",
            fiat = "USD"
        )

        assertTrue(result.isSuccess)
        coVerify {
            apiService.getPaymentMethods(
                partner = "partner",
                orderType = "buy",
                apiKey = "apikey",
                fiat = "USD"
            )
        }
    }

    @Test
    fun `getPaymentMethods returns failure when api returns error`() = runTest {
        coEvery {
            apiService.getPaymentMethods(
                partner = any(),
                orderType = any(),
                apiKey = any(),
                fiat = any()
            )
        } returns Response.error(400, "Bad Request".toResponseBody())

        val result = repository.getPaymentMethods(
            partner = "partner",
            apiKey = "apikey",
            orderType = "buy"
        )

        assertTrue(result.isFailure)
    }

    @Test
    fun `getPaymentMethods returns failure when api throws exception`() = runTest {
        coEvery {
            apiService.getPaymentMethods(
                partner = any(),
                orderType = any(),
                apiKey = any(),
                fiat = any()
            )
        } throws RuntimeException("Network Failure")

        val result = repository.getPaymentMethods(
            partner = "partner",
            apiKey = "apikey",
            orderType = "sell"
        )

        assertTrue(result.isFailure)
    }

    @Test
    fun `getCountries returns success when api responds successfully`() = runTest {
        val countries = listOf(
            Country(
                id = "US",
                description = "United States of America",
                states = listOf(
                    CountryState(id = "AL", description = "Alabama"),
                    CountryState(id = "CA", description = "California")
                )
            ),
            Country(
                id = "AU",
                description = "Australia"
            )
        )

        coEvery {
            apiService.getCountries(
                partner = "partner",
                apiKey = "apikey"
            )
        } returns Response.success(countries)

        val result = repository.getCountries(
            partner = "partner",
            apiKey = "apikey"
        )

        assertTrue(result.isSuccess)
        assertEquals(countries, result.getOrNull())
    }

    @Test
    fun `getCountries returns failure when api returns error`() = runTest {
        coEvery {
            apiService.getCountries(
                partner = any(),
                apiKey = any()
            )
        } returns Response.error(400, "Bad Request".toResponseBody())

        val result = repository.getCountries(
            partner = "partner",
            apiKey = "apikey"
        )

        assertTrue(result.isFailure)
    }

    @Test
    fun `getCountries returns failure when api throws exception`() = runTest {
        coEvery {
            apiService.getCountries(
                partner = any(),
                apiKey = any()
            )
        } throws RuntimeException("Network Failure")

        val result = repository.getCountries(
            partner = "partner",
            apiKey = "apikey"
        )

        assertTrue(result.isFailure)
    }

    @Test
    fun `getFiats returns success when api responds successfully`() = runTest {
        val fiats = listOf(
            Fiat(
                id = "USD",
                description = "US Dollar",
                symbol = "$",
                supportedPaymentMethods = listOf(
                    FiatPaymentMethod(
                        id = "debit-credit-card",
                        name = "Credit Debit Card",
                        minimum = "10",
                        maximum = "50000"
                    )
                )
            )
        )

        coEvery {
            apiService.getFiats(
                partner = "partner",
                orderType = "buy",
                apiKey = "apikey"
            )
        } returns Response.success(fiats)

        val result = repository.getFiats(
            partner = "partner",
            apiKey = "apikey",
            orderType = "buy"
        )

        assertTrue(result.isSuccess)
        assertEquals(fiats, result.getOrNull())
    }

    @Test
    fun `getFiats returns failure when api returns error`() = runTest {
        coEvery {
            apiService.getFiats(
                partner = any(),
                orderType = any(),
                apiKey = any()
            )
        } returns Response.error(400, "Bad Request".toResponseBody())

        val result = repository.getFiats(
            partner = "partner",
            apiKey = "apikey",
            orderType = "buy"
        )

        assertTrue(result.isFailure)
    }

    @Test
    fun `getFiats returns failure when api throws exception`() = runTest {
        coEvery {
            apiService.getFiats(
                partner = any(),
                orderType = any(),
                apiKey = any()
            )
        } throws RuntimeException("Network Failure")

        val result = repository.getFiats(
            partner = "partner",
            apiKey = "apikey",
            orderType = "sell"
        )

        assertTrue(result.isFailure)
    }

    @Test
    fun `getCrypto returns success when api responds successfully`() = runTest {
        val cryptos = listOf(
            Crypto(
                id = "AAVE",
                description = "Aave",
                blockchains = listOf(
                    Blockchain(
                        id = "ETH",
                        description = "Ethereum (ERC-20)",
                        isDefaultBlockchain = true,
                        address = "0x7fc66500c84a76ad7e9c93437bfc5ac33e2ddae9",
                        network = "1",
                        minimum = "1",
                        unsupportedCountries = mapOf("US" to listOf("NY"))
                    )
                )
            )
        )

        coEvery {
            apiService.getCrypto(
                partner = "partner",
                orderType = "buy",
                apiKey = "apikey"
            )
        } returns Response.success(cryptos)

        val result = repository.getCrypto(
            partner = "partner",
            apiKey = "apikey",
            orderType = "buy"
        )

        assertTrue(result.isSuccess)
        assertEquals(cryptos, result.getOrNull())
    }

    @Test
    fun `getCrypto returns failure when api returns error`() = runTest {
        coEvery {
            apiService.getCrypto(
                partner = any(),
                orderType = any(),
                apiKey = any()
            )
        } returns Response.error(400, "Bad Request".toResponseBody())

        val result = repository.getCrypto(
            partner = "partner",
            apiKey = "apikey",
            orderType = "buy"
        )

        assertTrue(result.isFailure)
    }

    @Test
    fun `getCrypto returns failure when api throws exception`() = runTest {
        coEvery {
            apiService.getCrypto(
                partner = any(),
                orderType = any(),
                apiKey = any()
            )
        } throws RuntimeException("Network Failure")

        val result = repository.getCrypto(
            partner = "partner",
            apiKey = "apikey",
            orderType = "sell"
        )

        assertTrue(result.isFailure)
    }

    @Test
    fun `getQuote returns success when api responds successfully`() = runTest {
        val quote = Quote(
            paymentMethodId = "debit-credit-card",
            cryptoAmount = "0.03564700",
            fiatAmount = "200.00",
            processingFee = "0.92",
            networkFee = "1.67"
        )

        coEvery {
            apiService.getQuote(
                partner = "partner",
                orderType = "buy",
                apiKey = "apikey",
                fiat = "AUD",
                crypto = "ETH",
                blockchain = "ETH",
                paymentMethodId = "debit-credit-card",
                fiatAmount = "200",
                cryptoAmount = null,
                externalCustomerId = null,
                ipAddress = null,
                discountCode = null
            )
        } returns Response.success(quote)

        val result = repository.getQuote(
            partner = "partner",
            apiKey = "apikey",
            orderType = "buy",
            request = quoteRequest()
        )

        assertTrue(result.isSuccess)
        assertEquals(quote, result.getOrNull())
    }

    @Test
    fun `getQuote returns failure when api returns error`() = runTest {
        coEvery {
            apiService.getQuote(
                partner = any(),
                orderType = any(),
                apiKey = any(),
                fiat = any(),
                crypto = any(),
                blockchain = any(),
                paymentMethodId = any(),
                fiatAmount = any(),
                cryptoAmount = any(),
                externalCustomerId = any(),
                ipAddress = any(),
                discountCode = any()
            )
        } returns Response.error(400, "Bad Request".toResponseBody())

        val result = repository.getQuote(
            partner = "partner",
            apiKey = "apikey",
            orderType = "buy",
            request = quoteRequest()
        )

        assertTrue(result.isFailure)
    }

    @Test
    fun `getQuote returns failure when api throws exception`() = runTest {
        coEvery {
            apiService.getQuote(
                partner = any(),
                orderType = any(),
                apiKey = any(),
                fiat = any(),
                crypto = any(),
                blockchain = any(),
                paymentMethodId = any(),
                fiatAmount = any(),
                cryptoAmount = any(),
                externalCustomerId = any(),
                ipAddress = any(),
                discountCode = any()
            )
        } throws RuntimeException("Network Failure")

        val result = repository.getQuote(
            partner = "partner",
            apiKey = "apikey",
            orderType = "sell",
            request = quoteRequest()
        )

        assertTrue(result.isFailure)
    }

    private fun quoteRequest() =
        QuoteRequest(
            fiat = "AUD",
            crypto = "ETH",
            blockchain = "ETH",
            paymentMethodId = "debit-credit-card",
            fiatAmount = "200"
        )

    private fun eligibilityRequest() =
        CreateOrderRequest(
            crypto = "BTC",
            fiat = "USD",
            fiatAmount = "100",
            walletAddress = "wallet-address",
            paymentMethodId = "card",
            redirectUrl = "banxa://redirect",
            email = "user@test.com"
        )

    private fun buyRequest() =
        CreateOrderRequest(
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