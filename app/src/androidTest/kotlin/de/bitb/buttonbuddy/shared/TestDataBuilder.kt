package de.bitb.buttonbuddy.shared

import de.bitb.buttonbuddy.data.model.Buddy
import de.bitb.buttonbuddy.data.model.User
import de.bitb.buttonbuddy.data.model.Message
import java.util.*

fun buildInfo(buddies: MutableList<String> = mutableListOf()): User =
    User(
        uuid = "uuid1",
        token = "token1",
        firstName = "name1",
        lastName = "lastName1",
        buddies = buddies,
    )

fun buildBuddy(): Buddy =
    Buddy(uuid = "uuid2", token = "token2", firstName = "name2", lastName = "lastName2")

fun buildMessage(
    uuid: String = "msgUuid",
    fromUuid: String = "uuid1",
    toUuid: String = "uuid2",
    date: Date = Date(System.currentTimeMillis()),
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