package com.example.kidssecurity.model

import android.util.Log
import com.example.kidssecurity.model.retrofit.RetrofitDaoImpl
import com.example.securitykids.model.entities.Location
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.isActive

/**
This class hold the data through network and resource internal resource
 */
class Repository {

    companion object{
        val retrofitRepository = RetrofitDaoImpl()
    }
}