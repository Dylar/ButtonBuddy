package de.bitb.buttonbuddy

import de.bitb.buttonbuddy.data.model.Buddy
import de.bitb.buttonbuddy.data.model.Info

fun buildInfo(): Info =
    Info(
        uuid = "uuid1",
        token = "token1",
        firstName = "name1",
        lastName = "lastName1",
    )

fun buildBuddy(): Buddy =
    Buddy(uuid = "uuid2", token = "token2", firstName = "name2", lastName = "lastName2")
