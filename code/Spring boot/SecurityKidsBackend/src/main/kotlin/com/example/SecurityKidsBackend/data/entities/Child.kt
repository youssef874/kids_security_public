package com.example.SecurityKidsBackend.data.entities

import com.fasterxml.jackson.annotation.JsonBackReference
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonManagedReference
import javax.persistence.*

@Table(name = "child")
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
class Child(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var idChild: Long = -1,
    var nameChild: String ="",
    @Column(name = "tel_child")
    var telChild: Int = 0,
    var imageChild: String = "",
    var loginChild: String = "",
    var pswChild: String = "",
    var imageType: String = "",
    @Lob
    var data: ByteArray = ByteArray(3072),
    @ManyToOne
    @JoinColumn(name = "id_parent", nullable = true, referencedColumnName = "idParent")
    @JsonBackReference
    var parent: Parent =Parent(),

) {

    @OneToMany(mappedBy = "childLocation", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JsonManagedReference
    val location:List<Location> = mutableListOf()

    @OneToMany(mappedBy = "childrenMessage",
        cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JsonIgnore
    val message: List<Message> = mutableListOf()

    @OneToMany(mappedBy = "childP",
        cascade = [CascadeType.ALL])
    @JsonIgnore
    val childPlace: List<ChildPlace> = mutableListOf()

    fun updateNameChild(mName: String?){
        mName?.let {
            if (it.isNotEmpty() && it != nameChild){
                nameChild = it
            }
        }
    }

    fun updateImageChild(mImage: String?){
        mImage?.let{
            if (it.isNotEmpty() && it != imageChild){
                imageChild = it
            }
        }
    }

    fun updateLoginChild(mLogin: String?){
        mLogin?.let {
            if (it.isNotEmpty() && loginChild != it){
                loginChild = it
            }
        }
    }

    fun updateImageType(mImageType: String?){
        mImageType?.let {
            if (it.isNotEmpty() && it != imageType){
                imageType = it
            }
        }
    }

    fun updateData(mData: ByteArray?){
        mData?.let {
            if (it.isNotEmpty() && !it.contentEquals(data)){
                data = it
            }
        }
    }

    fun updatePhoneNumber(mTelChild: Int?){
        mTelChild?.let {
            if (it != 0 && it != telChild){
                telChild = it
            }
        }
    }
}