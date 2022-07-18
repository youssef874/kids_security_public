package com.example.SecurityKidsBackend.data.entities

import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.*

@Table(name = "place")
@Entity
class Place(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var idPlace: Long = -1,
    @Column(name = "lat_place")
    var latPlace: Long = 0,
    @Column(name = "long_place")
    var longPlace: Long = 0,
    var typePlace: String = ""
) {

    @JsonIgnore
    @OneToMany(mappedBy = "placeP", cascade = [CascadeType.ALL])
    val childPlace: List<ChildPlace> = mutableListOf()

    fun updateLatPlace(mLat: Long?){
        mLat?.let {
            if (it != 0L && it != latPlace){
                latPlace = it
            }
        }
    }

    fun updateLongPlace(mLong: Long?){
        mLong?.let {
            if (it != 0L && it != longPlace){
                longPlace = it
            }
        }
    }

    fun updateTypePlace(mType: String?){
        mType?.let {
            if (it.isNotBlank() && it != typePlace){
                typePlace = it
            }
        }
    }
}