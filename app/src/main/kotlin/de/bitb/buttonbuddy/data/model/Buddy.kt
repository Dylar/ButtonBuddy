package de.bitb.buttonbuddy.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import de.bitb.buttonbuddy.core.misc.DEFAULT_COOLDOWN

@Suppress("UNCHECKED_CAST")
@Entity(tableName = "buddy")
data class Buddy(
    @PrimaryKey val uuid: String = "",
    val token: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val cooldown: Long = DEFAULT_COOLDOWN,
) {
    constructor(userUuid: String, map: Map<String, Any>) : this(
        uuid = map["uuid"] as String,
        token = map["token"] as String,
        firstName = map["firstName"] as String,
        lastName = map["lastName"] as String,
        cooldown = (map["cooldowns"] as? Map<String, Long>
            ?: mapOf<String, Long>())[userUuid] ?: 0,
    )

    val fullName: String
        get() = "$firstName $lastName"
}

