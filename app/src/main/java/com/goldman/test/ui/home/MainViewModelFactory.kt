package com.goldman.test.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.goldman.test.data.ApodDao
import com.goldman.test.data.MainRepository
import com.goldman.test.network.ApiService

/**
 * ViewModel provider factory to instantiate LoginViewModel.
 * Required given LoginViewModel has a non-empty constructor
 */
class MainViewModelFactory(private val apiHelper: ApiService, private val dao : ApodDao) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(mainRepo = MainRepository(apiHelper, dao)) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}