package com.example.securitykids.model.retrofit.service

import com.example.kidssecurity.model.retrofit.RetrofitDaoImpl
import com.example.securitykids.model.entities.Location
import retrofit2.http.*


/**
This Class represent all location network service
 */
interface LocationApiService {

    @GET("locations")
    suspend fun getAllLocation(): List<Location>

    @GET("locations/{id}")
    suspend fun getLocation(@Path("id") id: Long): Location

    @GET("locations/children/{childId}")
    suspend fun getChildrenLocations(@Path("childId") childId: Long): List<Location?>?

    @POST("locations")
    suspend fun addLocation(@Query("lat") lat: Long, @Query("log") log:Long,@Query("idChild") idChild: Long)

    @DELETE("locations/{id}")
    suspend fun deleteLocation(@Path("id") id: Long)

    @DELETE("locations/children/{childId}")
    suspend fun deleteAllChildrenLocation(childId: Long)

    @PUT("locations")
    suspend fun updateLocation(@Body location: Location)
}
object LocationApi{
    val locationService: LocationApiService by lazy {
        RetrofitDaoImpl.retrofit.create(LocationApiService::class.java)
    }
}

