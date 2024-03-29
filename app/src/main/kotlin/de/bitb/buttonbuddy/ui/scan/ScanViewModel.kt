package de.bitb.buttonbuddy.ui.scan

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.bitb.buttonbuddy.core.misc.Resource
import de.bitb.buttonbuddy.data.SettingsRepository
import de.bitb.buttonbuddy.ui.base.BaseViewModel
import de.bitb.buttonbuddy.ui.base.permission.PermissionHandler
import de.bitb.buttonbuddy.ui.base.permission.PermissionHandlerImpl
import de.bitb.buttonbuddy.usecase.BuddyUseCases
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScanViewModel @Inject constructor(
    override val settingsRepo: SettingsRepository,
    private val useCases: BuddyUseCases,
) : BaseViewModel(), PermissionHandler by PermissionHandlerImpl() {

    fun onScan(scanText: String) {
        viewModelScope.launch {
            val res = useCases.scanBuddyUC(scanText)
            if (res is Resource.Error) {
                showSnackbar(res.message!!)
            } else {
                navigateBack(null)
            }
        }
    }
}

