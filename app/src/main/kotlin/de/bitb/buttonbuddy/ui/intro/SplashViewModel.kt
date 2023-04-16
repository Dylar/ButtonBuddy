package de.bitb.buttonbuddy.ui.intro

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.bitb.buttonbuddy.R
import de.bitb.buttonbuddy.data.UserRepository
import de.bitb.buttonbuddy.core.misc.atLeast
import de.bitb.buttonbuddy.data.SettingsRepository
import de.bitb.buttonbuddy.ui.base.BaseViewModel
import de.bitb.buttonbuddy.usecase.BuddyUseCases
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    override val settingsRepo: SettingsRepository,
    private val userRepo: UserRepository,
    private val buddyUseCases: BuddyUseCases,
) : BaseViewModel() {

    fun loadData() {
        viewModelScope.launch {
            atLeast(2000) {
                val userResp = userRepo.getUser()
                val route = if (userResp.data == null) {
                    R.id.splash_to_login
                } else {
                    buddyUseCases.loadBuddiesUC()
                    R.id.splash_to_buddies
                }
                navigate(route)
            }
        }
    }
}
