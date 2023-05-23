package de.bitb.buttonbuddy.core

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import de.bitb.buttonbuddy.usecase.MessageUseCases
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class FCMService : FirebaseMessagingService() {

    @Inject
    lateinit var messageUseCases: MessageUseCases

    override fun onNewToken(token: String) {
        Log.d(toString(), "Refreshed token: $token")
        CoroutineScope(Dispatchers.IO).launch {
            messageUseCases.updateTokenUC(token)
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(toString(), "From: ${remoteMessage.from}")
        CoroutineScope(Dispatchers.IO).launch {
            messageUseCases.receivingMessageUC(remoteMessage.data)
        }
    }
}
