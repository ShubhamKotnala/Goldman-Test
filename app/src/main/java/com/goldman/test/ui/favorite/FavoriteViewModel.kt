package com.goldman.test.ui.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.goldman.test.data.ApodResponse
import com.goldman.test.data.FavoriteRepository
import com.goldman.test.utils.Resource
import kotlinx.coroutines.Dispatchers

class FavoriteViewModel(private val homeRepository: FavoriteRepository) : ViewModel() {

    fun getFavoriteListData() = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = homeRepository.getApodData()))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun deleteFavoriteItem(response: ApodResponse) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = homeRepository.deleteItem(response)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }
}