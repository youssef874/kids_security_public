package com.example.SecurityKidsBackend.data.repository

import com.example.SecurityKidsBackend.data.entities.Parent
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ParentRepository:JpaRepository<Parent,Long> {

    @Query("SELECT * FROM parent p WHERE p.tel_parent = ?1", nativeQuery = true)
    fun getParentByTel(tel:Int): Optional<Parent>

    @Query("SELECT * FROM parent p WHERE p.login_parent = ?1 AND p.psw_parent = ?2", nativeQuery = true)
    fun getParentByLoginAndPassword(name: String, psw:String): Optional<Parent>

    @Query("SELECT * FROM parent p WHERE p.login_parent = ?1", nativeQuery = true)
    fun getParentByLogin(login: String): Optional<Parent>

    @Query("SELECT * FROM parent p,child c WHERE p.id_parent = c.id_parent AND c.id_child=?1", nativeQuery = true)
    fun getParentChild(childId: Long): Optional<Parent>
}