package de.bitb.buttonbuddy.usecase.buddies

import de.bitb.buttonbuddy.data.BuddyRepository
import de.bitb.buttonbuddy.data.InfoRepository
import de.bitb.buttonbuddy.exceptions.NoInfoException
import de.bitb.buttonbuddy.misc.observeOnce
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class LoadBuddiesUC(
    private val infoRepo: InfoRepository,
    private val buddyRepo: BuddyRepository,
) {
    @Throws(NoInfoException::class)
    suspend operator fun invoke() {
        val info = infoRepo.getInfo() ?: throw NoInfoException()
        buddyRepo.loadBuddies(info.buddies)
    }
}