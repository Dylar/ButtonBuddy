package de.bitb.buttonbuddy.data.model

import androidx.room.PrimaryKey
import java.util.*

data class Message(
    @PrimaryKey val uuid: String = "",
    val title: String = "",
    val message: String = "",
    val recipient: String = "",
    val token: String = "",
    val date: Date = Date(System.currentTimeMillis()),
)