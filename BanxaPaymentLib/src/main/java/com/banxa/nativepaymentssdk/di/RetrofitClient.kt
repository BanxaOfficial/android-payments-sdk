package com.banxa.nativepaymentssdk.di

import com.banxa.nativepaymentssdk.data.api.BanxaApiService
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.ResponseBody.Companion.toResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private const val BASE_URL =  "https://api.banxa-sandbox.com/"
    private val loggingInterceptor =
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

    private val okHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor { chain ->
                val response = chain.proceed(chain.request())
                val body = response.body
                if (body != null) {
                    val content = body.string()
                    // Replace Non-Breaking Spaces with regular spaces to avoid MalformedJsonException
                    val sanitizedContent = content.replace('\u00A0', ' ')
                    val sanitizedBody = sanitizedContent.toResponseBody(body.contentType())
                    response.newBuilder().body(sanitizedBody).build()
                } else {
                    response
                }
            }
            .connectTimeout(60, TimeUnit.SECONDS)  // connection timeout
            .readTimeout(60, TimeUnit.SECONDS)     // read timeout
            .writeTimeout(60, TimeUnit.SECONDS)    // write timeout
            .build()
    val gson = GsonBuilder().setLenient().create()
    val api1: BanxaApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(
                GsonConverterFactory.create(gson)
            )
            .build()
            .create(BanxaApiService::class.java)
    }
}