package de.bitb.buttonbuddy.data

import androidx.lifecycle.LiveData
import de.bitb.buttonbuddy.data.model.Info
import de.bitb.buttonbuddy.data.source.InfoDao
import de.bitb.buttonbuddy.data.source.RemoteDatabase
import kotlinx.coroutines.*
import java.util.*
import javax.inject.Inject

interface InfoRepository {
    suspend fun getInfo(): Info?
    fun getLiveInfo(): LiveData<Info>
    fun saveInfo(info: Info)
    suspend fun updateToken(token: String)
}

class InfoRepositoryImpl @Inject constructor(
    private val remoteDatabase: RemoteDatabase,
    private val infoDao: InfoDao
) : InfoRepository {

    override suspend fun getInfo(): Info? = infoDao.getInfo()
    override fun getLiveInfo(): LiveData<Info> = infoDao.getLiveInfo()

    override suspend fun updateToken(token: String) {
        val info = infoDao.getInfo() ?: Info(
            uuid = UUID.randomUUID().toString(),
        )
        saveInfo(info.copy(token = token))
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun saveInfo(info: Info) {
        GlobalScope.launch {
            infoDao.insert(info)
            // TODO save to firestore
//            remoteDatabase.upload(info)
        }
    }
}
