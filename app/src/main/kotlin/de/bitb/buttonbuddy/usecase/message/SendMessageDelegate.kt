package de.bitb.buttonbuddy.usecase.message

import androidx.lifecycle.viewModelScope
import de.bitb.buttonbuddy.R
import de.bitb.buttonbuddy.core.misc.Resource
import de.bitb.buttonbuddy.data.model.Buddy
import de.bitb.buttonbuddy.ui.base.BaseViewModel
import de.bitb.buttonbuddy.ui.base.composable.ResString
import de.bitb.buttonbuddy.usecase.MessageUseCases
import kotlinx.coroutines.launch

interface SendMessageDelegate {
    val messageUC: MessageUseCases

    fun <T : BaseViewModel> T.sendMessage(buddy: Buddy) {
        viewModelScope.launch {
            when (val resp = messageUC.sendMessageUC(buddy)) {
                is Resource.Error -> showSnackbar(resp.message!!)
                is Resource.Success -> {
                    showSnackbar(
                        ResString.ResourceString(
                            R.string.message_sent_toast,
                            arrayOf(buddy.fullName),
                        )
                    )
                }
            }
        }
    }
}