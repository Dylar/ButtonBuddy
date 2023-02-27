package de.bitb.buttonbuddy.core

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import de.bitb.buttonbuddy.usecase.info.InfoUseCases
import de.bitb.buttonbuddy.usecase.message.MessageUseCases
import kotlinx.coroutines.*
import javax.inject.Inject

@OptIn(DelicateCoroutinesApi::class)
@AndroidEntryPoint
class FCMService : FirebaseMessagingService() {

    private var newTokenJob: Job? = null
    private var receivingMessageJob: Job? = null

    @Inject
    lateinit var infoUseCases: InfoUseCases

    @Inject
    lateinit var messageUseCases: MessageUseCases

    override fun onDestroy() {
        newTokenJob?.cancel()
        receivingMessageJob?.cancel()
        super.onDestroy()
    }

    override fun onNewToken(token: String) {
        Log.d(toString(), "Refreshed token: $token")
        newTokenJob?.cancel()
        newTokenJob = GlobalScope.launch {
            infoUseCases.updateTokenUC(token)
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(toString(), "From: ${remoteMessage.from}")
        receivingMessageJob?.cancel()
        receivingMessageJob = GlobalScope.launch {
            messageUseCases.receivingMessageUC(remoteMessage.data)
        }
    }
}
