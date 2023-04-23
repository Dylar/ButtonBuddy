package de.bitb.buttonbuddy.usecase.buddies

import de.bitb.buttonbuddy.data.BuddyRepository
import de.bitb.buttonbuddy.data.UserRepository
import de.bitb.buttonbuddy.data.model.Buddy
import de.bitb.buttonbuddy.core.misc.Resource

class ScanBuddyUC(
    private val userRepo: UserRepository,
    private val buddyRepo: BuddyRepository,
) {
    suspend operator fun invoke(uuid: String): Resource<Unit> {
        val userResp = userRepo.getUser()
        if (userResp is Resource.Error) {
            return Resource.Error(userResp.message!!)
        }
        val user = userResp.data!!
        val loadBuddiesResp = buddyRepo.loadBuddies(user.uuid, listOf(uuid))
        if (loadBuddiesResp is Resource.Error) {
            return Resource.Error(loadBuddiesResp.message!!)
        }

        loadBuddiesResp.data!!.firstOrNull() // check if buddy exists
            ?: return Resource.Error("No Buddy found")
        user.buddies.add(uuid)
        val saveUserResp = userRepo.saveUser(user)
        if (saveUserResp is Resource.Error) {
            return Resource.Error(saveUserResp.message!!)
        }
        return Resource.Success(Unit)
    }
}