package de.bitb.buttonbuddy.data

import androidx.lifecycle.LiveData
import de.bitb.buttonbuddy.data.model.Buddy
import de.bitb.buttonbuddy.data.source.BuddyDao
import de.bitb.buttonbuddy.data.source.RemoteDatabase
import java.util.*

interface BuddyRepository {
    suspend fun loadBuddies(buddyIds: List<String>)
    fun getBuddies(): LiveData<List<Buddy>>
    fun getBuddy(uuid: String): LiveData<Buddy>
}

class BuddyRepositoryImpl constructor(
    private val remoteDB: RemoteDatabase,
    private val buddyDao: BuddyDao
) : BuddyRepository {

    override suspend fun loadBuddies(buddyIds: List<String>) {
//        val buddies = remoteDB.loadBuddies(buddyIds)
        val buddies = listOf( //TODO make real
            Buddy(UUID.randomUUID().toString(),"XXX", "Hilde", "Bruns" ),
            Buddy(UUID.randomUUID().toString(),"YYY", "Brunella", "Hilds" ),
        )
        buddyDao.insertAll(buddies)
    }

    override fun getBuddies(): LiveData<List<Buddy>> = buddyDao.getAll()
    override fun getBuddy(uuid: String): LiveData<Buddy> = buddyDao.getByUuid(uuid)

}
