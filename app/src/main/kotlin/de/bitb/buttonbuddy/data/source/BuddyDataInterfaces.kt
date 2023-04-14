package de.bitb.buttonbuddy.data.source

import de.bitb.buttonbuddy.data.model.Buddy
import de.bitb.buttonbuddy.data.model.User
import de.bitb.buttonbuddy.data.model.Message
import de.bitb.buttonbuddy.core.misc.Resource

// LOCAL
interface LocalDatabase : UserDao, BuddyDao, MessageDao, TokenDao, SettingsDao

class BuddyLocalDatabase(db: RoomDatabaseImpl, pref: PreferenceDatabase) :
    LocalDatabase,
    UserDao by db.userDao,
    BuddyDao by db.buddyDao,
    MessageDao by db.messageDao,
    TokenDao by pref,
    SettingsDao by pref

interface TokenDao {
    fun setToken(token: String)
    fun getToken(): String
}

interface SettingsDao {
    fun getCoolDown(): Long
    fun setCoolDown(cd: Long)
    fun getDarkMode(): Boolean
    fun setDarkMode(isDark: Boolean)
}

// REMOTE
interface RemoteService : BuddyRemoteDao, UserRemoteDao, MessageRemoteDao, MessageService

class BuddyRemoteService(fireService: FirestoreService, retrofitService: RetrofitService) :
    RemoteService,
    BuddyRemoteDao by fireService,
    UserRemoteDao by fireService,
    MessageRemoteDao by fireService,
    MessageService by retrofitService

interface BuddyRemoteDao {
    suspend fun loadBuddies(buddyIds: List<String>): Resource<List<Buddy>>
}

interface UserRemoteDao {
    suspend fun registerUser(email: String, pw: String): Resource<Unit>
    suspend fun loginUser(email: String, pw: String): Resource<Boolean>
    suspend fun getUser(email: String): Resource<User?>
    suspend fun saveUser(user: User): Resource<Unit>
}

interface MessageRemoteDao {
    suspend fun saveMessage(msg: Message): Resource<Unit>
//    suspend fun getMessage(uuid: String): Resource<Unit>
}

interface MessageService {
    suspend fun sendMessage(msg: Message): Resource<Unit>
}