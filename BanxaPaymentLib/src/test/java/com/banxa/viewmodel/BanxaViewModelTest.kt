package com.banxa.viewmodel

import com.banxa.nativepaymentssdk.core.Banxa
import com.banxa.nativepaymentssdk.core.Environment
import com.banxa.nativepaymentssdk.data.model.CreateBuyOrderRequest
import com.banxa.nativepaymentssdk.data.model.EligibilityResponse
import com.banxa.nativepaymentssdk.data.repo.BanxaRepository
import com.banxa.nativepaymentssdk.viewmodel.BanxaViewModel
import com.banxa.nativepaymentssdk.viewmodel.BuyUiState
import com.banxa.nativepaymentssdk.viewmodel.EligibilityUiState
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BanxaViewModelTest {

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)

        Banxa.initialize(
            Banxa.Builder()
                .apiKey("apikey")
                .partner("partner")
                .environment(Environment.SANDBOX)
                .build()
        )

        viewModel = BanxaViewModel(repository)
    }

    private val dispatcher = StandardTestDispatcher()

    private val repository: BanxaRepository = mockk(relaxed = true)

    private lateinit var viewModel: BanxaViewModel


    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `checkEligibility emits Success when repository returns eligible`() = runTest {

        val response = mockk<EligibilityResponse>()

        coEvery { response.paymentReady } returns true

        coEvery {
            repository.checkEligibility(any(), any(), any())
        } returns Result.success(response)

        viewModel.checkEligibility(validRequest())

        advanceUntilIdle()

        assertTrue(
            viewModel.eligibilityUiState.value is EligibilityUiState.Success
        )
    }

    @Test
    fun `checkEligibility emits NotEligible when paymentReady is false`() = runTest {

        val response = mockk<EligibilityResponse>()

        coEvery { response.paymentReady } returns false
        coEvery { response.requirements } returns listOf("KYC_REQUIRED")

        coEvery {
            repository.checkEligibility(any(), any(), any())
        } returns Result.success(response)

        viewModel.checkEligibility(validRequest())

        advanceUntilIdle()

        assertTrue(
            viewModel.eligibilityUiState.value is EligibilityUiState.NotEligible
        )
    }

    @Test
    fun `checkEligibility emits Error when repository fails`() = runTest {

        coEvery {
            repository.checkEligibility(any(), any(), any())
        } returns Result.failure(Exception("Network Error"))

        viewModel.checkEligibility(validRequest())

        advanceUntilIdle()

        assertTrue(
            viewModel.eligibilityUiState.value is EligibilityUiState.Error
        )
    }

    @Test
    fun `createOrder emits Error when repository returns failure`() = runTest {

        coEvery {
            repository.createOrderAndShowPrimerCheckout(any(), any(), any())
        } returns Result.failure(Exception("API Error"))

        viewModel.createOrder(validRequest())

        advanceUntilIdle()

        assertTrue(
            viewModel.buyUiState.value is BuyUiState.Error
        )
    }

    private fun validRequest() =
        CreateBuyOrderRequest(
            externalCustomerId = "customer-123",
            fiat = "USD",
            crypto = "BTC",
            fiatAmount = "100",
            walletAddress = "wallet-address",
            redirectUrl = "banxa://redirect",
            paymentMethodId = "card",
            email = "user@test.com"
        )
}