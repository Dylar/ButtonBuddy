package de.bitb.buttonbuddy.ui.scan

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.bitb.buttonbuddy.misc.Resource
import de.bitb.buttonbuddy.ui.base.BaseViewModel
import de.bitb.buttonbuddy.ui.composable.ResString
import de.bitb.buttonbuddy.ui.permission.PermissionHandler
import de.bitb.buttonbuddy.ui.permission.PermissionHandlerImpl
import de.bitb.buttonbuddy.usecase.buddies.BuddyUseCases
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScanViewModel @Inject constructor(
    private val useCases: BuddyUseCases,
) : BaseViewModel(), PermissionHandler by PermissionHandlerImpl() {

    var error by mutableStateOf<ResString?>(null)

    fun onScan(scanText: String) {
        error = null
        viewModelScope.launch {
            val res = useCases.scanBuddy(scanText)
            if (res is Resource.Error) {
                error = res.message
            } else {
                navigateBack()
            }
        }
    }
}

