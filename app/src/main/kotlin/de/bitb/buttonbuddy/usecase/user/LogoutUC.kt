package de.bitb.buttonbuddy.usecase.user

import de.bitb.buttonbuddy.core.misc.Resource
import de.bitb.buttonbuddy.core.misc.tryIt
import de.bitb.buttonbuddy.data.BuddyRepository
import de.bitb.buttonbuddy.data.MessageRepository
import de.bitb.buttonbuddy.data.UserRepository

class LogoutUC( // TODO write test
    private val userRepo: UserRepository,
    private val buddyRepo: BuddyRepository,
    private val msgRepo: MessageRepository,
) {
    suspend operator fun invoke(): Resource<Unit> {
        return tryIt {
            val logoutResp = userRepo.logoutUser()
            if (logoutResp is Resource.Error) {
                return@tryIt logoutResp
            }
            val clearUserResp = userRepo.clearUser()
            if (clearUserResp is Resource.Error) {
                return@tryIt clearUserResp
            }
            val clearBuddysResp = buddyRepo.clearBuddys()
            if (clearBuddysResp is Resource.Error) {
                return@tryIt clearBuddysResp
            }
            msgRepo.clearMessages()
        }
    }
}