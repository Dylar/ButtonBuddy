package de.bitb.buttonbuddy.data.model

import androidx.room.*

@Entity(tableName = "info")
data class Info(
    @PrimaryKey val uuid: String = "",
    val token: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val buddies: MutableList<String> = mutableListOf(),
)