package com.example.securitykids.model.entities

import android.os.Parcelable
import com.example.kidssecurity.model.retrofit.entity.User

/**
This class represent the parent as user to this app
 */
data class Parent(
    var idParent: Long = -1,
    var nameParent: String = "",
    var telParent: Int = 0,
    //This properties represent the image Name
    var imageParent: String = "",
    var loginParent: String = "",
    var pswParent: String = "",
    var children: List<Child> = listOf()
): User(idParent,nameParent,telParent,loginParent)
