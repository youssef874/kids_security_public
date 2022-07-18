package com.example.SecurityKidsBackend.data.service

import com.example.SecurityKidsBackend.data.entities.Place
import com.example.SecurityKidsBackend.data.repository.PlaceRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class PlaceService(
    @Autowired
    val repository: PlaceRepository
) {

    fun findAPlace(placeId: Long): Place = repository.getById(placeId)

    fun findAllPlace(): MutableList<Place> = repository.findAll()

    fun addNewPlace(place: Place){
        val pOptional: Optional<Place> = repository.getPlaceByLatLong(place.latPlace,place.longPlace)
        if (!pOptional.isPresent){
            repository.save(place)
        }
    }

    fun deletePlace(placeId: Long){
        val pOptional: Optional<Place> = repository.findById(placeId)
        if (pOptional.isPresent){
            repository.delete(pOptional.get())
        }
    }

    fun updatePlace(place: Place){
        val pOptional: Optional<Place> = repository.findById(place.idPlace)
        val p: Place = pOptional.get()
        if (pOptional.isPresent){
            p.updateLatPlace(place.latPlace)
            p.updateLongPlace(place.longPlace)
            p.updateTypePlace(place.typePlace)
        }
    }
}