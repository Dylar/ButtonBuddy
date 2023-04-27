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
    fun getCoolDowns(): Map<String, Long>
    fun setCoolDowns(cooldowns: Map<String, Long>)
    fun getDarkMode(): Boolean
    fun setDarkMode(isDark: Boolean)
}

// REMOTE
interface RemoteService : SettingsRemoteDao, BuddyRemoteDao, UserRemoteDao, MessageRemoteDao,
    MessageService

class BuddyRemoteService(fireService: FirestoreService, retrofitService: RetrofitService) :
    RemoteService,
    SettingsRemoteDao by fireService,
    UserRemoteDao by fireService,
    BuddyRemoteDao by fireService,
    MessageRemoteDao by fireService,
    MessageService by retrofitService

interface BuddyRemoteDao {
    suspend fun loadBuddies(userUuid: String, buddyIds: List<String>): Resource<List<Buddy>>
    suspend fun updateCooldown(
        userUuid: String,
        buddyUuid: String,
        cooldown: Long,
    ): Resource<Unit>
}

interface UserRemoteDao {
    suspend fun isUserLoggedIn(): Resource<Boolean>
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

interface SettingsRemoteDao {
    suspend fun loadCooldowns(userUuid: String): Resource<Map<String, Long>>
}