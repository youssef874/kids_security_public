package com.example.kidssecurity.view_model.child_home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.kidssecurity.model.retrofit.RetrofitDao

class ChildHomeViewModelFactory(private val retrofitRepository: RetrofitDao): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChildHomeViewModel::class.java)) {
            return ChildHomeViewModel(retrofitRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}