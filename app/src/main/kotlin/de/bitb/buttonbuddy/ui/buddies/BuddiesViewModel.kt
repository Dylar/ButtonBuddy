package de.bitb.buttonbuddy.ui.buddies

import androidx.lifecycle.LiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import de.bitb.buttonbuddy.data.BuddyRepository
import de.bitb.buttonbuddy.data.InfoRepository
import de.bitb.buttonbuddy.data.model.Buddy
import de.bitb.buttonbuddy.data.model.Info
import de.bitb.buttonbuddy.ui.base.BaseViewModel
import de.bitb.buttonbuddy.usecase.buddies.BuddyUseCases
import javax.inject.Inject

@HiltViewModel
class BuddiesViewModel @Inject constructor(
    private val useCases: BuddyUseCases,
    private val buddyRepo: BuddyRepository,
    private val infoRepo: InfoRepository,
) : BaseViewModel() {

    val info: LiveData<Info> = infoRepo.getLiveInfo()
    val buddies: LiveData<List<Buddy>> = buddyRepo.getLiveBuddies()

    fun sendMessage(buddy: Buddy) {
        useCases.sendMessage(buddy)
    }
}

