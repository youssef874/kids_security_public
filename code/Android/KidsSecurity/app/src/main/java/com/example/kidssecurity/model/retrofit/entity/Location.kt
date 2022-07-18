package com.example.securitykids.model.entities


import java.util.*

data class Location(
    var idLocation: Long = -1,
    var latLocation: Long = 0,
    var longLocation: Long = 0,
    var time: Date = Date()
)
