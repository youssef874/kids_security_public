package com.example.kidssecurity.model.retrofit

import com.example.kidssecurity.model.retrofit.service.ImageApi
import com.example.kidssecurity.model.retrofit.service.ImageServiceApi
import com.example.kidssecurity.model.retrofit.service.MessageApi
import com.example.kidssecurity.model.retrofit.service.MessageServiceApi
import com.example.kidssecurity.model.retrofit.service.network_service.LocationNetworkService
import com.example.kidssecurity.model.retrofit.service.network_service.MessageNetworkService
import com.example.securitykids.model.retrofit.ParentApi
import com.example.securitykids.model.retrofit.ParentApiService
import com.example.securitykids.model.retrofit.service.ChildApi
import com.example.securitykids.model.retrofit.service.ChildApiService
import com.example.securitykids.model.retrofit.service.LocationApi
import com.example.securitykids.model.retrofit.service.LocationApiService
import com.google.gson.Gson
import okhttp3.OkHttpClient

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val BASE_URL = "http://your_ip_address:8080"

/**
This class hold all network services for this app
 */
class RetrofitDaoImpl: RetrofitDao {

    override fun getChildService(): ChildApiService {
        return ChildApi.childService
    }

    override fun getLocationService(): LocationNetworkService {
        return LocationNetworkService((LocationApi.locationService))
    }


    override fun getParentService(): ParentApiService {
        return ParentApi.parentService
    }

    override fun getImageService(): ImageServiceApi {
        return ImageApi.imageService
    }

    override fun getMessageService(): MessageNetworkService {
        return MessageNetworkService(MessageApi.messageService)
    }


    companion object{

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create( Gson()))
            .build()

        val retrofitWithoutConverter = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .build()

    }

}