package de.bitb.buttonbuddy.data.model

import androidx.room.*

@Entity(tableName = "info")
data class Info(
    @PrimaryKey val uuid: String = "",
    val token: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val buddies: MutableList<String> = mutableListOf(),
){
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