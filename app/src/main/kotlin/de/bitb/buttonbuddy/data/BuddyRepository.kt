package de.bitb.buttonbuddy.data

import androidx.lifecycle.LiveData
import de.bitb.buttonbuddy.core.misc.Resource
import de.bitb.buttonbuddy.core.misc.tryIt
import de.bitb.buttonbuddy.data.model.Buddy
import de.bitb.buttonbuddy.data.source.LocalDatabase
import de.bitb.buttonbuddy.data.source.RemoteService

interface BuddyRepository {
    fun getLiveBuddies(): LiveData<List<Buddy>>
    fun getLiveBuddy(uuid: String): LiveData<Buddy>
    suspend fun loadBuddies(userUuid: String, buddyIds: List<String>): Resource<List<Buddy>>
    suspend fun saveCooldown(
        userUuid: String,
        buddy: Buddy,
    ): Resource<Unit>
}

class BuddyRepositoryImpl(
    private val remoteDB: RemoteService,
    private val localDB: LocalDatabase,
) : BuddyRepository {

    override fun getLiveBuddies(): LiveData<List<Buddy>> = localDB.getAll()
    override fun getLiveBuddy(uuid: String): LiveData<Buddy> = localDB.getByUuid(uuid)

    override suspend fun loadBuddies(
        userUuid: String,
        buddyIds: List<String>
    ): Resource<List<Buddy>> {
        return tryIt {
            val loadBuddiesResp = remoteDB.loadBuddies(userUuid, buddyIds)
            if (loadBuddiesResp is Resource.Success) {
                localDB.insertAll(loadBuddiesResp.data!!)
            }
            loadBuddiesResp
        }
    }

    override suspend fun saveCooldown(
        userUuid: String,
        buddy: Buddy,
    ): Resource<Unit> {
        return tryIt {
            val updateCDResp = remoteDB.updateCooldown(userUuid, buddy.uuid, buddy.cooldown)
            if (updateCDResp is Resource.Success) {
                localDB.insertAll(listOf(buddy))
            }
            updateCDResp
        }
    }

}
