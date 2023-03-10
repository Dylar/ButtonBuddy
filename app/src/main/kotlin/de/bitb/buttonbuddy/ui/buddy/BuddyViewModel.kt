package de.bitb.buttonbuddy.ui.buddy

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.bitb.buttonbuddy.data.BuddyRepository
import de.bitb.buttonbuddy.data.InfoRepository
import de.bitb.buttonbuddy.data.MessageRepository
import de.bitb.buttonbuddy.data.model.Buddy
import de.bitb.buttonbuddy.data.model.Info
import de.bitb.buttonbuddy.data.model.Message
import de.bitb.buttonbuddy.ui.base.BaseViewModel
import de.bitb.buttonbuddy.usecase.MessageUseCases
import de.bitb.buttonbuddy.usecase.message.SendMessageDelegate
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject

@HiltViewModel
class BuddyViewModel @Inject constructor(
    override val messageUC: MessageUseCases,
    private val buddyRepo: BuddyRepository,
    private val msgRepo: MessageRepository,
    infoRepo: InfoRepository,
) : BaseViewModel(),
    SendMessageDelegate {

    override val scope: CoroutineScope
        get() = viewModelScope

    lateinit var uuid: String
    val info: LiveData<Info> = infoRepo.getLiveInfo()
    lateinit var buddy: LiveData<Buddy>
    lateinit var messages: LiveData<List<Message>>

    fun initLiveState(uuid: String) {
        this.uuid = uuid
        buddy = buddyRepo.getLiveBuddy(uuid)
        messages = msgRepo.getLiveMessages(uuid)
    }

}

