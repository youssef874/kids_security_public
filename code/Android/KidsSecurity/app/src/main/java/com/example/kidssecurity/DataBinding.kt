package com.example.kidssecurity


import android.net.Uri
import android.util.Log
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.kidssecurity.model.ChildListItem
import com.example.kidssecurity.model.MessageListItem
import com.example.kidssecurity.view.adapter.ChatListAdapter
import com.example.kidssecurity.view.adapter.ChildListAdapter

/**
 * This function will be called in the fragment_parent_home.xml
 * RecycleView using DataBinding .
 * Will submit the [ChildListItem] list the [RecyclerView] adapter
 * @param recyclerView: The [RecyclerView] which will be submitting in
 * @param list: The children list who is going to submit
 */
@BindingAdapter("listData")
fun bindRecycleView(recyclerView: RecyclerView, list: List<ChildListItem>?) {
    val adapter = recyclerView.adapter as ChildListAdapter
    list?.let { adapter.submitList(it) }
}

/**
 * This function will be called in the fragment_chat.xml
 * RecycleView using DataBinding .
 * Will submit the [MessageListItem] list the [RecyclerView] adapter
 * @param recyclerView: The [RecyclerView] which will be submitting in
 * @param list: The messages list who is going to submit
 */
@BindingAdapter("chatList")
fun bindChatRecycleView(recyclerView: RecyclerView,list: List<MessageListItem>?){
    val adapter = recyclerView.adapter as ChatListAdapter
    recyclerView.smoothScrollToPosition(adapter.itemCount)
    list?.let {
        adapter.submitList(it)
    }
}

/**
 * This function will be called in the any [ImageView] using DataBinding .
 * will display image using it is uri
 * @param imageView: the view which this function will apply to
 * @param uri: th image uri we are going to display
 */
@BindingAdapter("uri")
fun bindUriToImage(imageView: ImageView, uri: Uri?) {
    uri?.let {
        imageView.setImageURI(it)
    }
}



