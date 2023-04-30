package de.bitb.buttonbuddy.usecase.user

import de.bitb.buttonbuddy.R
import de.bitb.buttonbuddy.core.misc.Resource
import de.bitb.buttonbuddy.data.UserRepository
import de.bitb.buttonbuddy.data.model.User
import de.bitb.buttonbuddy.ui.base.composable.ResString
import de.bitb.buttonbuddy.ui.base.composable.ResString.DynamicString
import de.bitb.buttonbuddy.ui.base.composable.ResString.ResourceString
import java.util.*

sealed class RegisterResponse(val message: ResString) {
    class Registered : RegisterResponse(ResourceString(R.string.ok))
    class FirstNameEmpty : RegisterResponse(ResourceString(R.string.firstname_is_empty))
    class LastNameEmpty : RegisterResponse(ResourceString(R.string.lastname_is_empty))
    class EmailEmpty : RegisterResponse(ResourceString(R.string.email_is_empty))
    class PWEmpty : RegisterResponse(ResourceString(R.string.pw_is_empty))
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
        email: String,
        pw1: String,
        pw2: String,
    ): Resource<RegisterResponse> {
        val isValid = isValid(firstName, lastName, email, pw1, pw2)
        if (isValid != null) {
            return isValid.asError
        }

        val registerResp = userRepo.registerUser(email, pw1)
        if (registerResp is Resource.Error) {
            return RegisterResponse.ErrorThrown(registerResp).asError
        }

        val user = User(
            firstName = firstName,
            lastName = lastName,
            email = email,
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
        email: String,
        pw1: String,
        pw2: String
    ): RegisterResponse? {
        if (firstName.isBlank()) {
            return RegisterResponse.FirstNameEmpty()
        }
        if (lastName.isBlank()) {
            return RegisterResponse.LastNameEmpty()
        }
        if (email.isBlank()) { // TODO check if exists
            return RegisterResponse.EmailEmpty()
        }
        if (pw1.isBlank() || pw2.isBlank()) {
            return RegisterResponse.PWEmpty()
        }
        if (pw1 != pw2) { // TODO make more checks
            return RegisterResponse.PWNotSame()
        }
        return null
    }
}