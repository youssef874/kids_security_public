package com.example.kidssecurity.model.retrofit.service

import com.example.kidssecurity.model.retrofit.RetrofitDaoImpl
import com.example.securitykids.model.entities.Child
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.http.*

interface ImageServiceApi {

    @Streaming
    @GET("/children/image/{childId}")
    suspend fun getChildImage(@Path("childId") childId: Long): ResponseBody

    @GET("parents/image/{parentId}")
    suspend fun getParentImage(@Path("parentId") parentId: Long):ResponseBody


}

object ImageApi {
    val imageService: ImageServiceApi by lazy {
        RetrofitDaoImpl.retrofitWithoutConverter.create(ImageServiceApi::class.java)
    }
}