package de.bitb.buttonbuddy.usecase.message

import android.util.Log
import de.bitb.buttonbuddy.core.Notifier
import de.bitb.buttonbuddy.data.model.Message
import de.bitb.buttonbuddy.misc.Resource

class ReceivingMessageUC(
    private val notifier: Notifier,
) {
    suspend operator fun invoke(data: Map<String, String>): Resource<Unit> {
        return try {
            Log.d(toString(), "Message data payload: $data")
            val message = Message().fromMap(data)
            //TODO wenn ich die id nicht kenne ignorieren? -> trotzdem speichern bzw tracken
            notifier.showNotification(message)
            //TODO save message to db
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }
}