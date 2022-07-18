package com.example.kidssecurity.model.retrofit.service

import android.util.Log
import com.example.securitykids.model.entities.Location
import com.example.securitykids.model.retrofit.service.LocationApiService
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class LocationRepository(
    private val services: LocationApiService
) {

    suspend fun getChildLocation(id: Long) = flow {
            while (true){
                Log.i(TAG,"$id")
                val location = services.getChildrenLocations(id)
                emit(location)
                delay(5000L)
            }
        }

    companion object{
        const val TAG = "LocationRepository"
    }
}