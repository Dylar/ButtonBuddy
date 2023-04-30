package de.bitb.buttonbuddy.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import de.bitb.buttonbuddy.core.misc.Resource
import de.bitb.buttonbuddy.core.misc.tryIt
import de.bitb.buttonbuddy.data.model.Settings
import de.bitb.buttonbuddy.data.source.LocalDatabase
import de.bitb.buttonbuddy.data.source.RemoteService
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

interface SettingsRepository {
    fun getLiveSettings(): LiveData<Settings>
    suspend fun getSettings(): Resource<Settings>
    suspend fun saveSettings(settings: Settings): Resource<Unit>
    suspend fun loadSettings(uuid: String): Resource<Map<String, Long>>
}
// TODO write tests
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
        return tryIt {
            val cooldowns = localDB.getCoolDowns()
            Resource.Success(Settings(buddysCooldown = cooldowns))
        }
    }

    override suspend fun saveSettings(settings: Settings): Resource<Unit> {
        return tryIt {
            localDB.setCoolDowns(settings.buddysCooldown) //TODO save all shit
            localDB.setDarkMode(settings.isDarkMode)
            settingsData.postValue(settings)
            Resource.Success()
        }
    }

    override suspend fun loadSettings(uuid: String): Resource<Map<String, Long>> {
        return tryIt {
            val response = remoteDB.loadCooldowns(uuid)
            //TODO load all shit
            if (response is Resource.Success) {
                localDB.setCoolDowns(response.data!!)
            }
            response
        }
    }
}
