package com.example.kidssecurity.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.kidssecurity.databinding.FragmentChatBinding
import com.example.kidssecurity.hideKeyboard
import com.example.kidssecurity.model.Repository
import com.example.kidssecurity.model.retrofit.entity.User
import com.example.kidssecurity.view.adapter.ChatListAdapter
import com.example.kidssecurity.view_model.chat.ChatViewModel
import com.example.kidssecurity.view_model.chat.ChatViewModelFactory
import com.example.securitykids.model.entities.Child
import com.example.securitykids.model.entities.Parent


class ChatFragment : Fragment() {

    private lateinit var binding: FragmentChatBinding

    private var sender: User? = null
    private var receiver: User? = null
    private var isParent = true

    private val retrofitRepository = Repository.retrofitRepository

    private val factory = ChatViewModelFactory(retrofitRepository)

    private val viewModel: ChatViewModel by viewModels { factory }

    private var parent: Parent = Parent()
    private var child: Child = Child()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            sender = it.getParcelable(SENDER)
            receiver = it.getParcelable(RECEIVER)
            isParent = it.getBoolean(IS_PARENT)
        }
        initialize()
    }

    private fun initialize() {
        if (isParent) {
            parent = sender as Parent
            child = receiver as Child
        } else {
            parent = receiver as Parent
            child = sender as Child
        }
        viewModel.getConversation(child.idChild,parent.idParent)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentChatBinding.inflate(inflater, container, false)
        viewModel.ConversationMessages.observe(viewLifecycleOwner){
            if (it.data?.isNotEmpty() == true){
                receiver?.let { it1 ->
                    viewModel.displayConversation(it.data,requireContext(),isParent,
                        it1
                    )
                }
            }else{
                Log.e(TAG,"error: ${it.message}")
            }
        }

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.chatRecycleView.adapter = ChatListAdapter(isParent)

        viewModel.conversationMessagesListItem.observe(viewLifecycleOwner){
            if (it != null)
                binding.chatLoading.visibility = View.INVISIBLE
        }

        binding.sendButton.setOnClickListener {
            hideKeyboard()
            sendMessage(isParent)
        }

    }

    private fun sendMessage(isParent: Boolean) {

        val content = binding.messageEditText.text.toString()
        if (isParent) {
            viewModel.createMessage(content, "p", child.idChild, parent.idParent)
        } else {
            viewModel.createMessage(content, "c", child.idChild, parent.idParent)
        }
        binding.messageEditText.text?.clear()
        binding.messageEditText.clearFocus()
    }

    companion object {
        const val TAG = "ChatFragment"
        const val RECEIVER = "receiver"
        const val SENDER = "sender"
        const val IS_PARENT = "is_sender_parent"
    }
}