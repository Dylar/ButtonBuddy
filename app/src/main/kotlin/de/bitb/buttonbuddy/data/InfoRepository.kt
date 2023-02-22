package de.bitb.buttonbuddy.data

import androidx.lifecycle.LiveData
import de.bitb.buttonbuddy.data.model.Info
import de.bitb.buttonbuddy.data.source.*
import de.bitb.buttonbuddy.misc.Resource
import javax.inject.Inject

interface InfoRepository {
    suspend fun getInfo(): Resource<Info?>
    fun getLiveInfo(): LiveData<Info>
    suspend fun updateToken(token: String): Resource<Unit>
    suspend fun loadInfo(firstName: String, lastName: String): Resource<Info?>
    suspend fun saveInfo(info: Info): Resource<Info>
}

class InfoRepositoryImpl @Inject constructor(
    private val remoteDB: RemoteDatabase,
    private val localDB: LocalDatabase,
) : InfoRepository {

    override suspend fun getInfo(): Resource<Info?> {
        return try {
            Resource.Success(localDB.getInfo())
        } catch (e: Exception) {
            Resource.Error(e.toString())
        }
    }

    override fun getLiveInfo(): LiveData<Info> = localDB.getLiveInfo()

    override suspend fun updateToken(token: String): Resource<Unit> {
        return try {
            localDB.saveToken(token)
            val info = localDB.getInfo()
            if (info != null) {
                saveInfo(info)
            }
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.toString())
        }
    }

    override suspend fun loadInfo(firstName: String, lastName: String): Resource<Info?> {
        return try {
            when (val infoRes = remoteDB.getInfo(firstName, lastName)) {
                is Resource.Success -> {
                    val data = infoRes.data
                    if (infoRes.hasData) {
                        saveInfo(data!!)
                    }
                    Resource.Success(data)
                }
                is Resource.Error -> {
                    Resource.Error(infoRes.message!!, infoRes.data)
                }
            }
        } catch (e: Exception) {
            Resource.Error(e.toString())
        }
    }

    override suspend fun saveInfo(info: Info): Resource<Info> {
        return try {
            val token = localDB.getToken()
            val saveInfo = info.copy(token = token)
            localDB.insert(saveInfo)
            if (info.uuid.isNotBlank()) {
                remoteDB.saveInfo(saveInfo)
            }
            Resource.Success(saveInfo)
        } catch (e: Exception) {
            Resource.Error(e.toString())
        }
    }
}
