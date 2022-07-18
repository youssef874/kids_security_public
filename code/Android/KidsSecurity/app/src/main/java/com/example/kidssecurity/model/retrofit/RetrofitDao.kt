package com.example.kidssecurity.model.retrofit

import com.example.kidssecurity.model.retrofit.service.ImageServiceApi
import com.example.kidssecurity.model.retrofit.service.MessageServiceApi
import com.example.kidssecurity.model.retrofit.service.network_service.LocationNetworkService
import com.example.kidssecurity.model.retrofit.service.network_service.MessageNetworkService
import com.example.securitykids.model.retrofit.ParentApiService
import com.example.securitykids.model.retrofit.service.ChildApiService
import com.example.securitykids.model.retrofit.service.LocationApiService


interface RetrofitDao {

    fun getChildService(): ChildApiService

    fun getLocationService(): LocationNetworkService


    fun getParentService(): ParentApiService

    fun getImageService(): ImageServiceApi

    fun getMessageService(): MessageNetworkService


}