package de.bitb.buttonbuddy.shared

import de.bitb.buttonbuddy.data.model.Buddy
import de.bitb.buttonbuddy.data.model.User
import de.bitb.buttonbuddy.data.model.Message
import de.bitb.buttonbuddy.data.model.Settings
import java.util.*

fun buildSettings(): Settings = Settings()

fun buildUser(buddies: MutableList<String> = mutableListOf()): User =
    User(
        uuid = "uuid1",
        token = "token1",
        firstName = "firstName1",
        lastName = "lastName1",
        email = "email1",
        buddies = buddies,
    )

fun buildBuddy(): Buddy =
    Buddy(uuid = "uuid2", token = "token2", firstName = "name2", lastName = "lastName2")

fun buildMessage(
    uuid: String = "msgUuid",
    fromUuid: String = "uuid1",
    toUuid: String = "uuid2",
    date: Date = Date(),
): Message =
    Message(
        uuid = uuid,
        title = "Liebe",
        message = "Denk an dich",
        fromUuid = fromUuid,
        toUuid = toUuid,
        token = "token1",
        date = date
    )