package de.bitb.buttonbuddy.usecase.user

import de.bitb.buttonbuddy.data.BuddyRepository
import de.bitb.buttonbuddy.data.UserRepository
import de.bitb.buttonbuddy.core.misc.Resource
import de.bitb.buttonbuddy.core.misc.asResourceError
import de.bitb.buttonbuddy.core.misc.tryIt
import de.bitb.buttonbuddy.data.SettingsRepository

class LoadDataUC(
    private val settingsRepo: SettingsRepository,
    private val userRepo: UserRepository,
    private val buddyRepo: BuddyRepository,
) {
    suspend operator fun invoke(): Resource<Boolean> {
        return tryIt {
            val userLoggedInResp = userRepo.isUserLoggedIn()
            if (userLoggedInResp is Resource.Error) {
                return@tryIt userLoggedInResp
            }
            val userResp = userRepo.getLocalUser()
            if (userResp is Resource.Error) {
                userResp.castTo<Boolean>()
            }

            if (!userResp.hasData) {
                "No user found".asResourceError<Boolean>()
            }

            val user = userResp.data!!
            val loadUserResp = userRepo.loadUser(user.email)
            if (loadUserResp is Resource.Error) {
                loadUserResp.castTo<Boolean>()
            }

            val buddies = user.buddies
            if (buddies.isNotEmpty()) {
                val loadBuddiesResp = buddyRepo.loadBuddies(user.uuid, buddies)
                if (loadBuddiesResp is Resource.Error) {
                    loadBuddiesResp.castTo<Boolean>()
                }
            }

            val loadSettingsResp = settingsRepo.loadSettings(user.uuid)
            if (loadSettingsResp is Resource.Error) {
                loadSettingsResp.castTo<Boolean>()
            }
            return@tryIt Resource.Success(true)
        }
    }
}