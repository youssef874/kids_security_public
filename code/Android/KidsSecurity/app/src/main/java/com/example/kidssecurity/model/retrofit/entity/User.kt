package com.example.kidssecurity.model.retrofit.entity

import android.os.Parcel
import android.os.Parcelable

/**
This class to wrap the parent and user and make it easier to deal with in
the same time for navigation for example as an argument
 */
open class User(
    var id: Long = 0,
    var name: String = "",
    var tel: Int = 0,
    var login: String
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readString()!!
    ) {
    }

    override fun toString(): String {
        return "User[id=$id,name=$name,tel=$tel,login=$login]"
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(name)
        parcel.writeInt(tel)
        parcel.writeString(login)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }
}