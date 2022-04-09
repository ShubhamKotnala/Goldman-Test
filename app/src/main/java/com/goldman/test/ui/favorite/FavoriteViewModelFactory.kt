package com.goldman.test.ui.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.goldman.test.data.ApodDao
import com.goldman.test.data.FavoriteRepository
import com.goldman.test.network.ApiService

class FavoriteViewModelFactory(private val apiHelper: ApiService, private val dao: ApodDao) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FavoriteViewModel::class.java)) {
            return FavoriteViewModel(homeRepository = FavoriteRepository(apiHelper, dao)) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}