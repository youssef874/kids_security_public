package com.example.securitykids.model.retrofit

import com.example.kidssecurity.model.Response
import com.example.kidssecurity.model.retrofit.RetrofitDaoImpl
import com.example.securitykids.model.entities.Parent
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.http.*

/**
This Class represent all parent network service
 */
interface ParentApiService {

    @GET("parents/{id}")
    suspend fun getParent(@Path("id") id: Long): Parent

    @GET("parents/name?")
    suspend fun getAuthenticatedParent(@Query("name") name: String,@Query("psw") psw: String): Parent

    @GET("parents/child/{childId}")
    suspend fun getParentChild(@Path("childId") childId: Long): Parent

    @Multipart
    @POST("/parents")
    suspend fun addParent(@Part("parent") parent: Parent, @Part filePart: MultipartBody.Part) : Parent

    @DELETE("/parents/{id}")
    suspend fun deleteParent(@Path("id") id: Long)

    @Multipart
    @PUT("/parents")
    suspend fun updateParent(@Part("parent") parent: Parent, @Part image: MultipartBody.Part)


}

object ParentApi{

    val parentService: ParentApiService by lazy {
        RetrofitDaoImpl.retrofit.create(ParentApiService::class.java)
    }
}