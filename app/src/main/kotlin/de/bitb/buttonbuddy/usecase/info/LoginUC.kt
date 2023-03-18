package de.bitb.buttonbuddy.usecase.info

import de.bitb.buttonbuddy.R
import de.bitb.buttonbuddy.data.BuddyRepository
import de.bitb.buttonbuddy.data.InfoRepository
import de.bitb.buttonbuddy.data.model.Info
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
    private val infoRepo: InfoRepository,
    private val buddyRepo: BuddyRepository,
) {
    suspend operator fun invoke(firstName: String, lastName: String): Resource<LoginResponse> {
        if (firstName.isBlank()) {
            return LoginResponse.FirstNameEmpty().asError
        }
        if (lastName.isBlank()) {
            return LoginResponse.LastNameEmpty().asError
        }

        val loadInfoResp = infoRepo.loadInfo(firstName, lastName)
        if (loadInfoResp is Resource.Error) {
            return LoginResponse.ErrorThrown(loadInfoResp).asError
        }

        val info = loadInfoResp.data
            ?: Info(
                firstName = firstName,
                lastName = lastName,
                uuid = UUID.randomUUID().toString(),
            )
        val buddies = info.buddies
        if (buddies.isNotEmpty()) {
            val loadBuddiesResp = buddyRepo.loadBuddies(buddies)
            if (loadBuddiesResp is Resource.Error) {
                return LoginResponse.ErrorThrown(loadBuddiesResp).asError
            }
        }

        val saveInfoResp = infoRepo.saveInfo(info)
        if (saveInfoResp is Resource.Error) {
            return LoginResponse.ErrorThrown(saveInfoResp).asError
        }

        return Resource.Success(LoginResponse.LoggedIn())
    }
}