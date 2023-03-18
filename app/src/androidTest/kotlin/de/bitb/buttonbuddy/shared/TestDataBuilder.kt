package de.bitb.buttonbuddy.shared

import de.bitb.buttonbuddy.data.model.Buddy
import de.bitb.buttonbuddy.data.model.Info

fun buildInfo(buddies:MutableList<String> = mutableListOf()): Info =
    Info(
        uuid = "uuid1",
        token = "token1",
        firstName = "name1",
        lastName = "lastName1",
        buddies = buddies,
    )

fun buildBuddy(): Buddy =
    Buddy(uuid = "uuid2", token = "token2", firstName = "name2", lastName = "lastName2")
