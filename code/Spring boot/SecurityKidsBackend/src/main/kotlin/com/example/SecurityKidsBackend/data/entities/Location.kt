package com.example.SecurityKidsBackend.data.entities

import com.fasterxml.jackson.annotation.JsonBackReference
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "location")
@JsonIgnoreProperties(ignoreUnknown = true)
class Location(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var idLocation: Long = -1,
    var latLocation: Long = 0L,
    var longLocation: Long = 0L,
    @Column(unique = true)
    @Temporal(TemporalType.TIMESTAMP)
    var time: Date = Date(),
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_child", nullable = true, referencedColumnName = "idChild")
    @JsonBackReference
    var childLocation: Child = Child()
) {
    fun updateLatLocation(mLat: Long?){
        mLat?.let {
            if (it != 0L && it != latLocation){
                latLocation = it
            }
        }
    }

    fun updateLongLocation(mLong: Long?){
        mLong?.let {
            if (it != 0L && it != longLocation){
                longLocation = it
            }
        }
    }
}