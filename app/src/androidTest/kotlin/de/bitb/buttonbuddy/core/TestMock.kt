package de.bitb.buttonbuddy.core

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
    registerError: Resource.Error<Unit>? = null,
    loginError: Resource.Error<Boolean>? = null,
    saveUserError: Resource.Error<Unit>? = null,
    getUserError: Resource.Error<User?>? = null,
    loadBuddiesError: Resource.Error<List<Buddy>>? = null,
) {
    coEvery { registerUser(any(), any()) }.answers { registerError ?: Resource.Success() }
    coEvery { loginUser(any(), any()) }.answers { loginError ?: Resource.Success(true) }
    coEvery { saveUser(any()) }.answers { saveUserError ?: Resource.Success() }
    coEvery { getUser(any()) }.answers { getUserError ?: Resource.Success(user) }
    coEvery { loadBuddies(any(), any()) }.answers { loadBuddiesError ?: Resource.Success(buddies) }
}

fun RemoteService.mockMessageService(
    sendMessageError: String? = null,
    saveMessageError: String? = null
) {
    coEvery { saveMessage(any()) }.returns(
        saveMessageError?.let { Resource.Error(it) } ?: Resource.Success()
    )
    coEvery { sendMessage(any()) }.returns(
        sendMessageError?.let { Resource.Error(it) } ?: Resource.Success()
    )

    coEvery { sendMessage(any()) }.returns(
        sendMessageError?.let { Resource.Error(it) } ?: Resource.Success()
    )
}