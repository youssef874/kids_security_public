package com.example.kidssecurity.model

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable
import com.example.kidssecurity.model.retrofit.entity.User
import com.example.securitykids.model.entities.Message

/**
 * This class represent the data holder of children represented in the [ChatListAdapter] item
 */
data class MessageListItem(
    //This property will represent the message data
    var message: Message? = Message(),
    //This property will represent the receiver image uri
    var receiverUri: Uri? = Uri.EMPTY
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readParcelable(Message::class.java.classLoader),
        parcel.readParcelable(Uri::class.java.classLoader)
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(message, flags)
        parcel.writeParcelable(receiverUri, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MessageListItem> {
        override fun createFromParcel(parcel: Parcel): MessageListItem {
            return MessageListItem(parcel)
        }

        override fun newArray(size: Int): Array<MessageListItem?> {
            return arrayOfNulls(size)
        }
    }
}
