package de.bitb.buttonbuddy.data.source

import de.bitb.buttonbuddy.data.model.Buddy
import de.bitb.buttonbuddy.data.model.Info
import de.bitb.buttonbuddy.misc.Resource

interface RemoteDatabase {
    suspend fun loadBuddies(buddyIds: List<String>): Resource<List<Buddy>>
    suspend fun saveInfo(info: Info): Resource<Unit>
    suspend fun getInfo(firstName: String, lastName: String): Resource<Info?>
}

interface LocalDatabase : BuddyDao, InfoDao {
    fun saveToken(token: String)
    fun getToken(): String
}