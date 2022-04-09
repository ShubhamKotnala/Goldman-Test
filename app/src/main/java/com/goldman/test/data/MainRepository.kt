package com.goldman.test.data

import com.goldman.test.network.ApiService

class MainRepository(val apiService: ApiService, val dao : ApodDao) {
    suspend fun getApodData(date: String, apiKey: String) : ApodResponse {
        return apiService.getApodData(date, apiKey)
    }

    suspend fun insertFavoriteData(apodResponse: ApodResponse) {
        return dao.insert(apodResponse)
    }

    suspend fun isDataExists(apodResponse: ApodResponse) : Boolean {
        return dao.isDataExists(apodResponse.date)
    }
}