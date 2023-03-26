package de.bitb.buttonbuddy.usecase.user

import de.bitb.buttonbuddy.R
import de.bitb.buttonbuddy.data.UserRepository
import de.bitb.buttonbuddy.data.model.User
import de.bitb.buttonbuddy.core.misc.Resource
import de.bitb.buttonbuddy.ui.base.composable.ResString
import de.bitb.buttonbuddy.ui.base.composable.ResString.*
import java.util.*

sealed class RegisterResponse(val message: ResString) {
    class Registered : RegisterResponse(ResourceString(R.string.ok))
    class FirstNameEmpty : RegisterResponse(ResourceString(R.string.firstname_is_empty))
    class LastNameEmpty : RegisterResponse(ResourceString(R.string.lastname_is_empty))
    class ErrorThrown<T>(error: Resource.Error<T>) :
        RegisterResponse(error.message ?: DynamicString("Error thrown"))

    val asError: Resource<RegisterResponse>
        get() = Resource.Error(message, this)
}

class RegisterUC(
    private val userRepo: UserRepository,
) {
    suspend operator fun invoke(
        firstName: String,
        lastName: String,
        pw1: String,
        pw2: String,
    ): Resource<LoginResponse> {
        if (firstName.isBlank()) {
            return LoginResponse.FirstNameEmpty().asError
        }
        if (lastName.isBlank()) {
            return LoginResponse.LastNameEmpty().asError
        }

        val userResp = userRepo.loadUser(firstName, lastName)
        if (userResp is Resource.Error) {
            return LoginResponse.ErrorThrown(userResp).asError
        }

        val user = userResp.data
            ?: User(
                firstName = firstName,
                lastName = lastName,
                uuid = UUID.randomUUID().toString(),
            )

        val saveUserResp = userRepo.saveUser(user)
        if (saveUserResp is Resource.Error) {
            return LoginResponse.ErrorThrown(saveUserResp).asError
        }

        return Resource.Success(LoginResponse.LoggedIn())
    }
}