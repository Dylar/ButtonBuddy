package de.bitb.buttonbuddy.core

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import de.bitb.buttonbuddy.usecase.info.InfoUseCases
import kotlinx.coroutines.*
import javax.inject.Inject

@AndroidEntryPoint
class FCMService : FirebaseMessagingService() {

    private var job: Job? = null

    @Inject
    lateinit var notifyManager: NotifyManager

    @Inject
    lateinit var infoUseCases: InfoUseCases

    @OptIn(DelicateCoroutinesApi::class)
    override fun onNewToken(token: String) {
        Log.d(toString(), "Refreshed token: $token")
        job?.cancel()
        job = GlobalScope.launch{
            infoUseCases.updateTokenUC(token)
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(toString(), "From: ${remoteMessage.from}")
        val title = remoteMessage.notification?.title ?: ""
        val message = remoteMessage.notification?.body ?: ""
        if (title.isNotBlank() && message.isNotBlank()) {
            notifyManager.showNotification(title, message)
        }

        //TODO save message to db

        if (remoteMessage.data.isNotEmpty()) {
            Log.d(toString(), "Message data payload: " + remoteMessage.data)
        }
    }

    override fun onDestroy() {
        job?.cancel()
        super.onDestroy()
    }
}
