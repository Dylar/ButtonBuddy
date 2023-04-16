package de.bitb.buttonbuddy.ui.intro

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.bitb.buttonbuddy.R
import de.bitb.buttonbuddy.core.misc.Resource
import de.bitb.buttonbuddy.data.SettingsRepository
import de.bitb.buttonbuddy.ui.base.BaseViewModel
import de.bitb.buttonbuddy.ui.base.composable.ResString
import de.bitb.buttonbuddy.usecase.UserUseCases
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    override val settingsRepo: SettingsRepository,
    private val userUseCases: UserUseCases,
) : BaseViewModel() {

    var email by mutableStateOf("")
    var pw by mutableStateOf("")

    var error by mutableStateOf<ResString?>(null)

    fun login() {
        error = null
        viewModelScope.launch {
            val result = userUseCases.loginUC(email, pw)
            if (result is Resource.Success) {
                navigate(R.id.login_to_buddies)
            } else {
                error = result.message
            }
        }
    }
}



