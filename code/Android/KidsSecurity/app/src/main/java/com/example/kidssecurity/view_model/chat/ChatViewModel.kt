package com.example.kidssecurity.view_model.chat

import android.app.NotificationManager
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.*
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import com.example.kidssecurity.cancelNotification
import com.example.kidssecurity.getUri
import com.example.kidssecurity.model.MessageListItem
import com.example.kidssecurity.model.Response
import com.example.kidssecurity.model.retrofit.RetrofitDao
import com.example.kidssecurity.model.retrofit.entity.User
import com.example.kidssecurity.sendNotification
import com.example.securitykids.model.entities.Child
import com.example.securitykids.model.entities.Message
import com.example.securitykids.model.entities.Parent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class ChatViewModel(
    private val retrofitRepository: RetrofitDao
) : ViewModel() {

    //private val workerManager = WorkManager.getInstance(app.applicationContext)

    private var _ConversationMessages = MutableLiveData<Response<List<Message>>>()
    val ConversationMessages: LiveData<Response<List<Message>>>
        get() = _ConversationMessages

    private var _conversationMessagesListItem = MutableLiveData<List<MessageListItem?>>()
    val conversationMessagesListItem: LiveData<List<MessageListItem?>>
        get() = _conversationMessagesListItem


    fun getConversation(childId: Long,parentId: Long){
        viewModelScope.launch {
            retrofitRepository.getMessageService().getAllConversationMessage(childId,parentId,this)
                .collect{
                    _ConversationMessages.postValue(it)
                }
        }
    }

    fun displayConversation(messages: List<Message>?,context: Context,isParent: Boolean,receiver: User){
        viewModelScope.launch{

            Log.i(TAG,"before added ${conversationMessagesListItem.value}")
                val list = mutableListOf<MessageListItem>()
                messages?.forEach { message: Message ->
                    var receiverUri = Uri.EMPTY
                    receiverUri = getReceiverUri(isParent, message, receiverUri, receiver, context)
                    list.add(MessageListItem(message, receiverUri))
                    Log.i(TAG,"list to added:${list.size}")
                }
                _conversationMessagesListItem.postValue(list)
                startNotification(list,context, isParent, receiver)
                Log.i(TAG,"list added:${conversationMessagesListItem.value?.size}")

        }
    }

    private fun startNotification(list: List<MessageListItem>, context: Context, isParent: Boolean, receiver: User){
        if (list.isNotEmpty()){
            if (list.size != _conversationMessagesListItem.value?.size){
                Log.i(TAG,"start notification size check${list.size}")
                val notificationManager = ContextCompat.getSystemService(
                    context,
                    NotificationManager::class.java
                ) as NotificationManager
                notificationManager.cancelNotification()
                val message = list.last().message
                if (isParent && message?.sender?.contains("c") == true ){
                    message.content?.let {
                        notificationManager.sendNotification(
                            it,
                            (receiver as Child).nameChild,
                            context
                            )
                    }
                }
                else if (!isParent && message?.sender?.contains("p") == true){
                    message.content?.let {
                        notificationManager.sendNotification(
                            it,
                            (receiver as Parent).nameParent,
                            context
                        )
                    }
                }
            }
        }
    }

    private fun startWork(message: String,name: String){
        val constraint = Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val data = Data.Builder()
        data.putString(NAME,name)
        data.putString(MESSAGE,message)

        
    }

    private suspend fun getReceiverUri(
        isParent: Boolean,
        message: Message,
        receiverUri: Uri?,
        receiver: User,
        context: Context
    ): Uri? {
        var receiverUri1 = receiverUri
        if (isParent) {
            if (message.sender.equals("c")) {
                receiverUri1 = getUri(receiver, false, context,Dispatchers.IO)
            }
        } else {
            if (message.sender.equals("p")) {
                receiverUri1 = getUri(receiver, true, context,Dispatchers.IO)
            }
        }
        return receiverUri1
    }

    fun createMessage(content: String,sender: String, idChild: Long,idParent: Long) {
        viewModelScope.launch {
            try {
                retrofitRepository.getMessageService().addMessage(content, sender, idParent, idChild)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    companion object {
        const val MESSAGES_DELAY = 1000L
        const val TAG = "ChatViewModel"
        const val NAME = "name"
        const val MESSAGE = "message"
    }
}