package de.bitb.buttonbuddy.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import de.bitb.buttonbuddy.data.source.*
import de.bitb.buttonbuddy.core.misc.Resource
import de.bitb.buttonbuddy.data.model.Settings

interface SettingsRepository {
    fun getLiveSettings(): LiveData<Settings>
    suspend fun getSettings(): Resource<Settings>
    suspend fun saveSettings(settings: Settings): Resource<Unit>
}

class SettingsRepositoryImpl constructor(
    private val remoteDB: RemoteService,
    private val localDB: LocalDatabase,
) : SettingsRepository {
    private val settingsData = MutableLiveData<Settings>()

    override fun getLiveSettings(): LiveData<Settings> = settingsData

    override suspend fun getSettings(): Resource<Settings> {
        return try {
            val cooldown = localDB.getCoolDown()
            Resource.Success(Settings(cooldown))
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    override suspend fun saveSettings(settings: Settings): Resource<Unit> {
        return try {
            localDB.setCoolDown(settings.cooldown)
            settingsData.value = settings
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }
}
