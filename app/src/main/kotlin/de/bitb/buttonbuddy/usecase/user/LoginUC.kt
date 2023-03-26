package de.bitb.buttonbuddy.usecase.user

import de.bitb.buttonbuddy.R
import de.bitb.buttonbuddy.data.BuddyRepository
import de.bitb.buttonbuddy.data.UserRepository
import de.bitb.buttonbuddy.data.model.User
import de.bitb.buttonbuddy.core.misc.Resource
import de.bitb.buttonbuddy.ui.base.composable.ResString
import de.bitb.buttonbuddy.ui.base.composable.ResString.*
import java.util.*

sealed class LoginResponse(val message: ResString) {
    class LoggedIn : LoginResponse(ResourceString(R.string.ok))
    class FirstNameEmpty : LoginResponse(ResourceString(R.string.firstname_is_empty))
    class LastNameEmpty : LoginResponse(ResourceString(R.string.lastname_is_empty))
    class ErrorThrown<T>(error: Resource.Error<T>) :
        LoginResponse(error.message ?: DynamicString("Error thrown"))

    val asError: Resource<LoginResponse>
        get() = Resource.Error(message, this)
}

class LoginUC(
    private val userRepo: UserRepository,
    private val buddyRepo: BuddyRepository,
) {
    suspend operator fun invoke(firstName: String, lastName: String): Resource<LoginResponse> {
        if (firstName.isBlank()) {
            return LoginResponse.FirstNameEmpty().asError
        }
        if (lastName.isBlank()) {
            return LoginResponse.LastNameEmpty().asError
        }

        val loadUserResp = userRepo.loadUser(firstName, lastName)
        if (loadUserResp is Resource.Error) {
            return LoginResponse.ErrorThrown(loadUserResp).asError
        }

        val user = loadUserResp.data
            ?: User(
                firstName = firstName,
                lastName = lastName,
                uuid = UUID.randomUUID().toString(),
            )
        val buddies = user.buddies
        if (buddies.isNotEmpty()) {
            val loadBuddiesResp = buddyRepo.loadBuddies(buddies)
            if (loadBuddiesResp is Resource.Error) {
                return LoginResponse.ErrorThrown(loadBuddiesResp).asError
            }
        }

        val saveUserResp = userRepo.saveUser(user)
        if (saveUserResp is Resource.Error) {
            return LoginResponse.ErrorThrown(saveUserResp).asError
        }

        return Resource.Success(LoginResponse.LoggedIn())
    }
}