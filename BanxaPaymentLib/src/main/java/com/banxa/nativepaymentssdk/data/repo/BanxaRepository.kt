package com.banxa.nativepaymentssdk.data.repo

import com.banxa.nativepaymentssdk.data.api.BanxaApiService
import com.banxa.nativepaymentssdk.data.model.BuyResponse
import com.banxa.nativepaymentssdk.data.model.CreateOrderRequest
import com.banxa.nativepaymentssdk.data.model.EligibilityResponse
import com.banxa.nativepaymentssdk.data.model.ErrorResponse
import com.google.gson.Gson

class BanxaRepository(
    private val api: BanxaApiService
) {
    suspend fun checkEligibility(
        request1: CreateOrderRequest,
        partner: String,
        apiKey: String
    ): Result<EligibilityResponse> {
        var statusCode = 0
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
                var errorBody = response.errorBody()?.string()
                var errorMsg = ""
                val gson = Gson()
                response.errorBody()?.let { error ->
                    val errorResponse = gson.fromJson(errorBody, ErrorResponse::class.java)
                    errorMsg = errorResponse.message.toString()
                    errorResponse.errors?.forEach { (key, value) ->
                        errorMsg = value.joinToString()
                    }
                }
                statusCode = response.code()
                Result.failure(
                    Exception("Validation error : $statusCode - $errorMsg")
                )
            }

        } catch (e: Exception) {
            Result.failure(
                Exception("Validation error : 500 - Server Error")
            )
        }
    }

    suspend fun createOrderAndShowPrimerCheckout(
        partner: String,
        apiKey: String,
        request: CreateOrderRequest
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
                        "${response.code()}"
                    )
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}