package de.bitb.buttonbuddy.usecase.buddies

import de.bitb.buttonbuddy.data.BuddyRepository
import de.bitb.buttonbuddy.data.InfoRepository
import de.bitb.buttonbuddy.data.model.Buddy
import de.bitb.buttonbuddy.misc.Resource

class ScanBuddyUC(
    private val infoRepo: InfoRepository,
    private val buddyRepo: BuddyRepository,
) {
    suspend operator fun invoke(uuid: String): Resource<Buddy> {
        val loadBuddiesResp = buddyRepo.loadBuddies(listOf(uuid))
        if (loadBuddiesResp is Resource.Error) {
            return Resource.Error(loadBuddiesResp.message!!)
        }

        val buddy = loadBuddiesResp.data!!.firstOrNull()
            ?: return Resource.Error("No Buddy found")
        val getInfoResp = infoRepo.getInfo()
        if (getInfoResp is Resource.Error) {
            return Resource.Error(getInfoResp.message!!)
        }

        val info = getInfoResp.data!!
        info.buddies.add(uuid)
        val saveInfoResp = infoRepo.saveInfo(info)
        if (saveInfoResp is Resource.Error) {
            return Resource.Error(saveInfoResp.message!!)
        }
        return Resource.Success(buddy)
    }
}