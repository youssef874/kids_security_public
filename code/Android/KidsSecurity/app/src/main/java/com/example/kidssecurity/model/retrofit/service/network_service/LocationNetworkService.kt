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

/**
 * This class will the intermediate between [LocationApiService] an [RetrofitDao]
 * and will get a stream of data from the server to ensure Real time data
 */
class LocationNetworkService(
    private val locationApiService: LocationApiService
): INetworkService<Location> {

    /**
     * This function get all Location in the server
     */
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

    /**
     * This function get the Location in the server by it's id
     */
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

    /**
     * This function will delete Location from the server
     */
    override suspend fun delete(entity: Location) {
        locationApiService.deleteLocation(entity.idLocation)
    }

    /**
     * This function will insert a new Location to the server
     * @param lat: The location latitude
     * @param log: The location longitude
     * @param idChild: The child id who his location will be added
     */
    suspend fun addLocation(lat: Long,log: Long,idChild: Long){
        locationApiService.addLocation(lat, log, idChild)
    }

    /**
     * This function get a [Child] locations in the server
     */
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