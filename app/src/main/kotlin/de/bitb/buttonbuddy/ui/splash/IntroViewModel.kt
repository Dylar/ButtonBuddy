package de.bitb.buttonbuddy.ui.splash

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.bitb.buttonbuddy.R
import de.bitb.buttonbuddy.atLeast
import de.bitb.buttonbuddy.data.InfoRepository
import de.bitb.buttonbuddy.misc.Resource
import de.bitb.buttonbuddy.ui.base.BaseViewModel
import de.bitb.buttonbuddy.ui.composable.ResString
import de.bitb.buttonbuddy.usecase.buddies.BuddyUseCases
import de.bitb.buttonbuddy.usecase.info.InfoUseCases
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IntroViewModel @Inject constructor(
    private val infoRepo: InfoRepository,
    private val infoUseCases: InfoUseCases,
    private val buddyUseCases: BuddyUseCases,
) : BaseViewModel() {

    var firstName by mutableStateOf("")
    var lastName by mutableStateOf("")

    var error by mutableStateOf<ResString?>(null)

    fun loadData() {
        viewModelScope.launch {
            atLeast(2000) {
                val getInfoResp = infoRepo.getInfo()
                if (getInfoResp is Resource.Error) {
                    error = getInfoResp.message
                } else if (getInfoResp.data == null) {
                    navigate(R.id.splash_to_login, R.id.splashFragment)
                } else {
                    buddyUseCases.loadBuddiesUC()
                    navigate(R.id.splash_to_buddies, R.id.splashFragment)
                }
            }
        }
    }

    fun login() {
        error = null
        viewModelScope.launch {
            val result = infoUseCases.loginUC(firstName, lastName)
            if (result is Resource.Success) {
                navigate(R.id.login_to_buddies, R.id.loginFragment)
            } else {
                error = result.message
            }
        }
    }
}

