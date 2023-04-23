package de.bitb.buttonbuddy.data

import androidx.lifecycle.LiveData
import de.bitb.buttonbuddy.core.misc.Resource
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
        cooldown: Long,
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
        return try {
            val response = remoteDB.loadBuddies(userUuid, buddyIds)
            if (response is Resource.Success) {
                localDB.insertAll(response.data!!)
            }
            response
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    override suspend fun saveCooldown(
        userUuid: String,
        buddy: Buddy,
        cooldown: Long,
    ): Resource<Unit> {
        return try {
            val response = remoteDB.updateCooldown(userUuid, buddy.uuid, cooldown)
            if (response is Resource.Success) {
                localDB.insertAll(listOf(buddy))
            }
            response
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

}
