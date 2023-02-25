package de.bitb.buttonbuddy.usecase.message

import de.bitb.buttonbuddy.data.InfoRepository
import de.bitb.buttonbuddy.data.model.Buddy
import de.bitb.buttonbuddy.data.model.Message
import de.bitb.buttonbuddy.data.source.RemoteService
import de.bitb.buttonbuddy.misc.Resource
import java.util.*
import javax.inject.Inject

class SendMessageUC @Inject constructor(
    private val remoteService: RemoteService,
    private val infoRepo: InfoRepository,
) {
    suspend operator fun invoke(buddy: Buddy): Resource<Unit> {
        return try {
            val infoResp = infoRepo.getInfo()
            if (infoResp is Resource.Error) {
                return Resource.Error(infoResp.message!!)
            }
            val info = infoResp.data!!
            val msg = Message(
                uuid = UUID.randomUUID().toString(),
                title = info.fullName,
                message = "Denkt an dich",
                fromUuid = info.uuid,
                toUuid = buddy.uuid,
                token = buddy.token
            )
            remoteService.sendMessage(msg)

            // TODO save msg
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }
}