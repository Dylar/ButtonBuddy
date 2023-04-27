package de.bitb.buttonbuddy.usecase.buddies

import de.bitb.buttonbuddy.data.BuddyRepository
import de.bitb.buttonbuddy.data.UserRepository
import de.bitb.buttonbuddy.core.misc.Resource
import de.bitb.buttonbuddy.core.misc.asResourceError

class ScanBuddyUC(
    private val userRepo: UserRepository,
    private val buddyRepo: BuddyRepository,
) {
    suspend operator fun invoke(uuid: String): Resource<Unit> {
        val userResp = userRepo.getLocalUser()
        if (userResp is Resource.Error) {
            return userResp.castTo()
        }
        val user = userResp.data!!
        val loadBuddiesResp = buddyRepo.loadBuddies(user.uuid, listOf(uuid))
        if (loadBuddiesResp is Resource.Error) {
            return loadBuddiesResp.castTo()
        }

        loadBuddiesResp.data!!.firstOrNull() // check if buddy exists
            ?: return "No Buddy found".asResourceError()
        user.buddies.add(uuid)
        val saveUserResp = userRepo.saveUser(user)
        if (saveUserResp is Resource.Error) {
            return saveUserResp.castTo()
        }
        return Resource.Success()
    }
}