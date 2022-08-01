package com.example.kidssecurity.model.retrofit.service.network_service

import com.example.kidssecurity.model.Response
import com.example.kidssecurity.model.retrofit.service.MessageServiceApi
import com.example.securitykids.model.entities.Child
import com.example.securitykids.model.entities.Message
import com.example.securitykids.model.retrofit.service.LocationApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive

/**
 * This class will the intermediate between [MessageServiceApi] an [RetrofitDao]
 * and will get a stream of data from the server to ensure Real time data
 */
class MessageNetworkService(
    private val messageServiceApi: MessageServiceApi
): INetworkService<Message> {

    /**
     * This function get all [Message] in the server
     */
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


    /**
     * This function get the [Message] in the server by it's id
     */
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

    /**
     * This function will delete [Message] from the server
     */
    override suspend fun delete(entity: Message) {
        entity.idMessage?.let { messageServiceApi.deleteMessage(it) }
    }

    /**
     * This function will insert a new [Message] to the server
     * @param content: The message content
     * @param sender: The message sender
     * @param idChild: The child id who is one of the message parties
     * @param idParent: The parent id who is one of the message parties
     */
    suspend fun addMessage(content: String,sender: String,idParent: Long,idChild: Long){
        messageServiceApi.insertMessage(content,sender,idParent,idChild)
    }

    /**
     * Get All the current conversation messages
     * @param childId: The child id who is one of the conversation parties
     * @param parentId: The parent id who is one of the conversation parties
     */
    suspend fun getAllConversationMessage(childId: Long,parentId: Long
                                          ,coroutineScope: CoroutineScope= CoroutineScope(Dispatchers.IO))
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