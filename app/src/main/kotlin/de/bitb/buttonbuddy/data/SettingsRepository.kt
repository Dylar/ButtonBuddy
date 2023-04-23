package de.bitb.buttonbuddy.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import de.bitb.buttonbuddy.data.source.*
import de.bitb.buttonbuddy.core.misc.Resource
import de.bitb.buttonbuddy.data.model.Settings
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

interface SettingsRepository {
    fun getLiveSettings(): LiveData<Settings>
    suspend fun getSettings(): Resource<Settings>
    suspend fun saveSettings(settings: Settings): Resource<Unit>
    suspend fun loadSettings(uuid: String): Resource<Map<String, Long>>
}

class SettingsRepositoryImpl constructor(
    private val remoteDB: RemoteService,
    private val localDB: LocalDatabase,
) : SettingsRepository {
    private val settingsData = MutableLiveData<Settings>()

    override fun getLiveSettings(): LiveData<Settings> {
        if (settingsData.value == null) {
            //TODO make anders
            GlobalScope.launch {
                val settings = getSettings()
                settingsData.postValue(settings.data!!)
            }
        }
        return settingsData
    }

    override suspend fun getSettings(): Resource<Settings> {
        return try {
            val cooldowns = localDB.getCoolDowns()
            Resource.Success(Settings(buddysCooldown = cooldowns))
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    override suspend fun saveSettings(settings: Settings): Resource<Unit> {
        return try {
            localDB.setCoolDowns(settings.buddysCooldown) //TODO save all shit
            localDB.setDarkMode(settings.isDarkMode)
            settingsData.postValue(settings)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    override suspend fun loadSettings(uuid: String): Resource<Map<String, Long>> {
        return try {
            val response = remoteDB.loadCooldowns(uuid)
            //TODO load all shit
            if (response is Resource.Success) {
                localDB.setCoolDowns(response.data!!)
            }
            response
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }
}
