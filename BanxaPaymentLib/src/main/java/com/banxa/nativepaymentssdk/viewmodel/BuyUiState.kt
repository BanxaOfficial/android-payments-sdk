package com.banxa.nativepaymentssdk.viewmodel

import com.banxa.nativepaymentssdk.data.model.BuyResponse

sealed interface BuyUiState {

    data object Idle : BuyUiState

    data object Loading : BuyUiState

    data class Success(
        val data: BuyResponse
    ) : BuyUiState

    data class NoNativeToken(
        val checkoutUrl: String
    ) : BuyUiState

    data class Error(
        val message: String
    ) : BuyUiState
}