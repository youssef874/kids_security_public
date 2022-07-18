package com.example.kidssecurity.view_model.loading

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.kidssecurity.model.retrofit.RetrofitDao
import com.example.kidssecurity.view_model.create_account.CreateViewModel

class LoadingViewModelFactory(private val retrofitRepository: RetrofitDao): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoadingViewModel::class.java)) {
            return LoadingViewModel(retrofitRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}