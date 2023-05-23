package de.bitb.buttonbuddy.data.source

import androidx.lifecycle.LiveData
import androidx.room.*
import de.bitb.buttonbuddy.data.model.Buddy
import de.bitb.buttonbuddy.data.model.Message
import de.bitb.buttonbuddy.data.model.User
import de.bitb.buttonbuddy.data.model.converter.DateConverter
import de.bitb.buttonbuddy.data.model.converter.StringListConverter

@Database(
    entities = [User::class, Buddy::class, Message::class],
    version = 2,
    exportSchema = false,
)
@TypeConverters(value = [StringListConverter::class, DateConverter::class])
abstract class RoomDatabaseImpl : RoomDatabase() {
    abstract val userDao: UserDao
    abstract val buddyDao: BuddyDao
    abstract val messageDao: MessageDao
}

@Dao
interface UserDao {
    @Query("SELECT * FROM user LIMIT 1")
    fun getLiveUser(): LiveData<User>

    @Query("SELECT * FROM user LIMIT 1")
    suspend fun getUser(): User?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: User)
}

@Dao
interface BuddyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllBuddys(buddies: List<Buddy>)

    @Query("SELECT * FROM buddy")
    fun getAll(): LiveData<List<Buddy>>

    @Query("SELECT * FROM buddy WHERE uuid = :uuid LIMIT 1")
    fun getByUuid(uuid: String): LiveData<Buddy>
}

@Dao
interface MessageDao {
    @Query("SELECT * FROM message WHERE fromUuid = :uuid OR toUuid = :uuid ORDER BY date ASC")
    fun getLiveMessages(uuid: String): LiveData<List<Message>>

    @Query("SELECT * FROM message WHERE toUuid = :uuid ORDER BY date DESC LIMIT 1")
    fun getLiveLastMessage(uuid: String): LiveData<Message?>

    @Query("SELECT * FROM message WHERE toUuid = :uuid ORDER BY date DESC LIMIT 1")
    fun getLastMessage(uuid: String): Message?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(msg: Message)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllMessages(msg: List<Message>)
}