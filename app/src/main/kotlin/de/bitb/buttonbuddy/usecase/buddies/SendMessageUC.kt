package de.bitb.buttonbuddy.usecase.buddies

import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.RemoteMessage
import de.bitb.buttonbuddy.core.FCMService
import de.bitb.buttonbuddy.data.BuddyRepository
import de.bitb.buttonbuddy.data.InfoRepository
import de.bitb.buttonbuddy.data.model.Buddy
import de.bitb.buttonbuddy.exceptions.NoInfoException
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

class SendMessageUC {
    private val firestore: FirebaseMessaging by lazy { FirebaseMessaging.getInstance() }

    operator fun invoke(buddy: Buddy) {
        val messageTitle = "Hey"
        val messageBody = "Denk an dich"
        val message = RemoteMessage.Builder(buddy.token)
            .setMessageId(UUID.randomUUID().toString())
            .setData(mapOf("title" to messageTitle, "body" to messageBody))
            .build()

        firestore.send(message)
    }
}