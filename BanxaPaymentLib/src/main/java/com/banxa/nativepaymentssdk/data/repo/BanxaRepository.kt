package com.banxa.nativepaymentssdk.data.repo

import com.banxa.nativepaymentssdk.data.api.BanxaApiService
import com.banxa.nativepaymentssdk.data.model.BuyRequest
import com.banxa.nativepaymentssdk.data.model.BuyResponse
import com.banxa.nativepaymentssdk.data.model.EligibilityRequest
import com.banxa.nativepaymentssdk.data.model.EligibilityResponse
import kotlinx.coroutines.delay


class BanxaRepository(
    private val api: BanxaApiService
) {
    suspend fun checkEligibility(
        request: EligibilityRequest,
        partner: String,
        apiKey: String
    ): Result<EligibilityResponse> {
        return try {
            val response =
                api.checkEligibility(
                    partner = partner,
                    apiKey = apiKey,
                    request = request
                )

            if (response.isSuccessful) {
                Result.success(
                    response.body()!!
                )

            } else {

                Result.failure(
                    Exception("Error ${response.code()}")
                )
            }

        } catch (e: Exception) {

            Result.failure(e)
        }
    }

    suspend fun createOrderAndShowPrimerCheckout(
        partner: String,
        apiKey: String,
        request: BuyRequest
    ): Result<BuyResponse> {
        return try {
            val response =
                api.createBuyOrder(
                    partner = partner,
                    apiKey = apiKey,
                    request = request
                )
            if (response.isSuccessful) {
                Result.success(
                    response.body()!!
                )
            } else {
                Result.failure(
                    Exception(
                        "API Error ${response.code()}"
                    )
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}