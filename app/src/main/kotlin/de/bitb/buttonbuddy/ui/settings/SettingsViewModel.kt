package de.bitb.buttonbuddy.ui.settings

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.bitb.buttonbuddy.data.SettingsRepository
import de.bitb.buttonbuddy.data.model.Settings
import de.bitb.buttonbuddy.ui.base.BaseViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    override val settingsRepo: SettingsRepository,
) : BaseViewModel() {

    fun saveSettings(settings: Settings) {
        viewModelScope.launch {
            settingsRepo.saveSettings(settings)
        }
    }

}

