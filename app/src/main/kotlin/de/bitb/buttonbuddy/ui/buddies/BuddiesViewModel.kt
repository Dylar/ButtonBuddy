package de.bitb.buttonbuddy.ui.buddies

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.bitb.buttonbuddy.data.BuddyRepository
import de.bitb.buttonbuddy.data.InfoRepository
import de.bitb.buttonbuddy.data.model.Buddy
import de.bitb.buttonbuddy.data.model.Info
import de.bitb.buttonbuddy.core.misc.Resource
import de.bitb.buttonbuddy.ui.base.BaseViewModel
import de.bitb.buttonbuddy.usecase.BuddyUseCases
import de.bitb.buttonbuddy.usecase.MessageUseCases
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BuddiesViewModel @Inject constructor(
    private val messageUC: MessageUseCases,
    private val buddyUC: BuddyUseCases,
    buddyRepo: BuddyRepository,
    infoRepo: InfoRepository,
) : BaseViewModel() {
    val isRefreshing = mutableStateOf(false)

    val info: LiveData<Info> = infoRepo.getLiveInfo()
    val buddies: LiveData<List<Buddy>> = buddyRepo.getLiveBuddies()

    fun refreshData() {
        isRefreshing.value = true
        System.out.println("refreshData")
        viewModelScope.launch {
            when (val resp = buddyUC.loadBuddiesUC()) {
                is Resource.Error -> {
                    System.out.println("Error")
                    showSnackbar(resp.message!!.rawString())
                }
                is Resource.Success -> {
                    showSnackbar("Buddys geladen")
                }
            }
            System.out.println("done")
            isRefreshing.value = false
        }
    }

    fun sendMessage(buddy: Buddy) {
        viewModelScope.launch {
            when (val resp = messageUC.sendMessageUC(buddy)) {
                is Resource.Error -> showSnackbar(resp.message!!.rawString())
                is Resource.Success -> showSnackbar("Nachricht gesendet")
            }
        }
    }
}
