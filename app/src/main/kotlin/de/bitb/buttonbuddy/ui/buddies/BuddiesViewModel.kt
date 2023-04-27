package de.bitb.buttonbuddy.ui.buddies

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.bitb.buttonbuddy.R
import de.bitb.buttonbuddy.data.BuddyRepository
import de.bitb.buttonbuddy.data.model.Buddy
import de.bitb.buttonbuddy.core.misc.Resource
import de.bitb.buttonbuddy.data.MessageRepository
import de.bitb.buttonbuddy.data.SettingsRepository
import de.bitb.buttonbuddy.data.model.Message
import de.bitb.buttonbuddy.ui.base.BaseViewModel
import de.bitb.buttonbuddy.ui.base.composable.ResString
import de.bitb.buttonbuddy.usecase.MessageUseCases
import de.bitb.buttonbuddy.usecase.UserUseCases
import de.bitb.buttonbuddy.usecase.message.SendMessageDelegate
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BuddiesViewModel @Inject constructor(
    override val messageUC: MessageUseCases,
    override val settingsRepo: SettingsRepository,
    private val userUC: UserUseCases,
    private val msgRepo: MessageRepository,
    buddyRepo: BuddyRepository,
) : BaseViewModel(), SendMessageDelegate {

    val isRefreshing = mutableStateOf(false)
    val buddies: LiveData<List<Buddy>> = buddyRepo.getLiveBuddies()
    fun getLastMessage(uuid: String): LiveData<Message?> = msgRepo.getLiveLastMessage(uuid)

    fun refreshData() {
        isRefreshing.value = true
        viewModelScope.launch {
            when (val resp = userUC.loadDataUC()) {
                is Resource.Error -> showSnackbar(resp.message!!)
                is Resource.Success -> showSnackbar(ResString.ResourceString(R.string.buddies_loaded))
            }
            isRefreshing.value = false
        }
    }

    fun sendMessageToBuddy(buddy: Buddy) = sendMessage(buddy)
}
