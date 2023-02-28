package de.bitb.buttonbuddy.usecase.info

import de.bitb.buttonbuddy.data.InfoRepository
import de.bitb.buttonbuddy.core.misc.Resource

class UpdateTokenUC(
    private val infoRepo: InfoRepository,
) {
    suspend operator fun invoke(newToken: String): Resource<Unit> {
        return infoRepo.updateToken(newToken)
    }
}