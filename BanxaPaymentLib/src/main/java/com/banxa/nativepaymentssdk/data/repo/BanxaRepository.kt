package com.banxa.nativepaymentssdk.data.repo

import com.banxa.nativepaymentssdk.data.api.BanxaApiService
import com.banxa.nativepaymentssdk.data.model.BuyResponse
import com.banxa.nativepaymentssdk.data.model.Country
import com.banxa.nativepaymentssdk.data.model.CreateKycSessionRequest
import com.banxa.nativepaymentssdk.data.model.CreateOrderRequest
import com.banxa.nativepaymentssdk.data.model.Crypto
import com.banxa.nativepaymentssdk.data.model.EligibilityResponse
import com.banxa.nativepaymentssdk.data.model.ErrorResponse
import com.banxa.nativepaymentssdk.data.model.Fiat
import com.banxa.nativepaymentssdk.data.model.KycSession
import com.banxa.nativepaymentssdk.data.model.PaymentMethod
import com.banxa.nativepaymentssdk.data.model.Quote
import com.banxa.nativepaymentssdk.data.model.QuoteRequest
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

    suspend fun getPaymentMethods(
        partner: String,
        apiKey: String,
        orderType: String = "buy",
        fiat: String? = null
    ): Result<List<PaymentMethod>> {
        return try {
            val response =
                api.getPaymentMethods(
                    partner = partner,
                    orderType = orderType,
                    apiKey = apiKey,
                    fiat = fiat
                )
            if (response.isSuccessful) {
                Result.success(response.body().orEmpty())
            } else {
                Result.failure(Exception("${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getCountries(
        partner: String,
        apiKey: String
    ): Result<List<Country>> {
        return try {
            val response =
                api.getCountries(
                    partner = partner,
                    apiKey = apiKey
                )
            if (response.isSuccessful) {
                Result.success(response.body().orEmpty())
            } else {
                Result.failure(Exception("${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getFiats(
        partner: String,
        apiKey: String,
        orderType: String = "buy"
    ): Result<List<Fiat>> {
        return try {
            val response =
                api.getFiats(
                    partner = partner,
                    orderType = orderType,
                    apiKey = apiKey
                )
            if (response.isSuccessful) {
                Result.success(response.body().orEmpty())
            } else {
                Result.failure(Exception("${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getCrypto(
        partner: String,
        apiKey: String,
        orderType: String = "buy"
    ): Result<List<Crypto>> {
        return try {
            val response =
                api.getCrypto(
                    partner = partner,
                    orderType = orderType,
                    apiKey = apiKey
                )
            if (response.isSuccessful) {
                Result.success(response.body().orEmpty())
            } else {
                Result.failure(Exception("${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getQuote(
        partner: String,
        apiKey: String,
        orderType: String = "buy",
        request: QuoteRequest
    ): Result<Quote> {
        return try {
            val response =
                api.getQuote(
                    partner = partner,
                    orderType = orderType,
                    apiKey = apiKey,
                    fiat = request.fiat,
                    crypto = request.crypto,
                    blockchain = request.blockchain,
                    paymentMethodId = request.paymentMethodId,
                    fiatAmount = request.fiatAmount,
                    cryptoAmount = request.cryptoAmount,
                    externalCustomerId = request.externalCustomerId,
                    ipAddress = request.ipAddress,
                    discountCode = request.discountCode
                )
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Result.success(body)
                } else {
                    Result.failure(Exception("Empty quote response"))
                }
            } else {
                Result.failure(Exception("${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Creates a hosted KYC session, so a customer can be verified before any order exists.
     *
     * @param partner
     * @param apiKey
     * @param request
     * @return
     */
    suspend fun createKycSession(
        partner: String,
        apiKey: String,
        request: CreateKycSessionRequest
    ): Result<KycSession> {
        return try {
            val response =
                api.createKycSession(
                    partner = partner,
                    apiKey = apiKey,
                    request = request
                )
            if (response.isSuccessful) {
                val body = response.body()
                // Gson fills a missing field with null even though the type says otherwise.
                val redirectUrl: String? = body?.redirectUrl
                if (body == null || redirectUrl.isNullOrBlank()) {
                    Result.failure(Exception("Empty KYC session response"))
                } else {
                    Result.success(body)
                }
            } else {
                Result.failure(Exception("${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
