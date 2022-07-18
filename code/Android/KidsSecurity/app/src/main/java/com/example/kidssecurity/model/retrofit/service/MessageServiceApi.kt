package com.example.kidssecurity.model.retrofit.service

import com.example.kidssecurity.model.retrofit.RetrofitDaoImpl
import com.example.securitykids.model.entities.Message
import retrofit2.http.*

/**
This Class represent all message network service
 */
interface MessageServiceApi {

    @GET("/messages")
    suspend fun getAllMassages(): List<Message>

    @GET("/messages/{messageId}")
    suspend fun getMessageById(@Path("messageId") messageId: Long): Message

    @GET("/messages/conversation")
    suspend fun getAllConversationMessages(@Query("childId") childId: Long,@Query("parentId") ParentId: Long): List<Message>

    @POST("/messages")
    suspend fun insertMessage(@Query("content")content: String,@Query("sender") sender: String
    ,@Query("idParent") idParent: Long,@Query("idChild") idChild: Long)

    @DELETE("/messages/{messageId}")
    suspend fun deleteMessage(@Path("messageId") messageId: Long)
}

object MessageApi{
    val messageService: MessageServiceApi by lazy {
        RetrofitDaoImpl.retrofit.create(MessageServiceApi::class.java)
    }
}