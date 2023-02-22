package de.bitb.buttonbuddy.usecase.info

import de.bitb.buttonbuddy.data.BuddyRepository
import de.bitb.buttonbuddy.data.InfoRepository
import de.bitb.buttonbuddy.data.model.Info
import de.bitb.buttonbuddy.misc.Resource
import java.util.*

sealed class LoginResponse(val message: String) {
    class LoggedIn : LoginResponse("OK")
    class FirstNameEmpty : LoginResponse("Vorname darf nicht leer sein")
    class LastNameEmpty : LoginResponse("Nachname darf nicht leer sein")
    class ErrorThrown<T>(error: Resource.Error<T>) :
        LoginResponse(error.message?.rawString() ?: "Error thrown")

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
        val saveInfoResp = infoRepo.saveInfo(info)
        if (saveInfoResp is Resource.Error) {
            return LoginResponse.ErrorThrown(saveInfoResp).asError
        }

        val loadBuddiesResp = buddyRepo.loadBuddies(info.buddies)
        if (loadBuddiesResp is Resource.Error) {
            return LoginResponse.ErrorThrown(loadBuddiesResp).asError
        }

        return Resource.Success(LoginResponse.LoggedIn())
    }
}