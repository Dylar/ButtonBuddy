package de.bitb.buttonbuddy.ui.base

import androidx.lifecycle.ViewModel
import de.bitb.buttonbuddy.data.SettingsRepository
import de.bitb.buttonbuddy.ui.base.composable.ResString
import javax.inject.Inject

abstract class BaseViewModel : ViewModel() {
    abstract val settingsRepo: SettingsRepository
    lateinit var navigate: (Int) -> Unit
    lateinit var navigateBack: (Int?) -> Unit
    open lateinit var showSnackbar: (ResString) -> Unit
}