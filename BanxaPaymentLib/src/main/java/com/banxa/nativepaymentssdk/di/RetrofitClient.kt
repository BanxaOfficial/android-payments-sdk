package com.banxa.nativepaymentssdk.di

import android.util.Log
import com.banxa.nativepaymentssdk.core.Environment
import com.banxa.nativepaymentssdk.data.api.BanxaApiService
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.ResponseBody.Companion.toResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
/*
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
}*/
object RetrofitClient {

    private fun getClient(baseUrl: String): BanxaApiService {

        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor { chain ->
                val response = chain.proceed(chain.request())
                val body = response.body
                if (body != null) {
                    val content = body.string()
                    val sanitizedContent = content.replace('\u00A0', ' ')
                    val sanitizedBody = sanitizedContent.toResponseBody(body.contentType())
                    response.newBuilder().body(sanitizedBody).build()
                } else {
                    response
                }
            }
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()

        val gson = GsonBuilder().setLenient().create()

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(BanxaApiService::class.java)
    }

    fun getApi(bUrl: String?, environment: Environment): BanxaApiService {
         val baseUrl: String = when {
             bUrl != null -> bUrl.trimEnd('/') + "/"
            else -> when (environment) {
                Environment.LOCAL      -> "https://papi.test/"
                Environment.SANDBOX    -> "https://api.banxa-sandbox.com/"
                Environment.PREPROD    -> "https://api.banxa-preprod.com/"
                Environment.PRODUCTION -> "https://api.banxa.com/"
            }
        }
        Log.i("BPS", "Base URL : $baseUrl")
        return getClient(baseUrl)
    }
}
