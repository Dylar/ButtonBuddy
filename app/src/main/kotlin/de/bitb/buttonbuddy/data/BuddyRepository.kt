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

    suspend fun clearBuddys(): Resource<Unit>
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
            val resp = remoteDB.loadBuddies(userUuid, buddyIds)
            if (resp is Resource.Success) {
                localDB.insertAllBuddys(resp.data!!)
            }
            resp
        }
    }

    override suspend fun saveCooldown(
        userUuid: String,
        buddy: Buddy,
    ): Resource<Unit> {
        return tryIt {
            val resp = remoteDB.updateCooldown(userUuid, buddy.uuid, buddy.cooldown)
            if (resp is Resource.Success) {
                localDB.insertAllBuddys(listOf(buddy))
            }
            resp
        }
    }

    override suspend fun clearBuddys(): Resource<Unit> {
        return tryIt {
            localDB.clearBuddys()
            Resource.Success()
        }
    }

}
