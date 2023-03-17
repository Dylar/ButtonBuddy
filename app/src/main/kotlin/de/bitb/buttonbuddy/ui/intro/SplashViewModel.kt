package de.bitb.buttonbuddy.ui.intro

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.bitb.buttonbuddy.R
import de.bitb.buttonbuddy.data.InfoRepository
import de.bitb.buttonbuddy.core.misc.atLeast
import de.bitb.buttonbuddy.ui.base.BaseViewModel
import de.bitb.buttonbuddy.usecase.BuddyUseCases
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val infoRepo: InfoRepository,
    private val buddyUseCases: BuddyUseCases,
) : BaseViewModel() {

    fun loadData() {
        viewModelScope.launch {
            atLeast(2000) {
                val getInfoResp = infoRepo.getInfo()
                val route = if (getInfoResp.data == null) {
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
