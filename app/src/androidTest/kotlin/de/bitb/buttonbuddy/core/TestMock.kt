package de.bitb.buttonbuddy.core

import de.bitb.buttonbuddy.core.misc.Resource
import de.bitb.buttonbuddy.data.model.Buddy
import de.bitb.buttonbuddy.data.model.Message
import de.bitb.buttonbuddy.data.model.User
import de.bitb.buttonbuddy.data.source.*
import io.mockk.coEvery

suspend fun LocalDatabase.mockLocalDatabase(
    messages: List<Message> = emptyList()
) {
    for (message in messages) {
        insert(message)
    }
}

fun RemoteService.mockWholeService(
    buildUser: User? = null,
    isLoggedIn: Boolean = true,
    buddies: List<Buddy> = emptyList(),
) {
    mockUserDao(buildUser, isLoggedIn)
    mockBuddyDao(buddies)
    mockMessageService()
    mockSettingsRemoteDao()
}

fun SettingsRemoteDao.mockSettingsRemoteDao(
    loadCooldownError: Resource.Error<Map<String, Long>>? = null,
) {
    coEvery { loadCooldowns(any()) }.answers { loadCooldownError ?: Resource.Success(mapOf()) }
}

fun UserRemoteDao.mockUserDao(
    user: User? = null,
    isLoggedIn: Boolean = true,
    isLoggedInError: Resource.Error<Boolean>? = null,
    registerError: Resource.Error<Unit>? = null,
    loginError: Resource.Error<Boolean>? = null,
    saveUserError: Resource.Error<Unit>? = null,
    getUserError: Resource.Error<User?>? = null,
) {
    coEvery { isUserLoggedIn() }.answers { isLoggedInError ?: Resource.Success(isLoggedIn) }
    coEvery { registerUser(any(), any()) }.answers { registerError ?: Resource.Success() }
    coEvery { loginUser(any(), any()) }.answers { loginError ?: Resource.Success(true) }
    coEvery { getUser(any()) }.answers { getUserError ?: Resource.Success(user) }
    coEvery { saveUser(any()) }.answers { saveUserError ?: Resource.Success() }
}

fun BuddyRemoteDao.mockBuddyDao(
    buddies: List<Buddy> = emptyList(),
    loadBuddiesError: Resource.Error<List<Buddy>>? = null,
    updateCooldownError: Resource.Error<Unit>? = null,
) {
    coEvery { loadBuddies(any(), any()) }.answers { loadBuddiesError ?: Resource.Success(buddies) }
    coEvery { updateCooldown(any(), any(), any()) }.answers {
        updateCooldownError ?: Resource.Success()
    }
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
}