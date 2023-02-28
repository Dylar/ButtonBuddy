package de.bitb.buttonbuddy.data

import androidx.lifecycle.LiveData
import de.bitb.buttonbuddy.data.model.Message
import de.bitb.buttonbuddy.data.source.*
import de.bitb.buttonbuddy.core.misc.Resource

interface MessageRepository {
    fun getLiveMessages(uuid: String): LiveData<List<Message>>
    suspend fun saveMessage(msg: Message): Resource<Unit>
}

class MessageRepositoryImpl constructor(
    private val remoteDB: RemoteService,
    private val localDB: LocalDatabase,
) : MessageRepository {

    override fun getLiveMessages(uuid: String): LiveData<List<Message>> =
        localDB.getLiveMessages(uuid)

    override suspend fun saveMessage(msg: Message): Resource<Unit> {
        return try {
            localDB.insert(msg)
//                remoteDB.saveMessage(saveInfo) TODO save online
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }
}
