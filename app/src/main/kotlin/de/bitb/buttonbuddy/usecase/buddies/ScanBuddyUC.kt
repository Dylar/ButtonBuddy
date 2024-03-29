package de.bitb.buttonbuddy.usecase.buddies

import de.bitb.buttonbuddy.R
import de.bitb.buttonbuddy.core.misc.Resource
import de.bitb.buttonbuddy.core.misc.asResourceError
import de.bitb.buttonbuddy.core.misc.tryIt
import de.bitb.buttonbuddy.data.BuddyRepository
import de.bitb.buttonbuddy.data.UserRepository

class ScanBuddyUC(
    private val userRepo: UserRepository,
    private val buddyRepo: BuddyRepository,
) {
    suspend operator fun invoke(uuid: String): Resource<Unit> {
        return tryIt {
            val userResp = userRepo.getLocalUser()
            if (userResp is Resource.Error) {
                return@tryIt userResp.castTo()
            }
            val user = userResp.data!!
            val loadBuddiesResp = buddyRepo.loadBuddies(user.uuid, listOf(uuid))
            if (loadBuddiesResp is Resource.Error) {
                return@tryIt loadBuddiesResp.castTo()
            }

            loadBuddiesResp.data!!.firstOrNull() // check if buddy exists
                ?: return@tryIt R.string.no_buddy_found.asResourceError()
            user.buddies.add(uuid)
            val saveUserResp = userRepo.saveUser(user)
            if (saveUserResp is Resource.Error) {
                saveUserResp.castTo()
            } else {
                Resource.Success()
            }
        }
    }
}