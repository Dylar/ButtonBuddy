package de.bitb.buttonbuddy.usecase.user

import de.bitb.buttonbuddy.R
import de.bitb.buttonbuddy.core.misc.Resource
import de.bitb.buttonbuddy.data.BuddyRepository
import de.bitb.buttonbuddy.data.SettingsRepository
import de.bitb.buttonbuddy.data.UserRepository
import de.bitb.buttonbuddy.ui.base.composable.ResString
import de.bitb.buttonbuddy.ui.base.composable.ResString.DynamicString
import de.bitb.buttonbuddy.ui.base.composable.asResString

sealed class LoginResponse(val message: ResString) {
    class LoggedIn : LoginResponse(R.string.ok.asResString())
    sealed class EmailError(msg: ResString) : LoginResponse(msg) {
        object EmailEmpty : EmailError(R.string.email_is_empty.asResString())
        object EmailInvalidFormat : EmailError(R.string.email_wrong_format.asResString())
    }

    object PwEmpty : LoginResponse(R.string.pw_is_empty.asResString())
    object UserNotFound : LoginResponse(R.string.user_not_found.asResString())
    class ErrorThrown<T>(error: Resource.Error<T>) :
        LoginResponse(error.message ?: DynamicString("Error thrown"))

    val asError: Resource<LoginResponse>
        get() = Resource.Error(message, this)
}

fun <T> Resource.Error<T>.asError(): Resource<LoginResponse> {
    return Resource.Error(message!!, LoginResponse.ErrorThrown(this))
}

class LoginUC(
    private val settingsRepo: SettingsRepository,
    private val userRepo: UserRepository,
    private val buddyRepo: BuddyRepository,
) {
    suspend operator fun invoke(
        email: String,
        pw: String
    ): Resource<LoginResponse> {
        val isValid = isValid(email, pw)
        if (isValid != null) {
            return isValid.asError
        }

        val loginUserResp = userRepo.loginUser(email, pw)
        if (loginUserResp is Resource.Error) {
            return loginUserResp.asError()
        }

        if (!loginUserResp.hasData) {
            return LoginResponse.UserNotFound.asError
        }
        val user = loginUserResp.data!!
        val buddies = user.buddies
        if (buddies.isNotEmpty()) {
            val loadBuddiesResp = buddyRepo.loadBuddies(user.uuid, buddies)
            if (loadBuddiesResp is Resource.Error) {
                return loadBuddiesResp.asError()
            }
        }

        val saveUserResp = userRepo.saveUser(user)
        if (saveUserResp is Resource.Error) {
            return saveUserResp.asError()
        }

        val loadSettingsResp = settingsRepo.loadSettings(user.uuid)
        if (loadSettingsResp is Resource.Error) {
            return loadSettingsResp.castTo()
        }

        return Resource.Success(LoginResponse.LoggedIn())
    }

    private fun isValid(email: String, pw: String): LoginResponse? {
        val emailValid =  validateEmail(email)
        if (emailValid != null){
            return emailValid
        }

        if (pw.isBlank()) {
            return LoginResponse.PwEmpty
        }
        return null
    }

    private fun validateEmail(email: String): LoginResponse? {
        if (email.isBlank()) {
            return LoginResponse.EmailError.EmailEmpty
        }

        val emailRegex = Regex("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}")
        if (!emailRegex.matches(email)) {
            return LoginResponse.EmailError.EmailInvalidFormat
        }

        return null
    }
}