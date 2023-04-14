package de.bitb.buttonbuddy.data

import androidx.lifecycle.LiveData
import de.bitb.buttonbuddy.data.model.User
import de.bitb.buttonbuddy.data.source.*
import de.bitb.buttonbuddy.core.misc.Resource

interface UserRepository {
    suspend fun getUser(): Resource<User?>
    fun getLiveUser(): LiveData<User>
    suspend fun updateToken(token: String): Resource<Unit>
    suspend fun registerUser(email: String, pw: String): Resource<Unit>
    suspend fun loginUser(email: String, pw: String): Resource<User?>
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

    override suspend fun registerUser(email: String, pw: String): Resource<Unit> {
        return remoteDB.registerUser(email, pw)
    }

    override suspend fun loginUser(email: String, pw: String): Resource<User?> {
        return try {
            val loginResp = remoteDB.loginUser(email, pw)
            if (loginResp is Resource.Error) {
                return Resource.Error(loginResp.message!!)
            }
            when (val userRes = remoteDB.getUser(email)) {
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
