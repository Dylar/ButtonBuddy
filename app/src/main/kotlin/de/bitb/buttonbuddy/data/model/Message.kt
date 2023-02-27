package de.bitb.buttonbuddy.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "message")
data class Message(
    @PrimaryKey val uuid: String = "",
    val title: String = "",
    val message: String = "",
    val fromUuid: String = "",
    val toUuid: String = "",
    val token: String = "",
    val date: Date = Date(System.currentTimeMillis()),
) {
    fun fromMap(data: Map<String, String>): Message {
        return Message(
            uuid = data["uuid"]!!,
            title = data["title"]!!,
            message = data["message"]!!,
            fromUuid = data["fromUuid"]!!,
            toUuid = data["toUuid"]!!,
            date = Date(data["date"]!!.toLong())
        )
    }

    fun toMap(): Map<String, String> {
        return mapOf(
            "uuid" to uuid,
            "title" to title,
            "message" to message,
            "fromUuid" to fromUuid,
            "toUuid" to toUuid,
            "date" to date.time.toString()
        )
    }
}