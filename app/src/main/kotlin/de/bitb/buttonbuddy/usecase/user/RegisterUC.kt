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
    class UserNameEmpty : RegisterResponse(ResourceString(R.string.user_is_empty))
    class PWNotSame : RegisterResponse(ResourceString(R.string.pw_not_same))
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
        userName: String,
        pw1: String,
        pw2: String,
    ): Resource<RegisterResponse> {
        val response = isValid(firstName, lastName, userName, pw1, pw2)
        if (response != null) {
            return response.asError
        }

        val registerResp = userRepo.registerUser(userName, pw1)
        if (registerResp is Resource.Error) {
            return RegisterResponse.ErrorThrown(registerResp).asError
        }

        val user = User(
            firstName = firstName,
            lastName = lastName,
            userName = userName,
            uuid = UUID.randomUUID().toString(),
        )

        val saveUserResp = userRepo.saveUser(user)
        if (saveUserResp is Resource.Error) {
            return RegisterResponse.ErrorThrown(saveUserResp).asError
        }

        return Resource.Success(RegisterResponse.Registered())
    }

    private fun isValid(
        firstName: String,
        lastName: String,
        userName: String,
        pw1: String,
        pw2: String
    ): RegisterResponse? {
        if (firstName.isBlank()) {
            return RegisterResponse.FirstNameEmpty()
        }
        if (lastName.isBlank()) {
            return RegisterResponse.LastNameEmpty()
        }
        if (userName.isBlank()) {
            return RegisterResponse.UserNameEmpty()
        }
        if (pw1 != pw2) { // TODO make more checks
            return RegisterResponse.PWNotSame()
        }
        return null
    }
}