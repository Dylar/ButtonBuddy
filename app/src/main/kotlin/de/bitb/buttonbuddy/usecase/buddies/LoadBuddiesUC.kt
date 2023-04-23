package de.bitb.buttonbuddy.usecase.buddies

import de.bitb.buttonbuddy.data.BuddyRepository
import de.bitb.buttonbuddy.data.UserRepository
import de.bitb.buttonbuddy.core.misc.Resource

class LoadBuddiesUC(
    private val userRepo: UserRepository,
    private val buddyRepo: BuddyRepository,
) {
    suspend operator fun invoke(): Resource<Unit> {
        val userResp = userRepo.getUser()
        if (userResp is Resource.Error) {
            return Resource.Error(userResp.message!!)
        }
        if (userResp.hasData) {
            val user = userResp.data!!
            val loadBuddiesResp = buddyRepo.loadBuddies(user.uuid, user.buddies)
            if (loadBuddiesResp is Resource.Error) {
                return Resource.Error(loadBuddiesResp.message!!)
            }
        }
        return Resource.Success(Unit)
    }
}