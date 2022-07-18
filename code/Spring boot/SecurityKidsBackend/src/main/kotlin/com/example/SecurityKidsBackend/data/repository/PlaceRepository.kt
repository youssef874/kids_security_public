package com.example.SecurityKidsBackend.data.repository

import com.example.SecurityKidsBackend.data.entities.Place
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface PlaceRepository: JpaRepository<Place,Long> {

    @Query("SELECT * FROM place p WHERE p.lat_place = ?1 AND p.long_place = ?2", nativeQuery = true)
    fun getPlaceByLatLong(lat:Long, long: Long):Optional<Place>
}