package de.bitb.buttonbuddy.usecase.message

import de.bitb.buttonbuddy.R
import de.bitb.buttonbuddy.core.misc.Resource
import de.bitb.buttonbuddy.data.model.Buddy
import de.bitb.buttonbuddy.ui.base.composable.ResString
import de.bitb.buttonbuddy.usecase.MessageUseCases
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

interface SendMessageDelegate {
    val scope: CoroutineScope
    val messageUC: MessageUseCases
    var showSnackBar: (ResString) -> Unit

    fun sendMessage(buddy: Buddy) {
        scope.launch {
            when (val resp = messageUC.sendMessageUC(buddy)) {
                is Resource.Error -> showSnackBar(resp.message!!)
                is Resource.Success -> showSnackBar(
                    ResString.ResourceString(
                        R.string.message_sent_toast,
                        arrayOf(buddy.fullName),
                    )
                )
            }
        }
    }
}