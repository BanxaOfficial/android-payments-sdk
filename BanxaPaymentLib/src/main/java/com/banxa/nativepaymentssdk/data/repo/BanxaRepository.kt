package com.banxa.nativepaymentssdk.data.repo

import android.util.Log
import com.banxa.nativepaymentssdk.data.api.BanxaApiService
import com.banxa.nativepaymentssdk.data.model.BuyResponse
import com.banxa.nativepaymentssdk.data.model.CreateBuyOrderRequest
import com.banxa.nativepaymentssdk.data.model.EligibilityResponse

class BanxaRepository(
    private val api: BanxaApiService
) {
    suspend fun checkEligibility(
        request1: CreateBuyOrderRequest,
        partner: String,
        apiKey: String
    ): Result<EligibilityResponse> {
        return try {
            val response =
                api.checkEligibility(
                    partner = partner,
                    apiKey = apiKey,
                    request = request1
                )

            if (response.isSuccessful) {
                Result.success(
                    response.body()!!
                )

            } else {
                Result.failure(
                    Exception("${response.message()}")
                )
            }

        } catch (e: Exception) {
            Log.i("BPS99","catch : ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun createOrderAndShowPrimerCheckout(
        partner: String,
        apiKey: String,
        request: CreateBuyOrderRequest
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