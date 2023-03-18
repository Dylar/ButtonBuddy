package de.bitb.buttonbuddy.data.source

import de.bitb.buttonbuddy.data.model.Buddy
import de.bitb.buttonbuddy.data.model.Info
import de.bitb.buttonbuddy.data.model.Message
import de.bitb.buttonbuddy.core.misc.Resource

// LOCAL
interface LocalDatabase : InfoDao, BuddyDao, MessageDao, PreferenceDatabase

class BuddyLocalDatabase(db: RoomDatabaseImpl, pref: PreferenceDatabaseImpl) :
    LocalDatabase,
    InfoDao by db.infoDao,
    BuddyDao by db.buddyDao,
    MessageDao by db.messageDao,
    PreferenceDatabase by pref

// REMOTE
interface RemoteService : BuddyRemoteDao, InfoRemoteDao, MessageRemoteDao, MessageService

class BuddyRemoteService(fireService: FirestoreService, retrofitService: RetrofitService) :
    RemoteService,
    BuddyRemoteDao by fireService,
    InfoRemoteDao by fireService,
    MessageRemoteDao by fireService,
    MessageService by retrofitService

interface BuddyRemoteDao {
    suspend fun loadBuddies(buddyIds: List<String>): Resource<List<Buddy>>
}

interface InfoRemoteDao {
    suspend fun saveInfo(info: Info): Resource<Unit>
    suspend fun getInfo(firstName: String, lastName: String): Resource<Info?>
}

interface MessageRemoteDao {
    suspend fun saveMessage(msg: Message): Resource<Unit>
//    suspend fun getMessage(uuid: String): Resource<Unit>
}

interface MessageService {
    suspend fun sendMessage(msg: Message): Resource<Unit>
}