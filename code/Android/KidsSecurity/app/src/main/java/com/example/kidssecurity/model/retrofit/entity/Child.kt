package com.example.securitykids.model.entities

import android.os.Parcelable
import com.example.kidssecurity.model.retrofit.entity.User


/**
This class represent the child as uer to this app
 */
data class Child(
    var idChild: Long = -1,
    var nameChild: String = "",
    var telChild: Int = 0,
    //This properties represent the image Name
    var imageChild: String = "",
    var loginChild: String = "",
    var pswChild: String = "",
    var location: List<Location> = listOf(),
): User(idChild,nameChild,telChild,loginChild){

    var isTrackable: Boolean = false
}
