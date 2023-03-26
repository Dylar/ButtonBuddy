package de.bitb.buttonbuddy.data

import androidx.lifecycle.LiveData
import de.bitb.buttonbuddy.data.model.User
import de.bitb.buttonbuddy.data.source.*
import de.bitb.buttonbuddy.core.misc.Resource

interface UserRepository {
    suspend fun getUser(): Resource<User?>
    fun getLiveUser(): LiveData<User>
    suspend fun updateToken(token: String): Resource<Unit>
    suspend fun loadUser(firstName: String, lastName: String): Resource<User?>
    suspend fun saveUser(user: User): Resource<User>
}

class UserRepositoryImpl constructor(
    private val remoteDB: RemoteService,
    private val localDB: LocalDatabase,
) : UserRepository {

    override suspend fun getUser(): Resource<User?> {
        return try {
            Resource.Success(localDB.getUser())
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    override fun getLiveUser(): LiveData<User> = localDB.getLiveUser()

    override suspend fun updateToken(token: String): Resource<Unit> {
        return try {
            localDB.setToken(token)
            val user = localDB.getUser()
            if (user != null) {
                saveUser(user)
            }
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    override suspend fun loadUser(firstName: String, lastName: String): Resource<User?> {
        return try {
            when (val userRes = remoteDB.getUser(firstName, lastName)) {
                is Resource.Success -> {
                    val data = userRes.data
                    if (userRes.hasData) {
                        saveUser(data!!)
                    }
                    Resource.Success(data)
                }
                is Resource.Error -> {
                    Resource.Error(userRes.message!!, userRes.data)
                }
            }
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    override suspend fun saveUser(user: User): Resource<User> {
        return try {
            val token = localDB.getToken()
            val saveUser = user.copy(token = token)
            localDB.insert(saveUser)
            if (user.uuid.isNotBlank()) {
                remoteDB.saveUser(saveUser)
            }
            Resource.Success(saveUser)
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }
}
