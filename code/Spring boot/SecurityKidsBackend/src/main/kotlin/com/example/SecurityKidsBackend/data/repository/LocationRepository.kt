package com.example.SecurityKidsBackend.data.repository

import com.example.SecurityKidsBackend.data.entities.Location
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface LocationRepository: JpaRepository<Location,Long> {

    @Query("SELECT * FROM location l WHERE l.id_child = ?1", nativeQuery = true)
    fun getAllLocationByChildId(id: Long): List<Location>

    @Modifying
    @Query("DELETE FROM location WHERE id_child = ?1", nativeQuery = true)
    fun deleteAllLocationOfChild(childId: Long)
}