package de.bitb.buttonbuddy.usecase.buddies

import de.bitb.buttonbuddy.data.BuddyRepository
import de.bitb.buttonbuddy.data.InfoRepository
import de.bitb.buttonbuddy.exceptions.NoInfoException
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ScanBuddyUC(
    private val infoRepo: InfoRepository,
    private val buddyRepo: BuddyRepository,
) {
    @Throws(NoInfoException::class)
    suspend operator fun invoke(buddyToken: String) {
        buddyRepo.loadBuddies(listOf(buddyToken))
        val info = infoRepo.getInfo() ?: throw NoInfoException()
        info.buddies.add(buddyToken)
        infoRepo.saveInfo(info)
    }
}