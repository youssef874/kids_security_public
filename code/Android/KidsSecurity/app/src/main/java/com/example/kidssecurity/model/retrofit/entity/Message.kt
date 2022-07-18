package com.example.securitykids.model.entities

import android.os.Parcel
import android.os.Parcelable

data class Message(
    var idMessage: Long? = 0,
    var content: String? = "",
    var sender: String? = "",
    var childrenMessage: Child? = Child(),
    var parentMessage: Parent? = Parent()
):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readValue(Long::class.java.classLoader) as? Long,
        parcel.readString(),
        parcel.readString(),
        parcel.readParcelable(Child::class.java.classLoader),
        parcel.readParcelable(Parent::class.java.classLoader)
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(idMessage)
        parcel.writeString(content)
        parcel.writeString(sender)
        parcel.writeParcelable(childrenMessage, flags)
        parcel.writeParcelable(parentMessage, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Message> {
        override fun createFromParcel(parcel: Parcel): Message {
            return Message(parcel)
        }

        override fun newArray(size: Int): Array<Message?> {
            return arrayOfNulls(size)
        }
    }
}
