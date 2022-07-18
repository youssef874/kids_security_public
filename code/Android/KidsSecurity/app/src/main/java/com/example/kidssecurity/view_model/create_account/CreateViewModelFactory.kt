package com.example.kidssecurity.view_model.create_account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.kidssecurity.model.retrofit.RetrofitDao
import com.example.kidssecurity.view_model.login.LoginViewModel

class CreateViewModelFactory(private val retrofitRepository: RetrofitDao): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CreateViewModel::class.java)) {
            return CreateViewModel(retrofitRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}