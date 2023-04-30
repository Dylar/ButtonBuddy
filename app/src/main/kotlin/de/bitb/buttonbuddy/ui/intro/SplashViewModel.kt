package de.bitb.buttonbuddy.ui.intro

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.bitb.buttonbuddy.R
import de.bitb.buttonbuddy.core.misc.atLeast
import de.bitb.buttonbuddy.data.SettingsRepository
import de.bitb.buttonbuddy.ui.base.BaseViewModel
import de.bitb.buttonbuddy.usecase.UserUseCases
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    override val settingsRepo: SettingsRepository,
    private val userUseCases: UserUseCases,
) : BaseViewModel() {

    fun loadData() {
        viewModelScope.launch {
            val userResp = atLeast(0) { userUseCases.loadDataUC() }
            val route = if (userResp.data != true) {
                R.id.splash_to_login
            } else {
                R.id.splash_to_buddies
            }
            navigate(route)
        }
    }
}
