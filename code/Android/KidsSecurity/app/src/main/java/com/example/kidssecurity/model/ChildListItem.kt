package com.example.kidssecurity.model

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable
import com.example.securitykids.model.entities.Child

/**
 * This class represent the data holder of children represented in the [ChildListAdapter] item
 */
data class ChildListItem(
    //This property represent the child image uri
    var uri: Uri? = Uri.EMPTY,
    //This property represent the child data
    var child: Child? = Child()
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readParcelable(Uri::class.java.classLoader),
        parcel.readParcelable(Child::class.java.classLoader)
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(uri, flags)
        parcel.writeParcelable(child, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ChildListItem> {
        override fun createFromParcel(parcel: Parcel): ChildListItem {
            return ChildListItem(parcel)
        }

        override fun newArray(size: Int): Array<ChildListItem?> {
            return arrayOfNulls(size)
        }
    }
}
