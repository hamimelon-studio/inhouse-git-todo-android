package com.mikeapp.newideatodoapp.util

object RoomUtil {
    fun String.supportNullable(): String? {
        if (this == "null") return null
        return this
    }
}