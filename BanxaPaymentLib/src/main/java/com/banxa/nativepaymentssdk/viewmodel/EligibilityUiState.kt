package com.banxa.nativepaymentssdk.viewmodel

import com.banxa.nativepaymentssdk.data.model.EligibilityResponse

sealed interface EligibilityUiState {

    data object Idle : EligibilityUiState

    data object Loading : EligibilityUiState

    data class Success(
        val data: EligibilityResponse
    ) : EligibilityUiState

    data class NotEligible(
        val result: String,
        val listOfReq: List<String>
    ) : EligibilityUiState

    data class Error(
        val message: String
    ) : EligibilityUiState
}