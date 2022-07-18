package com.example.SecurityKidsBackend.data.repository

import com.example.SecurityKidsBackend.data.entities.Message
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface MessageRepository: JpaRepository<Message,Long> {

    @Query("SELECT * FROM message m WHERE m.id_parent = ?1", nativeQuery = true)
    fun getAllMessagesByParentId(parentId:Long): List<Message>

    @Query("SELECT * FROM message m WHERE m.id_children = ?1", nativeQuery = true)
    fun getAllMessagesByChildId(childId: Long): List<Message>

    @Query("SELECT * FROM message m WHERE m.id_children = ?1 AND m.id_parent = ?2", nativeQuery = true)
    fun getAllConversationMessages(childId: Long,parentId: Long): List<Message>

}