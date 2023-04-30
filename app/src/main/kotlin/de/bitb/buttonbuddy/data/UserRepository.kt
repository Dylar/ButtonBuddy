package de.bitb.buttonbuddy.data

import androidx.lifecycle.LiveData
import de.bitb.buttonbuddy.core.misc.Resource
import de.bitb.buttonbuddy.core.misc.tryIt
import de.bitb.buttonbuddy.data.model.User
import de.bitb.buttonbuddy.data.source.LocalDatabase
import de.bitb.buttonbuddy.data.source.RemoteService

interface UserRepository {
    suspend fun isUserLoggedIn(): Resource<Boolean>
    fun getLiveUser(): LiveData<User>
    suspend fun getLocalUser(): Resource<User?>
    suspend fun registerUser(email: String, pw: String): Resource<Unit>
    suspend fun loginUser(email: String, pw: String): Resource<User?>
    suspend fun loadUser(email: String): Resource<User?>
    suspend fun saveUser(user: User): Resource<User>
    suspend fun updateToken(token: String): Resource<Unit>
}

class UserRepositoryImpl constructor(
    private val remoteDB: RemoteService,
    private val localDB: LocalDatabase,
) : UserRepository {

    override suspend fun isUserLoggedIn(): Resource<Boolean> = remoteDB.isUserLoggedIn()

    override fun getLiveUser(): LiveData<User> = localDB.getLiveUser()

    override suspend fun getLocalUser(): Resource<User?> {
        return tryIt { Resource.Success(localDB.getUser()) }
    }

    override suspend fun registerUser(email: String, pw: String): Resource<Unit> {
        return tryIt { remoteDB.registerUser(email, pw) }
    }

    override suspend fun loginUser(email: String, pw: String): Resource<User?> {
        return tryIt {
            val loginResp = remoteDB.loginUser(email, pw)
            if (loginResp is Resource.Error) {
                return@tryIt loginResp.castTo<User?>()
            }
            loadUser(email)
        }
    }

    override suspend fun loadUser(email: String): Resource<User?> {
        return tryIt {
            when (val userRes = remoteDB.getUser(email)) {
                is Resource.Success -> {
                    val data = userRes.data
                    if (userRes.hasData) {
                        val saveUserResp = saveUser(data!!)
                        if (saveUserResp is Resource.Error) {
                            return@tryIt saveUserResp.castTo()
                        }
                        return@tryIt Resource.Success(saveUserResp.data)
                    }
                    Resource.Success()
                }
                is Resource.Error -> {
                    Resource.Error(userRes.message!!, userRes.data)
                }
            }
        }
    }

    override suspend fun saveUser(user: User): Resource<User> {
        return tryIt {
            val token = localDB.getToken()
            val saveUser = user.copy(token = token)
            localDB.insert(saveUser)
            if (user.uuid.isNotBlank()) {
                val saveUserResp = remoteDB.saveUser(saveUser)
                if (saveUserResp is Resource.Error) {
                    return@tryIt saveUserResp.castTo()
                }
            }
            Resource.Success(saveUser)
        }
    }

    override suspend fun updateToken(token: String): Resource<Unit> {
        return tryIt {
            localDB.setToken(token)
            val user = localDB.getUser()
            if (user != null) {
                val saveUserResp = remoteDB.saveUser(user)
                if (saveUserResp is Resource.Error) {
                    return@tryIt saveUserResp.castTo()
                }
            }
            Resource.Success()
        }
    }
}
