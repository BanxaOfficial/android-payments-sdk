package com.banxa.nativepaymentssdk.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.banxa.nativepaymentssdk.core.Banxa
import com.banxa.nativepaymentssdk.data.model.BuyRequest
import com.banxa.nativepaymentssdk.data.model.CreateBuyOrderRequest
import com.banxa.nativepaymentssdk.data.model.EligibilityRequest
import com.banxa.nativepaymentssdk.data.repo.BanxaRepository
import com.banxa.nativepaymentssdk.di.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BanxaViewModel(
    private val repository: BanxaRepository
) : ViewModel() {

    private val _eligibilityUiState =
        MutableStateFlow<EligibilityUiState>(
            EligibilityUiState.Idle
        )
    val eligibilityUiState = _eligibilityUiState.asStateFlow()

    private val _buyUiState =
        MutableStateFlow<BuyUiState>(
            BuyUiState.Idle
        )

    val buyUiState = _buyUiState.asStateFlow()

    fun checkEligibility(createBuyOrderRequest: CreateBuyOrderRequest) {
        viewModelScope.launch {
            _eligibilityUiState.value =
                EligibilityUiState.Loading
            val request = EligibilityRequest(
                crypto = createBuyOrderRequest.crypto,
                fiat = createBuyOrderRequest.fiat,
                walletAddress = createBuyOrderRequest.walletAddress,
                email = createBuyOrderRequest.email,
                redirectUrl= createBuyOrderRequest.redirectUrl,
                fiatAmount= createBuyOrderRequest.fiatAmount,
                paymentMethodId = createBuyOrderRequest.paymentMethodId
            )
            try {
                repository.checkEligibility(
                    request,
                    Banxa.getInstance().partner,
                    Banxa.getInstance().apiKey
                )
                    .onSuccess {
                        if (it.paymentReady) {
                            _eligibilityUiState.value =
                                EligibilityUiState.Success(it)
                            createOrder(createBuyOrderRequest)
                        } else {
                            createOrder(createBuyOrderRequest)
                            _eligibilityUiState.value =
                                EligibilityUiState.NotEligible("Not eligible...", listOfReq = it.requirements as List<String>)
                        }
                    }
                    .onFailure {
                        _eligibilityUiState.value =
                            EligibilityUiState.Error(
                                it.message ?: "Unknown Error"
                            )
                    }
            }catch (e: Exception){
                _eligibilityUiState.value =
                    EligibilityUiState.Error(
                        e.message ?: "Unknown Error"
                    )
            }
        }
    }

    fun createOrder(createBuyOrderRequest: CreateBuyOrderRequest) {
        viewModelScope.launch {
            _buyUiState.value =
                BuyUiState.Loading
            val request =
                BuyRequest(
                    paymentMethodId = createBuyOrderRequest.paymentMethodId,
                    crypto = createBuyOrderRequest.crypto,
                    fiat = createBuyOrderRequest.fiat,
                    fiatAmount = createBuyOrderRequest.fiatAmount,
                    walletAddress = createBuyOrderRequest.walletAddress,
                    walletAddressTag = null,
                    redirectUrl = createBuyOrderRequest.redirectUrl,
                    subPartnerId = null,
                    metadata = null,
                    externalCustomerId = null,
                    externalOrderId = null,
                    discountCode = null,
                    email = createBuyOrderRequest.email,
                )
            try {
                repository.createOrderAndShowPrimerCheckout(
                    partner = Banxa.getInstance().partner,
                    apiKey = Banxa.getInstance().apiKey,
                    request = request
                )
                    .onSuccess {
                        _buyUiState.value = BuyUiState.Success(it)
                    }
                    .onFailure {
                        _buyUiState.value =
                            BuyUiState.Error(
                                it.message ?: "Unknown Error"
                            )
                    }
            } catch (e: Exception) {
                _buyUiState.value = BuyUiState.Error(e.message ?: "Unknown Error")
            }
        }
    }
}

class BanxaViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        val repository =
            BanxaRepository(
                RetrofitClient.api1
            )

        return BanxaViewModel(repository) as T
    }
}