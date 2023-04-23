package de.bitb.buttonbuddy.usecase.buddies

import de.bitb.buttonbuddy.core.misc.Resource
import de.bitb.buttonbuddy.core.misc.calculateMilliseconds
import de.bitb.buttonbuddy.data.BuddyRepository
import de.bitb.buttonbuddy.data.UserRepository
import de.bitb.buttonbuddy.data.model.Buddy

class SetCooldownUC(
    private val userRepo: UserRepository,
    private val buddyRepo: BuddyRepository,
) {
    suspend operator fun invoke(buddy: Buddy, hour: Int, min: Int): Resource<Unit> {
        val userResp = userRepo.getUser()
        if (userResp is Resource.Error) {
            return Resource.Error(userResp.message!!)
        }

        val cooldown = calculateMilliseconds(hour, min)
        val xBuddy = buddy.copy(cooldown = cooldown)
        return buddyRepo.saveCooldown(userResp.data!!.uuid, xBuddy, cooldown)
    }
}