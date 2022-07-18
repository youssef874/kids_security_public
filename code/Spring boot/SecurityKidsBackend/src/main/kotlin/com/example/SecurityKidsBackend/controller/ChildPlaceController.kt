package com.example.SecurityKidsBackend.controller

import com.example.SecurityKidsBackend.data.entities.ChildPlace
import com.example.SecurityKidsBackend.data.service.ChildPlaceService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(path = ["/childPlaces"])
class ChildPlaceController(
    @Autowired
    val service: ChildPlaceService
) {

    @GetMapping("/{id}")
    fun getChildPlace(@PathVariable id: Long): ChildPlace = service.getChildPlace(id)

    @GetMapping
    fun getAllChildPlace(): List<ChildPlace> = service.getAllChildPlace()

    @GetMapping("/child/{childId}")
    fun getAllPlaceByChildId(@PathVariable childId: Long): List<ChildPlace> = service.getAllPlaceByChildId(childId)

    @GetMapping("/place/{placeId}")
    fun getAllChildByPlaceId(@PathVariable placeId: Long): List<ChildPlace> = service.getAllChildByPlaceId(placeId)

    @PostMapping
    fun insertChildPlace(@RequestBody childPlace: ChildPlace){
        service.addNewChildPlace(childPlace)
    }

    @DeleteMapping("/{id}")
    fun deleteChildPlace(@PathVariable id: Long){
        service.deleteChildPlace(id)
    }


}