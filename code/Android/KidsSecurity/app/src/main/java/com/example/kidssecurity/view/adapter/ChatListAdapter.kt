package com.example.kidssecurity.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.kidssecurity.databinding.ReceiverListItemBinding
import com.example.kidssecurity.databinding.SenderListItemBinding
import com.example.kidssecurity.model.MessageListItem

//Tis const will represent the Sender view
const val SENDER_TYPE: Int = 1
//Tis const will represent the receiver view
const val RECEIVER_TYPE: Int = 2

/**
 * This class represent the adapter of the [RecycleView] of fragment chat
 * @param isParent: the user type (parent or child)
 */
class ChatListAdapter(private val isParent: Boolean):
    ListAdapter<MessageListItem,ChatListViewHolder>(DiffCallback) {

    /**
     * This will figure out any data changes for the [RecycleView] to be notified
     */
    companion object DiffCallback: DiffUtil.ItemCallback<MessageListItem>(){

        /**
         * Called to check whether two items have the same data.
         */
        override fun areItemsTheSame(oldItem: MessageListItem, newItem: MessageListItem): Boolean {
            return oldItem == newItem
        }

        /**
         * Called to check whether two objects represent the same item.
         */
        override fun areContentsTheSame(
            oldItem: MessageListItem,
            newItem: MessageListItem
        ): Boolean {
            val newMessageId = newItem.message?.idMessage
            val oldMessageId = oldItem.message?.idMessage
            return newMessageId == oldMessageId
        }

    }

    /**
     * This will create [ChatListViewHolder] instance for each item
     * @param parent: The ViewGroup into which the new View will be added
     * after it is bound to an adapter position.
     * @param viewType: The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatListViewHolder {
        return when(viewType){
            RECEIVER_TYPE -> {
                //inflate sender_list_item.xml with viewBinding and passe it in
                //the ChatListViewHolder.SenderViewHolder constructor
                ChatListViewHolder.SenderViewHolder(SenderListItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ))
            }
            SENDER_TYPE ->{
                //inflate receiver_list_item.xml with viewBinding and passe it in
                //the ChatListViewHolder.ReceiverViewHolder constructor
                ChatListViewHolder.ReceiverViewHolder(ReceiverListItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ))
            }
            else -> {
               throw IllegalArgumentException("Invalid view type")
            }
        }
    }

    /**
     * Called by RecyclerView to display the data at the specified position.
     * This method should update the contents of the RecyclerView.ViewHolder.
     * itemView to reflect the item at the given position.
     * @param holder: The ViewHolder which should be updated to represent
     * the contents of the item at the given position in the data set.
     * @param position: The position of the item within the adapter's data set.
     */
    override fun onBindViewHolder(holder: ChatListViewHolder, position: Int) {
        val data = getItem(position)
        when(holder){
            //Depending on the holder type bind the data to the corresponding view
            is ChatListViewHolder.SenderViewHolder ->{holder.bind(data)}
            is ChatListViewHolder.ReceiverViewHolder ->{holder.bind(data)}
        }
    }

    /**
     * Return the view type of the item at position for the purposes of view recycling.
     */
    override fun getItemViewType(position: Int): Int {
        //If the user is the parent
        return when(isParent){
            true ->{
                //If the sender is parent according to the data base
                if (getItem(position).message?.sender.equals("p")){
                    SENDER_TYPE
                }else{
                    RECEIVER_TYPE
                }
            }else ->{
                //If the sender is child according to the data base
                if (getItem(position).message?.sender.equals("c")){
                    SENDER_TYPE
                }else{
                    RECEIVER_TYPE
                }
            }
        }
    }

}