package de.bitb.buttonbuddy.usecase.user

import de.bitb.buttonbuddy.R
import de.bitb.buttonbuddy.core.misc.Resource
import de.bitb.buttonbuddy.data.UserRepository
import de.bitb.buttonbuddy.data.model.User
import de.bitb.buttonbuddy.ui.base.composable.ResString
import de.bitb.buttonbuddy.ui.base.composable.ResString.DynamicString
import de.bitb.buttonbuddy.ui.base.composable.asResString
import java.util.*

sealed class RegisterResponse(val message: ResString) {
    object Registered : RegisterResponse(R.string.ok.asResString())
    object FirstNameEmpty : RegisterResponse(R.string.firstname_is_empty.asResString())
    object LastNameEmpty : RegisterResponse(R.string.lastname_is_empty.asResString())
    sealed class EmailError(msg: ResString) : RegisterResponse(msg) {
        object EmailEmpty : EmailError(R.string.email_is_empty.asResString())
        object EmailInvalidFormat : EmailError(R.string.email_wrong_format.asResString())
    }

    sealed class PWError(msg: ResString) : RegisterResponse(msg) {
        object PWEmpty : PWError(R.string.pw_is_empty.asResString())
        object PWNotSame : PWError(R.string.pw_not_same.asResString())
        object PWLengthTooShort : PWError(R.string.pw_length_too_short.asResString())
        object PWMissingUppercase : PWError(R.string.pw_missing_upper_case.asResString())
        object PWMissingLowercase : PWError(R.string.pw_missing_lower_case.asResString())
        object PWMissingDigit : PWError(R.string.pw_missing_digit.asResString())
        object PWMissingSpecialCharacter : PWError(R.string.pw_missing_special.asResString())
    }

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

        return Resource.Success(RegisterResponse.Registered)
    }

    private fun isValid(
        firstName: String,
        lastName: String,
        email: String,
        pw1: String,
        pw2: String
    ): RegisterResponse? {
        if (firstName.isBlank()) {
            return RegisterResponse.FirstNameEmpty
        }
        if (lastName.isBlank()) {
            return RegisterResponse.LastNameEmpty
        }

        val emailValidation = validateEmail(email)
        if (emailValidation != null) {
            return emailValidation
        }
        return validatePassword(pw1, pw2)
    }

    private fun validateEmail(email: String): RegisterResponse? {
        if (email.isBlank()) { // TODO check if already exists
            return RegisterResponse.EmailError.EmailEmpty
        }

        val emailRegex = Regex("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}")
        if (!emailRegex.matches(email)) {
            return RegisterResponse.EmailError.EmailInvalidFormat
        }

        return null
    }

    private fun validatePassword(pw1: String, pw2: String): RegisterResponse? {
        if (pw1.isBlank() || pw2.isBlank()) {
            return RegisterResponse.PWError.PWEmpty
        }
        if (pw1 != pw2) {
            return RegisterResponse.PWError.PWNotSame
        }

        if (pw1.length < 8) {
            return RegisterResponse.PWError.PWLengthTooShort
        }

        if (!pw1.contains(Regex("[A-Z]"))) {
            return RegisterResponse.PWError.PWMissingUppercase
        }

        if (!pw1.contains(Regex("[a-z]"))) {
            return RegisterResponse.PWError.PWMissingLowercase
        }

        if (!pw1.contains(Regex("[0-9]"))) {
            return RegisterResponse.PWError.PWMissingDigit
        }

        if (!pw1.contains(Regex("[!@#\$%^&*()]"))) {
            return RegisterResponse.PWError.PWMissingSpecialCharacter
        }
        return null
    }
}