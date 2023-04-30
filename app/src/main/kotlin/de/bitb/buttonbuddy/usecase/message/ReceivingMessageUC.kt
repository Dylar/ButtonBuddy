package de.bitb.buttonbuddy.usecase.message

import de.bitb.buttonbuddy.core.Notifier
import de.bitb.buttonbuddy.core.misc.Resource
import de.bitb.buttonbuddy.core.misc.tryIt
import de.bitb.buttonbuddy.data.MessageRepository
import de.bitb.buttonbuddy.data.model.Message

class ReceivingMessageUC(
    private val msgRepo: MessageRepository,
    private val notifier: Notifier,
) {
    suspend operator fun invoke(data: Map<String, String>): Resource<Unit> {
        return tryIt {
            val msg = Message().fromMap(data)
            //TODO wenn ich die id nicht kenne ignorieren? -> trotzdem speichern bzw tracken

            val saveMsgResp = msgRepo.saveMessage(msg)
            if(saveMsgResp is Resource.Error){
                return@tryIt saveMsgResp
            }
            notifier.showNotification(msg)
            return@tryIt Resource.Success()
        }
    }
}