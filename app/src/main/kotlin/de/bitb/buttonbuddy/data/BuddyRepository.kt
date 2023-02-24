package de.bitb.buttonbuddy.data

import android.util.Log
import androidx.lifecycle.LiveData
import de.bitb.buttonbuddy.data.model.Buddy
import de.bitb.buttonbuddy.data.source.LocalDatabase
import de.bitb.buttonbuddy.data.source.RemoteService
import de.bitb.buttonbuddy.misc.Resource

interface BuddyRepository {
    suspend fun loadBuddies(buddyIds: List<String>): Resource<List<Buddy>>
    fun getLiveBuddies(): LiveData<List<Buddy>>
    fun getLiveBuddy(uuid: String): LiveData<Buddy>
}

class BuddyRepositoryImpl(
    private val remoteDB: RemoteService,
    private val localDB: LocalDatabase
) : BuddyRepository {

    override suspend fun loadBuddies(buddyIds: List<String>): Resource<List<Buddy>> {
        val response = remoteDB.loadBuddies(buddyIds)
        Log.e("TAG", "response: ${response.data?.size}")
        if (response is Resource.Success) {
            localDB.insertAll(response.data!!)
        }
        return response
    }

    override fun getLiveBuddies(): LiveData<List<Buddy>> = localDB.getAll()
    override fun getLiveBuddy(uuid: String): LiveData<Buddy> = localDB.getByUuid(uuid)

}
