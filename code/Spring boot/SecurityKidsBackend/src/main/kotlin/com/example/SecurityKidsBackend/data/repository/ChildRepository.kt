package com.example.SecurityKidsBackend.data.repository

import com.example.SecurityKidsBackend.data.entities.Child
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ChildRepository: JpaRepository<Child,Long> {

    @Query("SELECT * FROM child c WHERE c.id_parent = ?1", nativeQuery = true)
    fun getAllChildByParentId(id:Long):List<Child>

    @Query("SELECT * FROM child c WHERE c.tel_child = ?1", nativeQuery = true)
    fun getChildByTel(tel:Int): Optional<Child>

    @Modifying
    @Query("DELETE FROM child c WHERE c.id_parent = ?1 ", nativeQuery = true)
    fun deleteByParentId(parentId: Long)

    @Query("SELECT * FROM child c WHERE c.login_child = ?1 AND psw_child=?2", nativeQuery = true)
    fun getChildLoginAndPassword(name: String, psw: String): Child

    @Query("SELECT * FROM child c WHERE c.login_child = ?1", nativeQuery = true)
    fun getChildByLogin(login: String): Optional<Child>
}