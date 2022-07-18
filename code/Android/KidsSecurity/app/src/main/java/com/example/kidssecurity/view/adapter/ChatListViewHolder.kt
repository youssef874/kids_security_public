package com.example.kidssecurity.view.adapter

import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.kidssecurity.databinding.ReceiverListItemBinding
import com.example.kidssecurity.databinding.SenderListItemBinding
import com.example.kidssecurity.model.MessageListItem

/**
 * This class will hold [ChatListAdapter] view holder as it will contain tow
 * type of view one for the sender and the other for the receiver
 * @param binding: the view who is going to be inflated to display our data
 */
sealed class ChatListViewHolder( binding: ViewBinding): RecyclerView.ViewHolder(binding.root){

    /**
     * This class represent the receiver view in the [RecyclerView]
     * @param binding: this represent receiver_list_item.xml
     */
    class ReceiverViewHolder(val binding: ReceiverListItemBinding): ChatListViewHolder(binding){
        /**
         * This function will bind the [MessageListItem] data to the views
         * and notify the view if any thing changes in the data
         * @param messageListItem: the data that will be displayed
         */
        fun bind(messageListItem: MessageListItem){
            binding.messageListItem = messageListItem
            binding.executePendingBindings()
        }
    }

    /**
     * This class represent the receiver view in the [RecyclerView]
     * @param binding: this represent sender_list_item.xml
     */
    class SenderViewHolder(val binding: SenderListItemBinding): ChatListViewHolder(binding){
        /**
         * This function will bind the [MessageListItem] data to the views
         * and notify the view if any thing changes in the data
         * @param messageListItem: the data that will be displayed
         */
        fun bind(messageListItem: MessageListItem){
            binding.messageListItem = messageListItem
            binding.executePendingBindings()
        }
    }
}
