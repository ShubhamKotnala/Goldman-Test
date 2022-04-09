package com.goldman.test.network

import com.goldman.test.data.ApodResponse
import retrofit2.http.*

interface ApiService {
    @GET("planetary/apod")
    suspend fun getApodData(@Query("date") date: String, @Query("api_key") apiKey: String): ApodResponse
}