package de.bitb.buttonbuddy.usecase.buddies

import de.bitb.buttonbuddy.data.model.Buddy
import de.bitb.buttonbuddy.data.model.Message
import de.bitb.buttonbuddy.data.source.RemoteService
import de.bitb.buttonbuddy.misc.Resource
import java.util.*
import javax.inject.Inject

class SendMessageUC @Inject constructor(
    private val remoteService: RemoteService
) {
    suspend operator fun invoke(buddy: Buddy): Resource<Unit> {
        return try {
            val messageTitle = "Hey"
            val messageBody = "Denk an dich"
            val uuid = UUID.randomUUID().toString()
            val message = Message(uuid, messageTitle, messageBody, buddy.uuid, buddy.token)
            remoteService.sendMessage(message)
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }
}