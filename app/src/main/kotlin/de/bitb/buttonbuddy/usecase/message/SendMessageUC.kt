package de.bitb.buttonbuddy.usecase.message

import de.bitb.buttonbuddy.R
import de.bitb.buttonbuddy.data.UserRepository
import de.bitb.buttonbuddy.data.model.Buddy
import de.bitb.buttonbuddy.data.model.Message
import de.bitb.buttonbuddy.data.source.LocalDatabase
import de.bitb.buttonbuddy.data.source.RemoteService
import de.bitb.buttonbuddy.core.misc.Resource
import de.bitb.buttonbuddy.core.misc.asResourceError
import de.bitb.buttonbuddy.core.misc.tryIt
import de.bitb.buttonbuddy.core.misc.timeExceeded
import de.bitb.buttonbuddy.data.MessageRepository
import de.bitb.buttonbuddy.data.SettingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

class SendMessageUC @Inject constructor(
    private val remoteService: RemoteService,
    private val localDB: LocalDatabase,
    private val settingsRepo: SettingsRepository,
    private val userRepo: UserRepository,
    private val msgRepo: MessageRepository,
) {
    suspend operator fun invoke(buddy: Buddy): Resource<Unit> {
        return tryIt {
            val lastMsgResp =
                withContext(Dispatchers.IO) { msgRepo.getLastMessage(buddy.uuid) }
            if (lastMsgResp is Resource.Error) {
                lastMsgResp.castTo<Unit>()
            }
            val settingsResp =
                withContext(Dispatchers.IO) { settingsRepo.getSettings() }
            if (settingsResp is Resource.Error) {
                settingsResp.castTo<Unit>()
            }

            val lastMsg = lastMsgResp.data
            val onCooldown = lastMsg != null && !timeExceeded(
                lastMsg.date,
                Date(),
                settingsResp.data!!.buddysCooldown[buddy.uuid] ?: Date().time
            )
            if (onCooldown) {
                R.string.send_on_cooldown.asResourceError<Unit>()
            }

            val userResp = userRepo.getLocalUser()
            if (userResp is Resource.Error) {
                userResp.castTo<Unit>()
            }
            val user = userResp.data!!
            val msg = Message(
                uuid = UUID.randomUUID().toString(),
                title = user.fullName,
                message = "Denkt an dich",
                fromUuid = user.uuid,
                toUuid = buddy.uuid,
                token = buddy.token
            )
            localDB.insert(msg)
            remoteService.sendMessage(msg)
        }
    }
}