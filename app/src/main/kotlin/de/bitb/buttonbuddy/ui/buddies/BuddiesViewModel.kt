package de.bitb.buttonbuddy.ui.buddies

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.bitb.buttonbuddy.data.BuddyRepository
import de.bitb.buttonbuddy.data.InfoRepository
import de.bitb.buttonbuddy.data.model.Buddy
import de.bitb.buttonbuddy.data.model.Info
import de.bitb.buttonbuddy.misc.Resource
import de.bitb.buttonbuddy.ui.base.BaseViewModel
import de.bitb.buttonbuddy.usecase.buddies.BuddyUseCases
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BuddiesViewModel @Inject constructor(
    private val useCases: BuddyUseCases,
    buddyRepo: BuddyRepository,
    infoRepo: InfoRepository,
) : BaseViewModel() {

    val info: LiveData<Info> = infoRepo.getLiveInfo()
    val buddies: LiveData<List<Buddy>> = buddyRepo.getLiveBuddies()

    fun sendMessage(buddy: Buddy) {
        viewModelScope.launch {
            when (val result = useCases.sendMessage(buddy)) {
                is Resource.Error -> showSnackbar(result.message!!.rawString())
                is Resource.Success -> showSnackbar("Nachricht gesendet")
            }
        }
    }
}
