package com.example.SecurityKidsBackend.data.entities

import org.hibernate.annotations.GenericGenerator
import javax.persistence.*


data class Image(
    var name: String = "",
    var type: String = "",
    var data: ByteArray = ByteArray(100),
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Image

        if (name != other.name) return false
        if (type != other.type) return false
        if (!data.contentEquals(other.data)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + data.contentHashCode()
        return result
    }
}
