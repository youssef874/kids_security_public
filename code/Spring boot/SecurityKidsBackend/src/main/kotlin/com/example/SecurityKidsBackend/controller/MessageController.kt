package com.example.SecurityKidsBackend.controller

import com.example.SecurityKidsBackend.data.entities.Child
import com.example.SecurityKidsBackend.data.entities.Message
import com.example.SecurityKidsBackend.data.entities.Parent
import com.example.SecurityKidsBackend.data.service.MessageService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(path = ["/messages"])
class MessageController(
    @Autowired
    val service: MessageService
) {

    @GetMapping
    fun getAllMessages(): List<Message> = service.getAllMessage()

    @GetMapping("/parent/{parentId}")
    fun getParentMessage(@PathVariable parentId: Long): List<Message> = service.getAllParentMessage(parentId)

    @GetMapping("/child/{childId}")
    fun getChildMessage(@PathVariable childId: Long): List<Message> = service.getChildMessage(childId)

    @GetMapping("/{messageId}")
    fun getMessage(@PathVariable messageId: Long): Message = service.getAMessage(messageId)

    @GetMapping("/conversation")
    fun getAllConversationMessages(@RequestParam childId: Long,@RequestParam parentId: Long) = service
        .getAllConversationMessages(childId, parentId)

    @PostMapping
    fun insertMessage(@RequestParam content:String,@RequestParam sender: String
                      ,@RequestParam idParent: Long,@RequestParam idChild: Long){
        val message = Message(content = content, sender = sender, childrenMessage = Child(idChild = idChild),
        parentMessage = Parent(idParent = idParent)
        )
        service.addNewMessage(message)
    }

    @PutMapping
    fun updateMessage(@RequestBody message: Message){
        service.updateMessage(message)
    }

    @DeleteMapping("/{messageId}")
    fun deleteMessage(@PathVariable messageId: Long){
        service.deleteAMessage(messageId)
    }
}