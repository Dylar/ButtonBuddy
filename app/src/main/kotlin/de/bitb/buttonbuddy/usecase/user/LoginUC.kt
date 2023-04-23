package de.bitb.buttonbuddy.usecase.user

import de.bitb.buttonbuddy.R
import de.bitb.buttonbuddy.data.BuddyRepository
import de.bitb.buttonbuddy.data.UserRepository
import de.bitb.buttonbuddy.core.misc.Resource
import de.bitb.buttonbuddy.data.SettingsRepository
import de.bitb.buttonbuddy.ui.base.composable.ResString
import de.bitb.buttonbuddy.ui.base.composable.ResString.*

sealed class LoginResponse(val message: ResString) {
    class LoggedIn : LoginResponse(ResourceString(R.string.ok))
    class UserEmpty : LoginResponse(ResourceString(R.string.email_is_empty))
    class PwEmpty : LoginResponse(ResourceString(R.string.pw_is_empty))
    class UserNotFound : LoginResponse(ResourceString(R.string.user_not_found))
    class ErrorThrown<T>(error: Resource.Error<T>) :
        LoginResponse(error.message ?: DynamicString("Error thrown"))

    val asError: Resource<LoginResponse>
        get() = Resource.Error(message, this)
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
        val response = isValid(email, pw)
        if (response != null) {
            return response.asError
        }

        val loginUserResp = userRepo.loginUser(email, pw)
        if (loginUserResp is Resource.Error) {
            return LoginResponse.ErrorThrown(loginUserResp).asError
        }

        if (!loginUserResp.hasData) {
            return LoginResponse.UserNotFound().asError
        }
        val user = loginUserResp.data!!
        val buddies = user.buddies
        if (buddies.isNotEmpty()) {
            val loadBuddiesResp = buddyRepo.loadBuddies(user.uuid, buddies)
            if (loadBuddiesResp is Resource.Error) {
                return LoginResponse.ErrorThrown(loadBuddiesResp).asError
            }
        }

        val saveUserResp = userRepo.saveUser(user)
        if (saveUserResp is Resource.Error) {
            return LoginResponse.ErrorThrown(saveUserResp).asError
        }

        val loadSettingsResp = settingsRepo.loadSettings(user.uuid)
        if (loadSettingsResp is Resource.Error) {
            return Resource.Error(loadSettingsResp.message!!)
        }

        return Resource.Success(LoginResponse.LoggedIn())
    }

    private fun isValid(email: String, pw: String): LoginResponse? {
        if (email.isBlank()) {
            return LoginResponse.UserEmpty()
        }
        if (pw.isBlank()) {
            return LoginResponse.PwEmpty()
        }
        return null
    }
}