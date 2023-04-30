package de.bitb.buttonbuddy.usecase.user

import de.bitb.buttonbuddy.core.misc.Resource
import de.bitb.buttonbuddy.data.UserRepository

class UpdateTokenUC(
    private val userRepo: UserRepository,
) {
    suspend operator fun invoke(newToken: String): Resource<Unit> {
        return userRepo.updateToken(newToken)
    }
}