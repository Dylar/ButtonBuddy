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
import de.bitb.buttonbuddy.misc.Resource
import de.bitb.buttonbuddy.ui.base.BaseViewModel
import de.bitb.buttonbuddy.usecase.message.MessageUseCases
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BuddyViewModel @Inject constructor(
    private val messageUC: MessageUseCases,
    private val buddyRepo: BuddyRepository,
    private val messageRepo: MessageRepository,
    infoRepo: InfoRepository,
) : BaseViewModel() {

    lateinit var uuid:String
    val info: LiveData<Info> = infoRepo.getLiveInfo()
    lateinit var buddy: LiveData<Buddy>
    lateinit var messages: LiveData<List<Message>>

    fun initLiveState(uuid: String) {
        this.uuid = uuid
        buddy = buddyRepo.getLiveBuddy(uuid)
        messages = messageRepo.getLiveMessages(uuid)
    }

    fun sendMessage(buddy: Buddy) {
        viewModelScope.launch {
            when (val result = messageUC.sendMessageUC(buddy)) {
                is Resource.Error -> showSnackbar(result.message!!.rawString())
                is Resource.Success -> showSnackbar("Nachricht gesendet")
            }
        }
    }

}

