package de.bitb.buttonbuddy.usecase.buddies

import de.bitb.buttonbuddy.data.BuddyRepository
import de.bitb.buttonbuddy.data.UserRepository
import de.bitb.buttonbuddy.data.model.Buddy
import de.bitb.buttonbuddy.core.misc.Resource

class ScanBuddyUC(
    private val userRepo: UserRepository,
    private val buddyRepo: BuddyRepository,
) {
    suspend operator fun invoke(uuid: String): Resource<Buddy> {
        val loadBuddiesResp = buddyRepo.loadBuddies(listOf(uuid))
        if (loadBuddiesResp is Resource.Error) {
            return Resource.Error(loadBuddiesResp.message!!)
        }

        val buddy = loadBuddiesResp.data!!.firstOrNull()
            ?: return Resource.Error("No Buddy found")
        val userResp = userRepo.getUser()
        if (userResp is Resource.Error) {
            return Resource.Error(userResp.message!!)
        }

        val user = userResp.data!!
        user.buddies.add(uuid)
        val saveUserResp = userRepo.saveUser(user)
        if (saveUserResp is Resource.Error) {
            return Resource.Error(saveUserResp.message!!)
        }
        return Resource.Success(buddy)
    }
}