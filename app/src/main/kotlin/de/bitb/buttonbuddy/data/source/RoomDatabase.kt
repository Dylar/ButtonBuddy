package de.bitb.buttonbuddy.data.source

import androidx.lifecycle.LiveData
import androidx.room.*
import de.bitb.buttonbuddy.data.model.Buddy
import de.bitb.buttonbuddy.data.model.Info
import de.bitb.buttonbuddy.data.model.converter.StringListConverter

@Database(
    entities = [Info::class, Buddy::class],
    version = 1,
    exportSchema = false,
)
@TypeConverters(value = [StringListConverter::class])
abstract class RoomDatabaseImpl : RoomDatabase() {
    abstract val infoDao: InfoDao
    abstract val buddyDao: BuddyDao
}

@Dao
interface BuddyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(buddies: List<Buddy>)

    @Query("SELECT * FROM buddy")
    fun getAll(): LiveData<List<Buddy>>

    @Query("SELECT * FROM buddy WHERE uuid = :uuid LIMIT 1")
    fun getByUuid(uuid: String): LiveData<Buddy>
}

@Dao
interface InfoDao {
    @Query("SELECT * FROM info LIMIT 1")
    fun getLiveInfo(): LiveData<Info>

    @Query("SELECT * FROM info LIMIT 1")
    suspend fun getInfo(): Info?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(info: Info)
}