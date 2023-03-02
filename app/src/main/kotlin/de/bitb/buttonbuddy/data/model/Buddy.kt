package de.bitb.buttonbuddy.data.model

import androidx.room.*

@Entity(tableName = "buddy")
data class Buddy(
    @PrimaryKey val uuid: String = "",
    val token: String = "",
    val firstName: String = "",
    val lastName: String = "",
){
    val fullName: String
        get() = "$firstName $lastName"
}

