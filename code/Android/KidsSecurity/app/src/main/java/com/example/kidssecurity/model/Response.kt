package com.example.kidssecurity.model

/**
This class represent data service represent
 */
sealed class Response<T>(
    val data: T? = null,
    val message: String? = null
) {

    //If the data still fetched successfully
     class Success<T>(data: T?): Response<T>(data)

    //If an error occur
    class Error<T>(data: T?,message: String?): Response<T>(data, message)

    //If data fetching still in process
    class Loading<T>: Response<T>()
}