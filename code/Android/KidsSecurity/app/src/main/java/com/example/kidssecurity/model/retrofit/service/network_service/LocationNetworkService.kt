package com.example.kidssecurity.model.retrofit.service.network_service

import com.example.kidssecurity.model.Response
import com.example.securitykids.model.entities.Child
import com.example.securitykids.model.entities.Location
import com.example.securitykids.model.retrofit.service.LocationApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive

class LocationNetworkService(
    private val locationApiService: LocationApiService
): INetworkService<Location> {

    override suspend fun getAll(coroutineScope: CoroutineScope): Flow<Response<List<Location>>> =
        flow {
            emit(Response.Loading())
            while (coroutineScope.isActive){
                try {
                    val list = locationApiService.getAllLocation()
                    emit(Response.Success(list))
                }catch (e: Exception){
                    emit(Response.Error(emptyList(),e.message))
                }
                delay(LOCATION_DELAY)
            }
        }

    override suspend fun getById(
        id: Long,
        coroutineScope: CoroutineScope
    ): Flow<Response<Location>> = flow {
        emit(Response.Loading())
        while (coroutineScope.isActive){
            try {
                val result = locationApiService.getLocation(id)
                emit(Response.Success(result))
            }catch (e:Exception){
                emit(Response.Error(null,e.message))
            }
            delay(LOCATION_DELAY)
        }
    }

    override suspend fun delete(entity: Location) {
        locationApiService.deleteLocation(entity.idLocation)
    }

    suspend fun addLocation(lat: Long,log: Long,idChild: Long){
        locationApiService.addLocation(lat, log, idChild)
    }

    suspend fun getChildLocation(child: Child,coroutineScope: CoroutineScope): Flow<Response<List<Location?>?>> = flow {
        emit(Response.Loading())
        while (coroutineScope.isActive && child.isTrackable){
            try {
                val list = locationApiService.getChildrenLocations(child.idChild)
                emit(Response.Success(list))
            }catch (e: Exception){
                emit(Response.Error(emptyList(),e.message))
            }
            delay(LOCATION_DELAY)
        }
    }

    companion object{
        const val LOCATION_DELAY = 5000L
    }
}