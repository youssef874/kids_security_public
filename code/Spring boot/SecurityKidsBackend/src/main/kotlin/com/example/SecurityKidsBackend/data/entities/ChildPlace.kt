package com.example.SecurityKidsBackend.data.entities

import javax.persistence.*

@Table(name = "child_place")
@Entity
class ChildPlace(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_child_place")
    var idChildPlace: Long = -1,
    @ManyToOne
    @JoinColumn(name = "id_child", nullable = false, referencedColumnName = "idChild")
    var childP: Child = Child(),
    @ManyToOne
    @JoinColumn(name = "id_place", nullable = false, referencedColumnName = "idPlace")
    var placeP: Place = Place()
) {
}