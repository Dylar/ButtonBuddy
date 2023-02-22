package de.bitb.buttonbuddy.usecase.info

import de.bitb.buttonbuddy.data.InfoRepository
import de.bitb.buttonbuddy.data.model.Info
import de.bitb.buttonbuddy.misc.Resource
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class UpdateTokenUC(
    private val infoRepo: InfoRepository,
) {
    suspend operator fun invoke(newToken: String): Resource<Unit> {
        return infoRepo.updateToken(newToken)
    }
}