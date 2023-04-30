package de.bitb.buttonbuddy.data

import androidx.lifecycle.LiveData
import de.bitb.buttonbuddy.core.misc.Resource
import de.bitb.buttonbuddy.core.misc.tryIt
import de.bitb.buttonbuddy.data.model.Message
import de.bitb.buttonbuddy.data.source.LocalDatabase
import de.bitb.buttonbuddy.data.source.RemoteService

interface MessageRepository {
    fun getLiveMessages(uuid: String): LiveData<List<Message>>
    fun getLiveLastMessage(uuid: String): LiveData<Message?>
    suspend fun getLastMessage(uuid: String): Resource<Message>
    suspend fun saveMessage(msg: Message): Resource<Unit>
}

class MessageRepositoryImpl constructor(
    private val remoteDB: RemoteService,
    private val localDB: LocalDatabase,
) : MessageRepository {

    override fun getLiveMessages(uuid: String): LiveData<List<Message>> =
        localDB.getLiveMessages(uuid)

    override fun getLiveLastMessage(uuid: String): LiveData<Message?> =
        localDB.getLiveLastMessage(uuid)

    override suspend fun getLastMessage(uuid: String): Resource<Message> {
        return tryIt { Resource.Success(localDB.getLastMessage(uuid)) }
    }

    override suspend fun saveMessage(msg: Message): Resource<Unit> {
        return tryIt {
            localDB.insert(msg)
//                remoteDB.saveMessage(saveInfo) TODO save online + test
            Resource.Success()
        }
    }
}
