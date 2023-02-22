package de.bitb.buttonbuddy.usecase.buddies

import de.bitb.buttonbuddy.data.BuddyRepository
import de.bitb.buttonbuddy.data.InfoRepository
import de.bitb.buttonbuddy.exceptions.NoInfoException
import de.bitb.buttonbuddy.misc.Resource
import de.bitb.buttonbuddy.misc.observeOnce
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class LoadBuddiesUC(
    private val infoRepo: InfoRepository,
    private val buddyRepo: BuddyRepository,
) {
    suspend operator fun invoke(): Resource<Unit> {
        val getInfoResp = infoRepo.getInfo()
        if (getInfoResp is Resource.Error) {
            return Resource.Error(getInfoResp.message!!)
        }
        if (getInfoResp.hasData) {
            val loadBuddiesResp = buddyRepo.loadBuddies(getInfoResp.data!!.buddies)
            if (loadBuddiesResp is Resource.Error) {
                return Resource.Error(loadBuddiesResp.message!!)
            }
        }
        return Resource.Success(Unit)
    }
}