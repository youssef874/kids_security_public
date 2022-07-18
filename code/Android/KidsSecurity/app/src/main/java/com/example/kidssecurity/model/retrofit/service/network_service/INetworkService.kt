package com.example.kidssecurity.model.retrofit.service.network_service

import com.example.kidssecurity.model.Response
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

interface INetworkService<T> {

    suspend fun getAll(coroutineScope: CoroutineScope): Flow<Response<List<T>>>

    suspend fun getById(id: Long,coroutineScope: CoroutineScope): Flow<Response<T>>

    suspend fun delete(entity: T)

}