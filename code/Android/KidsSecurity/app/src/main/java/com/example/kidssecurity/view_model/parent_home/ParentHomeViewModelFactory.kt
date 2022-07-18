package com.example.kidssecurity.view_model.parent_home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.kidssecurity.model.retrofit.RetrofitDao
import com.example.kidssecurity.view_model.loading.LoadingViewModel

class ParentHomeViewModelFactory(private val retrofitRepository: RetrofitDao): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ParentHomeViewModel::class.java)) {
            return ParentHomeViewModel(retrofitRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}