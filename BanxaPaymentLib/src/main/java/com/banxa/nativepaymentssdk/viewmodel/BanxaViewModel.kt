package com.banxa.nativepaymentssdk.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.banxa.nativepaymentssdk.core.Banxa
import com.banxa.nativepaymentssdk.core.Environment
import com.banxa.nativepaymentssdk.data.model.CreateBuyOrderRequest
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
            try {
                repository.checkEligibility(
                    createBuyOrderRequest,
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
            try {
                repository.createOrderAndShowPrimerCheckout(
                    partner = Banxa.getInstance().partner,
                    apiKey = Banxa.getInstance().apiKey,
                    request = createBuyOrderRequest
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

    fun resetEligibilityState() {
        _eligibilityUiState.value = EligibilityUiState.Idle
        _buyUiState.value = BuyUiState.Idle
    }
    fun resetBuyState() {
        _eligibilityUiState.value = EligibilityUiState.Idle
        _buyUiState.value = BuyUiState.Idle
    }
}



class BanxaViewModelFactory(
    private val baseUrl: String?,
    private val environment: Environment
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        val repository =
            BanxaRepository(
                RetrofitClient.getApi(baseUrl,environment)
            )

        return BanxaViewModel(repository) as T
    }
}