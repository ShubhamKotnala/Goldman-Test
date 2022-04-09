package com.goldman.test.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.goldman.test.BuildConfig
import com.goldman.test.data.ApodError
import com.goldman.test.data.ApodResponse
import com.goldman.test.data.MainRepository
import com.goldman.test.utils.Resource
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import retrofit2.HttpException

class MainViewModel(private val mainRepo: MainRepository) : ViewModel() {

    fun callGetApodData(date: String) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainRepo.getApodData(date, BuildConfig.API_KEY)))
        } catch (exception: Exception) {

            if (exception is HttpException) {
                val response = exception.response()
                val responseData =
                    Gson().fromJson(response?.errorBody()?.string(), ApodError::class.java)
                emit(Resource.error(data = null, message = responseData.msg))
            } else emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun addToFavorite(apodResponse: ApodResponse) = liveData(Dispatchers.IO){
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainRepo.insertFavoriteData(apodResponse)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun isDataExists(apodResponse: ApodResponse) = liveData(Dispatchers.IO){
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainRepo.isDataExists(apodResponse)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }
}