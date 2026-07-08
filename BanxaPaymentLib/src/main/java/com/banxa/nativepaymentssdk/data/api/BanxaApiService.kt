package com.banxa.nativepaymentssdk.data.api

import com.banxa.nativepaymentssdk.data.model.BuyResponse
import com.banxa.nativepaymentssdk.data.model.CreateBuyOrderRequest
import com.banxa.nativepaymentssdk.data.model.EligibilityResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface BanxaApiService {

    @POST("{partner}/v2/eligibility")
    suspend fun checkEligibility(
        @Path("partner") partner: String,
        @Header("x-api-key") apiKey: String,
        @Body request: CreateBuyOrderRequest
    ): Response<EligibilityResponse>

    @POST("{partner}/v2/buy")
    suspend fun createBuyOrder(
        @Path("partner") partner: String,
        @Header("x-api-key") apiKey: String,
        @Body request: CreateBuyOrderRequest
    ): Response<BuyResponse>

}