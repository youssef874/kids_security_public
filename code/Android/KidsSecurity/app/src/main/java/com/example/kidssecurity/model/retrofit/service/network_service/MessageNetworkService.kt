package com.example.kidssecurity.model.retrofit.service.network_service

import com.example.kidssecurity.model.Response
import com.example.kidssecurity.model.retrofit.service.MessageServiceApi
import com.example.securitykids.model.entities.Child
import com.example.securitykids.model.entities.Message
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive

class MessageNetworkService(
    private val messageServiceApi: MessageServiceApi
): INetworkService<Message> {

    override suspend fun getAll(coroutineScope: CoroutineScope): Flow<Response<List<Message>>> = flow {
        emit(Response.Loading())
        while (coroutineScope.isActive){
            try {
                val list = messageServiceApi.getAllMassages()
                emit(Response.Success(list))
            }catch (e: Exception){
                emit(Response.Error(emptyList(),e.message))
            }
            delay(MESSAGE_DELAY)
        }
    }

    override suspend fun getById(id: Long,coroutineScope: CoroutineScope): Flow<Response<Message>> = flow {
        emit(Response.Loading())
        while (coroutineScope.isActive){
            try {
                val result = messageServiceApi.getMessageById(id)
                emit(Response.Success(result))
            }catch (e: Exception){
                emit(Response.Error(null,e.message))
            }
            delay(MESSAGE_DELAY)
        }
    }

    override suspend fun delete(entity: Message) {
        entity.idMessage?.let { messageServiceApi.deleteMessage(it) }
    }

    suspend fun addMessage(content: String,sender: String,idParent: Long,idChild: Long){
        messageServiceApi.insertMessage(content,sender,idParent,idChild)
    }

    suspend fun getAllConversationMessage(childId: Long,parentId: Long,coroutineScope: CoroutineScope)
    : Flow<Response<List<Message>>> = flow {
        emit(Response.Loading())
        while (coroutineScope.isActive){
            try {
                val list = messageServiceApi.getAllConversationMessages(childId,parentId)
                emit(Response.Success(list))
            }catch (e: Exception){
                emit(Response.Error(emptyList(),e.message))
            }
            delay(MESSAGE_DELAY)
        }
    }

    companion object{
        const val MESSAGE_DELAY = 2000L
    }
}