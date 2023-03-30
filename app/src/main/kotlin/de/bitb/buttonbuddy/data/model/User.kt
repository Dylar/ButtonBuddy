package de.bitb.buttonbuddy.data.model

import androidx.room.*

@Entity(tableName = "user")
data class User(
    @PrimaryKey val uuid: String = "",
    val token: String = "",
    val userName: String = "",
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
            "firstName" to firstName,
            "lastName" to lastName,
            "buddies" to buddies
        )
    }
}