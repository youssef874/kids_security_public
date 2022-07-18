package com.example.SecurityKidsBackend.data.entities

import com.fasterxml.jackson.annotation.JsonIdentityInfo
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonManagedReference
import com.fasterxml.jackson.annotation.ObjectIdGenerators
import javax.persistence.*

@Entity
@Table(name = "parent")

class Parent(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var idParent:Long = -1,
    var nameParent:String = "",
    @Column(unique = true, name = "tel_parent")
    var telParent: Int = 0,
    @Column(unique = true)
    var imageParent:String = "",
    @Column(unique = true)
    var loginParent: String = "",
    @Column(unique = true)
    var pswParent: String = "",
    @Lob
    var data: ByteArray = ByteArray(3072),
    var imageType: String = ""

){
    @OneToMany(mappedBy = "parent", cascade = [CascadeType.ALL])
    @JsonManagedReference
    val children: List<Child> = mutableListOf()

    @OneToMany(mappedBy = "parentMessage"
        , cascade = [CascadeType.ALL])
    @JsonIgnore
    val message: List<Message> = mutableListOf()

    fun updateName(mName: String?){
        mName?.let {
            if (it.isNotEmpty() && it != nameParent)
                nameParent = it
        }
    }

    fun updateImageParent(mImage: String?){
        mImage?.let {
            if (it.isNotEmpty() && it != imageParent){
                imageParent = it
            }
        }
    }

    fun updateLoginParent(mLogin: String?){
        mLogin?.let {
            if (it.isNotEmpty() && it != loginParent){
                loginParent = it
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

    fun updatePhoneNumber(mTelParent: Int?){
        mTelParent?.let {
            if (it != 0 && it != telParent){
                telParent = it
            }
        }
    }

}
