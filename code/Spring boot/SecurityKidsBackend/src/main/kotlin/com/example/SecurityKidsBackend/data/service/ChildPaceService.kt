package com.example.SecurityKidsBackend.data.service

import com.example.SecurityKidsBackend.data.entities.Child
import com.example.SecurityKidsBackend.data.entities.ChildPlace
import com.example.SecurityKidsBackend.data.entities.Place
import com.example.SecurityKidsBackend.data.repository.ChildPlaceRepository
import com.example.SecurityKidsBackend.data.repository.ChildRepository
import com.example.SecurityKidsBackend.data.repository.PlaceRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class ChildPlaceService(
    @Autowired
    val repository: ChildPlaceRepository,
    @Autowired
    val childRepository: ChildRepository,
    @Autowired
    val placeRepository: PlaceRepository
) {

    fun getChildPlace(childPlaceId: Long): ChildPlace = repository.getById(childPlaceId)

    fun getAllChildPlace(): MutableList<ChildPlace> = repository.findAll()

    fun getAllPlaceByChildId(childId: Long): List<ChildPlace> = repository.getAllPlaceByChildId(childId)

    fun getAllChildByPlaceId(placeId: Long): List<ChildPlace> = repository.getAllChildByPlaceId(placeId)

    fun addNewChildPlace(childPlace: ChildPlace){
        val cpOptional: Optional<ChildPlace> = repository.findById(childPlace.idChildPlace)
        val childOptional: Optional<Child> = childRepository.findById(childPlace.childP.idChild)
        val placeOptional: Optional<Place> = placeRepository.findById(childPlace.placeP.idPlace)
        if (!cpOptional.isPresent && childOptional.isPresent && placeOptional.isPresent){
            repository.save(ChildPlace(
                childP = childPlace.childP, placeP = childPlace.placeP
            ))
        }
    }

    fun deleteByChildId(childId: Long){
        val list: List<ChildPlace> = getAllPlaceByChildId(childId)
        if (list.isNotEmpty()){
            repository.deleteAll(list)
        }
    }

    fun deleteChildPlace(id: Long){
        val cpOptional: Optional<ChildPlace> = repository.findById(id)
        if (cpOptional.isPresent){
            repository.delete(cpOptional.get())
        }
    }

}