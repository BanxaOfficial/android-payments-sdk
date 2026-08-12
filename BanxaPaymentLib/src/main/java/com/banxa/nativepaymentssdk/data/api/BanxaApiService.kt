package com.banxa.nativepaymentssdk.data.api

import com.banxa.nativepaymentssdk.data.model.BuyResponse
import com.banxa.nativepaymentssdk.data.model.Country
import com.banxa.nativepaymentssdk.data.model.CreateOrderRequest
import com.banxa.nativepaymentssdk.data.model.Crypto
import com.banxa.nativepaymentssdk.data.model.EligibilityResponse
import com.banxa.nativepaymentssdk.data.model.Fiat
import com.banxa.nativepaymentssdk.data.model.PaymentMethod
import com.banxa.nativepaymentssdk.data.model.Quote
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface BanxaApiService {

    @POST("{partner}/v2/eligibility")
    suspend fun checkEligibility(
        @Path("partner") partner: String,
        @Header("x-api-key") apiKey: String,
        @Body request: CreateOrderRequest
    ): Response<EligibilityResponse>

    @POST("{partner}/v2/buy")
    suspend fun createBuyOrder(
        @Path("partner") partner: String,
        @Header("x-api-key") apiKey: String,
        @Body request: CreateOrderRequest
    ): Response<BuyResponse>

    @GET("{partner}/v2/payment-methods/{orderType}")
    suspend fun getPaymentMethods(
        @Path("partner") partner: String,
        @Path("orderType") orderType: String,
        @Header("x-api-key") apiKey: String,
        @Query("fiat") fiat: String? = null
    ): Response<List<PaymentMethod>>

    @GET("{partner}/v2/countries")
    suspend fun getCountries(
        @Path("partner") partner: String,
        @Header("x-api-key") apiKey: String
    ): Response<List<Country>>

    @GET("{partner}/v2/fiats/{orderType}")
    suspend fun getFiats(
        @Path("partner") partner: String,
        @Path("orderType") orderType: String,
        @Header("x-api-key") apiKey: String
    ): Response<List<Fiat>>

    @GET("{partner}/v2/crypto/{orderType}")
    suspend fun getCrypto(
        @Path("partner") partner: String,
        @Path("orderType") orderType: String,
        @Header("x-api-key") apiKey: String
    ): Response<List<Crypto>>

    @GET("{partner}/v2/quotes/{orderType}")
    suspend fun getQuote(
        @Path("partner") partner: String,
        @Path("orderType") orderType: String,
        @Header("x-api-key") apiKey: String,
        @Query("fiat") fiat: String,
        @Query("crypto") crypto: String,
        @Query("blockchain") blockchain: String,
        @Query("paymentMethodId") paymentMethodId: String,
        @Query("fiatAmount") fiatAmount: String? = null,
        @Query("cryptoAmount") cryptoAmount: String? = null,
        @Query("externalCustomerId") externalCustomerId: String? = null,
        @Query("ipAddress") ipAddress: String? = null,
        @Query("discountCode") discountCode: String? = null
    ): Response<Quote>

}