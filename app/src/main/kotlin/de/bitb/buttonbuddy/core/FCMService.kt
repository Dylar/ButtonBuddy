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

    override fun onDestroy() {
        job?.cancel()
        super.onDestroy()
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onNewToken(token: String) {
        Log.d(toString(), "Refreshed token: $token")
        job?.cancel()
        job = GlobalScope.launch {
            infoUseCases.updateTokenUC(token)
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(toString(), "From: ${remoteMessage.from}")
        Log.d(toString(), "Show payload")
        val data = remoteMessage.data
        if (data.isNotEmpty() && data.containsKey("title") && data.containsKey("body")) {
            Log.d(toString(), "Message data payload: $data")
            notifyManager.showNotification(data["title"]!!, data["body"]!!)
        }

        //TODO save message to db

    }
}
