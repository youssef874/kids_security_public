package com.example.kidssecurity.view_model.chat

import android.app.Application
import android.app.NotificationManager
import android.content.Context
import android.net.Uri
import androidx.core.content.ContextCompat
import androidx.lifecycle.*
import androidx.work.*
import com.example.kidssecurity.R
import com.example.kidssecurity.cancelNotification
import com.example.kidssecurity.getUri
import com.example.kidssecurity.model.MessageListItem
import com.example.kidssecurity.model.Response
import com.example.kidssecurity.model.retrofit.RetrofitDao
import com.example.kidssecurity.model.retrofit.entity.User
import com.example.kidssecurity.sendNotification
import com.example.kidssecurity.view_model.worker.ChatWorker
import com.example.securitykids.model.entities.Child
import com.example.securitykids.model.entities.Message
import com.example.securitykids.model.entities.Parent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class ChatViewModel(
    private val retrofitRepository: RetrofitDao
) : ViewModel() {

    //This attribute represent the current Conversation Messages fetched from the server
    private var _conversationMessages = MutableLiveData<Response<List<Message>>>()
    val conversationMessages: LiveData<Response<List<Message>>>
        get() = _conversationMessages

    //This attribute represent the current Conversation Messages transform into List<MessageListItem>
    private var _conversationMessagesListItem = MutableLiveData<List<MessageListItem?>>()
    val conversationMessagesListItem: LiveData<List<MessageListItem?>>
        get() = _conversationMessagesListItem


    /**
     * Call getAllConversationMessage of the [MessageNetworkService]
     */
    fun getConversation(childId: Long,parentId: Long){
        viewModelScope.launch {
            retrofitRepository.getMessageService().getAllConversationMessage(childId,parentId,this)
                .collect{
                    _conversationMessages.postValue(it)
                }
        }
    }

    /**
     * Convert [List<Message>] of current conversation into [List<MessageListItem>]
     * to be displayed in the ui
     */
    fun displayConversation(messages: List<Message>?,context: Context,isParent: Boolean,receiver: User){
        viewModelScope.launch{

                val list = mutableListOf<MessageListItem>()
                messages?.forEach { message: Message ->
                    var receiverUri = Uri.EMPTY
                    receiverUri = getReceiverUri(isParent, message, receiverUri, receiver, context)
                    list.add(MessageListItem(message, receiverUri))
                }
                _conversationMessagesListItem.postValue(list)
                startNotification(list,context, isParent, receiver)

        }
    }

    /**
     * Clear all the Notification an then start the notification when the user receive a new message
     */
    private fun startNotification(list: List<MessageListItem>, context: Context
                                  , isParent: Boolean, receiver: User){
        if (list.isNotEmpty()){
            if (list.size != _conversationMessagesListItem.value?.size){
                val notificationManager = clearNotification(context)
                val message = list.last().message
                if (isParent && message?.sender?.contains("c") == true ){
                    message.content?.let {
                        notificationManager.sendNotification(
                            it,
                            context.getString(R.string.chat_notification_title,(receiver as Child).nameChild),
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

    /**
     * Clear all the notification
     * @return an instance of [NotificationManager]
     */
    private fun clearNotification(context: Context): NotificationManager {
        val notificationManager = ContextCompat.getSystemService(
            context,
            NotificationManager::class.java
        ) as NotificationManager
        notificationManager.cancelNotification()
        return notificationManager
    }

    /**
     * This function will set up the requirement to start listen to the [List<Message>]
     * changes in the server and notify the user accordingly in the background
     * @param isParent: To identify if the user is [Child] or [Parent]
     * @param messages: [List<Message>] that will be keep tracking of
     * @param receiver: the other parties of the conversation
     */
    fun startWork(
        isParent: Boolean,
        messages: List<Message>?,
        receiver: User,
        parentId: Long,
        childId: Long,
        application: Application
    ) {
        val workerManager =  WorkManager.getInstance(application.applicationContext)
        val constraint = setUpConstraintsForWorker()

        val data = setDataForWorker(messages, isParent, receiver, parentId, childId)

        val work = PeriodicWorkRequestBuilder<ChatWorker>(1, TimeUnit.MINUTES)
            .setConstraints(constraint)
            .setInputData(data.build())
            .build()

        workerManager.enqueue(work)
    }

    /**
     * Set up [Constraints] for [WorkManager]
     */
    private fun setUpConstraintsForWorker() = Constraints.Builder()
        .setRequiresBatteryNotLow(true)
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()

    /**
     * Put the desired data into worker to do her work
     */
    private fun setDataForWorker(messages: List<Message>?,isParent: Boolean,receiver: User,parentId: Long,
                                 childId: Long,)
    = Data.Builder().apply {
        if (messages != null){
            putInt(MESSAGE_LIST_SIZE, messages.size)
        }
        putBoolean(IS_PARENT_WORK_MANAGER, isParent)
        putString(RECEIVER_WORK_MANAGER, receiver.name)
        putLong(PARENT_ID, parentId)
        putLong(CHILD_ID, childId)
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
        const val TAG = "ChatViewModel"
        const val RECEIVER_WORK_MANAGER = "receiver_work"
        const val IS_PARENT_WORK_MANAGER = "is_parent_work"
        const val MESSAGE_LIST_SIZE = "message_list_size"
        const val PARENT_ID = "parent_id"
        const val CHILD_ID = "child_id"
    }
}