package de.bitb.buttonbuddy.ui.buddy

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.bitb.buttonbuddy.core.misc.Resource
import de.bitb.buttonbuddy.data.BuddyRepository
import de.bitb.buttonbuddy.data.MessageRepository
import de.bitb.buttonbuddy.data.SettingsRepository
import de.bitb.buttonbuddy.data.model.Buddy
import de.bitb.buttonbuddy.data.model.Message
import de.bitb.buttonbuddy.ui.base.BaseViewModel
import de.bitb.buttonbuddy.usecase.BuddyUseCases
import de.bitb.buttonbuddy.usecase.MessageUseCases
import de.bitb.buttonbuddy.ui.base.SendMessageDelegate
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BuddyViewModel @Inject constructor(
    override val messageUC: MessageUseCases,
    override val settingsRepo: SettingsRepository,
    private val msgRepo: MessageRepository,
    private val buddyRepo: BuddyRepository,
    private val buddyUC: BuddyUseCases,
) : BaseViewModel(), SendMessageDelegate {

    lateinit var buddy: LiveData<Buddy>
    lateinit var messages: LiveData<List<Message>>

    fun initLiveState(uuid: String) {
        buddy = buddyRepo.getLiveBuddy(uuid)
        messages = msgRepo.getLiveMessages(uuid)
    }

    fun sendMessageToBuddy(buddy: Buddy) = sendMessage(buddy)
    fun setCooldown(buddy: Buddy, h: Int, m: Int) {
        viewModelScope.launch {
            val result = buddyUC.setCooldownUC(buddy, h, m)
            if (result is Resource.Error) {
                showSnackbar(result.message!!)
            }
        }
    }
}

