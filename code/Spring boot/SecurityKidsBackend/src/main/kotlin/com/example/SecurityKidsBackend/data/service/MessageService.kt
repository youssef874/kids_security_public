package com.example.SecurityKidsBackend.data.service

import com.example.SecurityKidsBackend.data.entities.Child
import com.example.SecurityKidsBackend.data.entities.Message
import com.example.SecurityKidsBackend.data.entities.Parent
import com.example.SecurityKidsBackend.data.repository.ChildRepository
import com.example.SecurityKidsBackend.data.repository.MessageRepository
import com.example.SecurityKidsBackend.data.repository.ParentRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class MessageService(
    @Autowired
    val repository: MessageRepository,
    @Autowired
    val childRepository: ChildRepository,
    @Autowired
    val parentRepository: ParentRepository
) {

    fun getAllMessage(): MutableList<Message> = repository.findAll()

    fun getAMessage(messageId: Long): Message = repository.getById(messageId)

    fun getAllParentMessage(parentId: Long): List<Message> = repository.getAllMessagesByParentId(parentId)

    fun getChildMessage(childId: Long): List<Message> = repository.getAllMessagesByChildId(childId)

    fun getAllConversationMessages(childId: Long,parentId: Long) = repository
        .getAllConversationMessages(childId, parentId)

    fun addNewMessage(message: Message){
        val parenOptional: Optional<Parent> = parentRepository.findById(message.parentMessage.idParent)
        val childOptional: Optional<Child> = childRepository.findById(message.childrenMessage.idChild)
        if ( childOptional.isPresent && parenOptional.isPresent){
            repository.save(
                Message(
                    content = message.content, sender = message.sender,
                    childrenMessage = message.childrenMessage, parentMessage = message.parentMessage
                )
            )
        }
    }

    fun deleteAMessage(messageId: Long){
        val mOptional: Optional<Message> = repository.findById(messageId)
        if (mOptional.isPresent){
            repository.delete(mOptional.get())
        }
    }

    fun updateMessage(message: Message){
        val mOptional: Optional<Message> = repository.findById(message.idMessage)
        val m : Message = mOptional.get()
        if (mOptional.isPresent){
            m.updateContent(message.content)
        }
    }
}