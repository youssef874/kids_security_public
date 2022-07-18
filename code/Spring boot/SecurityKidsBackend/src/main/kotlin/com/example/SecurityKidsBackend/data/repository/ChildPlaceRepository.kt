package com.example.SecurityKidsBackend.data.repository

import com.example.SecurityKidsBackend.data.entities.ChildPlace
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface ChildPlaceRepository:JpaRepository<ChildPlace,Long> {

    @Query("SELECT * FROM child_place cp WHERE cp.id_child = ?1", nativeQuery = true)
    fun getAllPlaceByChildId(childId: Long):List<ChildPlace>

    @Query("SELECT * FROM child_place cp WHERE cp.id_place = ?1", nativeQuery = true)
    fun getAllChildByPlaceId(placeId: Long): List<ChildPlace>
}