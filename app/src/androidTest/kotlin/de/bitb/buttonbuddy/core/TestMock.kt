package de.bitb.buttonbuddy.core

import de.bitb.buttonbuddy.shared.buildUser
import de.bitb.buttonbuddy.core.misc.Resource
import de.bitb.buttonbuddy.data.model.Buddy
import de.bitb.buttonbuddy.data.model.User
import de.bitb.buttonbuddy.data.model.Message
import de.bitb.buttonbuddy.data.source.LocalDatabase
import de.bitb.buttonbuddy.data.source.RemoteService
import io.mockk.coEvery

suspend fun LocalDatabase.mockLocalDatabase(
    messages: List<Message> = emptyList()
) {
    for (message in messages) {
        insert(message)
    }
}

fun RemoteService.mockUserService(
    user: User? = null,
    buddies: List<Buddy> = emptyList(),
) {
    coEvery { registerUser(any(), any()) }.answers { Resource.Success(Unit) }
    coEvery { loginUser(any(), any()) }.answers { Resource.Success(true) }
    coEvery { saveUser(any()) }.answers { Resource.Success(Unit) }
    coEvery { getUser(any(), any()) }.answers { Resource.Success(user) }
    coEvery { loadBuddies(any()) }.returns(Resource.Success(buddies))
}

fun RemoteService.mockMessageService(
    sendMessageError: String? = null,
    saveMessageError: String? = null
) {
    coEvery { saveMessage(any()) }.returns(
        saveMessageError?.let { Resource.Error(it) } ?: Resource.Success(Unit)
    )
    coEvery { sendMessage(any()) }.returns(
        sendMessageError?.let { Resource.Error(it) } ?: Resource.Success(Unit)
    )

    coEvery { sendMessage(any()) }.returns(
        sendMessageError?.let { Resource.Error(it) } ?: Resource.Success(Unit)
    )
}