package de.bitb.buttonbuddy.usecase.message

import de.bitb.buttonbuddy.R
import de.bitb.buttonbuddy.data.InfoRepository
import de.bitb.buttonbuddy.data.model.Buddy
import de.bitb.buttonbuddy.data.model.Message
import de.bitb.buttonbuddy.data.source.LocalDatabase
import de.bitb.buttonbuddy.data.source.RemoteService
import de.bitb.buttonbuddy.core.misc.Resource
import de.bitb.buttonbuddy.core.misc.timeExceeded
import de.bitb.buttonbuddy.data.MessageRepository
import de.bitb.buttonbuddy.ui.base.composable.ResString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

const val COOLDOWN: Long = 1000 * 60 * 60 * 2// TODO get cooldown from settings

class SendMessageUC @Inject constructor(
    private val remoteService: RemoteService,
    private val localDB: LocalDatabase,
    private val infoRepo: InfoRepository,
    private val msgRepo: MessageRepository,
//    private val settingsRepo:MessageRepository // TODO get cooldown
) {
    suspend operator fun invoke(buddy: Buddy): Resource<Unit> {
        return try {
            val lastMessageSentTime =
                withContext(Dispatchers.IO) { msgRepo.getLastMessage(buddy.uuid) }
            if (lastMessageSentTime != null && !timeExceeded(lastMessageSentTime.date, Date())) {
                return Resource.Error(ResString.ResourceString(R.string.send_on_cooldown))
            }

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
            localDB.insert(msg)
            remoteService.sendMessage(msg)
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }
}