package com.example.SecurityKidsBackend.controller

import com.example.SecurityKidsBackend.data.entities.Child
import com.example.SecurityKidsBackend.data.entities.Location
import com.example.SecurityKidsBackend.data.service.LocationService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(path = ["/locations"])
class LocationController(
    @Autowired
    val service: LocationService
) {

    @GetMapping
    fun getAllLocation() = service.getAllLocation()

    @GetMapping("/{locationId}")
    fun getLocation(@PathVariable locationId: Long): Location{
        return service.getLocation(locationId)
    }

    @GetMapping("/children/{childId}")
    fun getChildrenLocation(@PathVariable childId: Long): List<Location>{
        return service.getAllLocationOfChildren(childId)
    }

    @PostMapping
    fun insertLocation(@RequestParam lat: Long,@RequestParam log: Long,@RequestParam idChild: Long){
        val location = Location(latLocation = lat, longLocation = log, childLocation = Child(idChild = idChild))
        service.addNewLocation(location)
    }

    @DeleteMapping("/{locationId}")
    fun deleteLocation(@PathVariable locationId: Long){
        service.deleteLocation(locationId)
    }

    @DeleteMapping("/children/{childId}")
    fun deleteAllChildrenLocation(@PathVariable childrenId: Long){
        service.deleteLocationByChildId(childrenId)
    }

    @PutMapping
    fun updateLocation(@RequestBody location: Location){
        service.updateLocation(location)
    }
}