package com.example.SecurityKidsBackend.data.entities

import com.fasterxml.jackson.annotation.JsonBackReference
import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.*

@Table(name = "message")
@Entity
class Message(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var idMessage: Long = -1,
    var content: String = "",
    var sender: String = "",
    @ManyToOne
    @JoinColumn(name = "id_children", nullable = false, referencedColumnName = "idChild")
    var childrenMessage: Child = Child(),

    @ManyToOne
    @JoinColumn(name = "id_parent", nullable = false, referencedColumnName = "idParent")
    var parentMessage: Parent = Parent()
) {
    fun updateContent(mContent: String?){
        mContent?.let {
            if (it.isNotEmpty() && it != content){
                content = it
            }
        }
    }
}