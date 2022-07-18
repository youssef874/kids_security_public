package com.example.SecurityKidsBackend.controller

import com.example.SecurityKidsBackend.data.entities.Place
import com.example.SecurityKidsBackend.data.service.PlaceService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(path = ["/places"])
class PlaceController(
    @Autowired
    val service: PlaceService
) {

    @GetMapping("/{placeId}")
    fun getPlace(@PathVariable placeId: Long): Place = service.findAPlace(placeId)

    @GetMapping
    fun getAllPlaces(): List<Place> = service.findAllPlace()

    @PostMapping
    fun insertPlace(@RequestBody place: Place){
        service.addNewPlace(place)
    }

    @PutMapping
    fun updatePlace(@RequestBody place: Place){
        service.updatePlace(place)
    }

    @DeleteMapping("/{placeId}")
    fun deletePlace(@PathVariable placeId: Long){
        service.deletePlace(placeId)
    }
}