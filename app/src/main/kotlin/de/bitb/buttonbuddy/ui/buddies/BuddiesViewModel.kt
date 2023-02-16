package de.bitb.buttonbuddy.ui.buddies

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import de.bitb.buttonbuddy.data.BuddyRepository
import de.bitb.buttonbuddy.data.InfoRepository
import de.bitb.buttonbuddy.data.model.Buddy
import de.bitb.buttonbuddy.data.model.Info
import de.bitb.buttonbuddy.usecase.buddies.BuddyUseCases
import javax.inject.Inject

@HiltViewModel
class BuddiesViewModel @Inject constructor(
    private val useCases: BuddyUseCases,
    private val buddyRepo: BuddyRepository,
    infoRepo: InfoRepository,
) : ViewModel() {

    val buddies: LiveData<List<Buddy>> = buddyRepo.getBuddies()
    val info: LiveData<Info> = infoRepo.getLiveInfo()

    fun sendMessage(buddy: Buddy) {
        useCases.sendMessage(buddy)
    }

}

