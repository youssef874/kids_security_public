package com.example.SecurityKidsBackend.data.service

import com.example.SecurityKidsBackend.data.entities.Child
import com.example.SecurityKidsBackend.data.entities.Location
import com.example.SecurityKidsBackend.data.repository.ChildRepository
import com.example.SecurityKidsBackend.data.repository.LocationRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*
import javax.transaction.Transactional
import kotlin.collections.ArrayList

@Service
class LocationService(
    @Autowired
    val repository: LocationRepository,
    @Autowired
    val childRepository: ChildRepository
) {

    fun getLocation(locationId: Long) = repository.findById(locationId).get()

    fun getAllLocationOfChildren(childId: Long): List<Location>{
        val list = ArrayList<Location>()
        val fetchedList = repository.getAllLocationByChildId(childId)
        if (fetchedList.isNotEmpty()){
            fetchedList.forEach {
                list.add(it)
            }
        }
        return list
    }

    fun getAllLocation(): MutableList<Location> = repository.findAll()

    @Transactional
    fun updateLocation(location: Location){
        val lOptional: Optional<Location> = repository.findById(location.idLocation)
        val l: Location = lOptional.get()
        if (lOptional.isPresent){
            l.updateLatLocation(location.latLocation)
            l.updateLongLocation(location.longLocation)
        }
    }

    fun deleteLocation(locationId: Long){
        val lOptional: Optional<Location> = repository.findById(locationId)
        val l: Location = lOptional.get()
        if (lOptional.isPresent){
            repository.delete(l)
        }
    }

    fun deleteLocationByChildId(childId: Long){
        val childLocations = getAllLocationOfChildren(childId)
        if (childLocations.isNotEmpty()){
            repository.deleteAllLocationOfChild(childId)
        }
    }

    fun addNewLocation(location: Location){
        val lOptional: Optional<Location> = repository.findById(location.idLocation)
        val childOptional: Optional<Child> = childRepository.findById(location.childLocation.idChild)
        if (!lOptional.isPresent && childOptional.isPresent){
            repository.save(Location(
                latLocation = location.latLocation, longLocation = location.longLocation,
                childLocation = location.childLocation
            ))
        }
    }
}