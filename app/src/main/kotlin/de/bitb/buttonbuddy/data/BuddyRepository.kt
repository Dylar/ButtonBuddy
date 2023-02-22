package de.bitb.buttonbuddy.data

import androidx.lifecycle.LiveData
import de.bitb.buttonbuddy.data.model.Buddy
import de.bitb.buttonbuddy.data.source.LocalDatabase
import de.bitb.buttonbuddy.data.source.RemoteDatabase
import de.bitb.buttonbuddy.misc.Resource

interface BuddyRepository {
    suspend fun loadBuddies(buddyIds: List<String>): Resource<List<Buddy>>
    fun getBuddies(): LiveData<List<Buddy>>
    fun getBuddy(uuid: String): LiveData<Buddy>
}

class BuddyRepositoryImpl constructor(
    private val remoteDB: RemoteDatabase,
    private val localDB: LocalDatabase
) : BuddyRepository {

    override suspend fun loadBuddies(buddyIds: List<String>): Resource<List<Buddy>> {
        val response = remoteDB.loadBuddies(buddyIds)
        if(response is Resource.Success){
            localDB.insertAll(response.data!!)
        }
        return response
    }

    override fun getBuddies(): LiveData<List<Buddy>> = localDB.getAll()
    override fun getBuddy(uuid: String): LiveData<Buddy> = localDB.getByUuid(uuid)

}
