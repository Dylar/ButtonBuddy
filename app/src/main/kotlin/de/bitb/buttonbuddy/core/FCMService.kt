package de.bitb.buttonbuddy.core

import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import de.bitb.buttonbuddy.usecase.info.InfoUseCases
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class FCMService : FirebaseMessagingService() {

    @Inject
    lateinit var notifyManager: NotifyManager

    @Inject
    lateinit var infoUseCases: InfoUseCases

    override fun onNewToken(token: String) {
        Log.d(toString(), "Refreshed token: $token")
        infoUseCases.updateToken(token)
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

}
