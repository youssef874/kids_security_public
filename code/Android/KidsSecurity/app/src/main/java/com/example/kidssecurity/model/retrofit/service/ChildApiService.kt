package com.example.securitykids.model.retrofit.service

import com.example.kidssecurity.model.retrofit.RetrofitDaoImpl
import com.example.securitykids.model.entities.Child
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.http.*


/**
This Class represent all child network service
 */
interface ChildApiService{

    @GET("children")
    suspend fun getAllChildren(): List<Child>

    @GET("children/{id}")
    suspend fun getChild(@Path("id") id: Long): Child

    @GET("children/name?")
    suspend fun getAuthenticatedChild(@Query("name") name: String,@Query("psw") psw: String): Child

    @Multipart
    @POST("/children/{parentId}")
    suspend fun addChild(@Part("child") child: Child, @Part filePart: MultipartBody.Part,
    @Path("parentId")parentId: Long)

    @DELETE("children/{id}")
    suspend fun deleteChild(@Path("id") id: Long)

    @DELETE("children/parent/{parentId}")
    suspend fun deleteAllParentChildren(@Path("parentId") parentId: Long)

}

object ChildApi{

    val childService: ChildApiService by lazy {
        RetrofitDaoImpl.retrofit.create(ChildApiService::class.java)
    }

}


