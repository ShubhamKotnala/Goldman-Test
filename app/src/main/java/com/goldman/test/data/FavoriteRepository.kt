package com.goldman.test.data

import com.goldman.test.network.ApiService

class FavoriteRepository(val dataSource: ApiService, val dao: ApodDao) {

    suspend fun getApodData(): List<ApodResponse> {
            return dao.getApodData()
    }

    suspend fun deleteItem(response: ApodResponse) {
        return dao.deleteItem(response.date)
    }
}