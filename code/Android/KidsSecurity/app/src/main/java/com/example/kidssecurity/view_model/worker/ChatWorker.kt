package com.example.kidssecurity.view_model.worker

import android.app.NotificationManager
import android.content.Context
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.kidssecurity.R
import com.example.kidssecurity.cancelNotification
import com.example.kidssecurity.model.Repository
import com.example.kidssecurity.model.Response
import com.example.kidssecurity.sendNotification
import com.example.kidssecurity.view_model.chat.ChatViewModel
import com.example.securitykids.model.entities.Message


class ChatWorker(
    context: Context,
    workerParameters: WorkerParameters
): CoroutineWorker(context,workerParameters) {
    /**
     * This function which will be executed in the background.
     * get all the data that added in [ChatViewModel], then get new fetched conversation messages
     * and then compare the from the old conversation messages that added after leaving [ChatFragment]
     * then call sendNotification()
     */
    override suspend fun doWork(): Result {
        var messageSize = inputData.getInt(ChatViewModel.MESSAGE_LIST_SIZE, 0)
        val name = inputData.getString(ChatViewModel.RECEIVER_WORK_MANAGER)
        val isParent = inputData.getBoolean(ChatViewModel.IS_PARENT_WORK_MANAGER, true)
        val parentId = inputData.getLong(ChatViewModel.PARENT_ID, 0)
        val childId = inputData.getLong(ChatViewModel.CHILD_ID, 0)
        try {
            val conversation = Repository.retrofitRepository.getMessageService()
                .getAllConversationMessage(parentId, childId)
            conversation.collect {
                if (it is Response.Success) {
                    if (it.data?.size!! > messageSize) {
                        messageSize = it.data.size
                        val lastMessage = it.data.last()
                        sendNotification(lastMessage, isParent, name)
                    }
                }
            }
            return Result.Success()
        }catch (e: Exception){
            Log.e("Work","error:${e.message}")
            return Result.failure()
        }
    }

    /**
     * This function will cancel all the notification and call checkReceiverToSendNotification()
     * @param lastMessage: The [Message] that will send in the notification
     */
    private fun sendNotification(
        lastMessage: Message,
        isParent: Boolean,
        name: String?
    ) {
        val notificationManager = cancelAllNotification()
        checkReceiverToSendNotification(lastMessage, isParent, name, notificationManager)
    }

    /**
     * Tis function will cancel all the notification
     * @return [NotificationManager] instance after all the notification canceled
     */
    private fun cancelAllNotification(): NotificationManager {
        val notificationManager = ContextCompat.getSystemService(
            applicationContext,
            NotificationManager::class.java
        ) as NotificationManager
        notificationManager.cancelNotification()
        return notificationManager
    }

    /**
     * This function will check if the receiver of the last message fetched from the server
     * is not the current user then call notifyForNewAddedMessage()
     */
    private fun checkReceiverToSendNotification(
        lastMessage: Message,
        isParent: Boolean,
        name: String?,
        notificationManager: NotificationManager
    ) {
        if (lastMessage.sender.equals("c")) {
            if (isParent) {
                notifyForNewAddedMessage(lastMessage, name, notificationManager)
            }
        } else {
            if (!isParent) {
                notifyForNewAddedMessage(lastMessage, name, notificationManager)
            }
        }
    }

    /**
     * This function trigger the notification
     * @param lastMessage: has the content which will be the body of the notification
     * @param name: The [User] name which will be part of the notification title
     */
    private fun notifyForNewAddedMessage(lastMessage: Message, name: String?
                                         , notificationManager: NotificationManager
    ){
        lastMessage.content?.let { it1 ->
            if (name != null) {
                notificationManager.sendNotification(
                    it1,
                    applicationContext.getString(R.string.chat_notification_title,name),
                    applicationContext
                )
            }
        }
    }
}