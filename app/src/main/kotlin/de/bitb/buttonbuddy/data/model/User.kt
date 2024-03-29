package de.bitb.buttonbuddy.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class User(
    @PrimaryKey val uuid: String = "",
    val token: String = "",
    val email: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val buddies: MutableList<String> = mutableListOf(),
) {
    val fullName: String
        get() = "$firstName $lastName"

    fun toMap(): Map<String, Any> {
        return mapOf(
            "uuid" to uuid,
            "token" to token,
            "email" to email,
            "firstName" to firstName,
            "lastName" to lastName,
            "buddies" to buddies,
        )
    }
}