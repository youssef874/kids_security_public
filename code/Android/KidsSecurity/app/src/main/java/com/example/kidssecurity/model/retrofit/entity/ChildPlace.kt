package com.example.securitykids.model.entities

data class ChildPlace(
    var idChildPlace: Long? = -1,
    var childP: Child? = Child(),
    var placeP: Place? = Place()
)
