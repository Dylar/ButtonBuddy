package de.bitb.buttonbuddy.usecase.info

import de.bitb.buttonbuddy.data.InfoRepository
import de.bitb.buttonbuddy.data.model.Info
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class UpdateToken(
    private val infoRepo: InfoRepository,
) {
    operator fun invoke(newToken: String) {
        GlobalScope.launch {
            val info = infoRepo.getInfo() ?: Info()
            infoRepo.saveInfo(info.copy(token = newToken))
        }
    }
}