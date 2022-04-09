package com.goldman.test.network

import com.goldman.test.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitBuilder {
    private lateinit var sOkHttpClient: OkHttpClient

    private const val BASE_URL = "https://api.nasa.gov/"

    private fun getRetrofit(): Retrofit {
    val builder: OkHttpClient.Builder =
        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)

    sOkHttpClient = OkHttpClient()

    if (BuildConfig.DEBUG) {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        builder.addInterceptor(logging)
    }
    sOkHttpClient = builder.build()

    return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(sOkHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val apiService: ApiService = getRetrofit().create(ApiService::class.java)
}