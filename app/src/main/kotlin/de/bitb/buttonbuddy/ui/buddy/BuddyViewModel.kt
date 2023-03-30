package de.bitb.buttonbuddy.ui.buddy

import androidx.lifecycle.LiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import de.bitb.buttonbuddy.data.BuddyRepository
import de.bitb.buttonbuddy.data.UserRepository
import de.bitb.buttonbuddy.data.MessageRepository
import de.bitb.buttonbuddy.data.SettingsRepository
import de.bitb.buttonbuddy.data.model.Buddy
import de.bitb.buttonbuddy.data.model.User
import de.bitb.buttonbuddy.data.model.Message
import de.bitb.buttonbuddy.data.model.Settings
import de.bitb.buttonbuddy.ui.base.BaseViewModel
import de.bitb.buttonbuddy.usecase.MessageUseCases
import de.bitb.buttonbuddy.usecase.message.SendMessageDelegate
import javax.inject.Inject

@HiltViewModel
class BuddyViewModel @Inject constructor(
    override val messageUC: MessageUseCases,
    private val buddyRepo: BuddyRepository,
    private val msgRepo: MessageRepository,
    settingsRepo: SettingsRepository,
    userRepo: UserRepository,
) : BaseViewModel(), SendMessageDelegate {

    val settings: LiveData<Settings> = settingsRepo.getLiveSettings()

    val user: LiveData<User> = userRepo.getLiveUser()
    lateinit var buddy: LiveData<Buddy>
    lateinit var messages: LiveData<List<Message>>

    fun initLiveState(uuid: String) {
        buddy = buddyRepo.getLiveBuddy(uuid)
        messages = msgRepo.getLiveMessages(uuid)
    }

    fun sendMessageToBuddy(buddy: Buddy) = sendMessage(buddy)
}

