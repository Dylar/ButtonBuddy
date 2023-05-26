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
import de.bitb.buttonbuddy.usecase.UserUseCases
import de.bitb.buttonbuddy.usecase.user.RegisterResponse
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    override val settingsRepo: SettingsRepository,
    private val userUseCases: UserUseCases,
) : BaseViewModel() {

    var firstName by mutableStateOf("")
    var lastName by mutableStateOf("")
    var email by mutableStateOf("")
    var pw1 by mutableStateOf("")
    var pw2 by mutableStateOf("")

    var error by mutableStateOf<RegisterResponse?>(null)

    fun register() {
        error = null
        viewModelScope.launch {
            val result = userUseCases.registerUC(firstName, lastName, email, pw1, pw2)
            if (result is Resource.Error) {
                error = result.data
            } else {
                navigate(R.id.register_to_buddies)
            }
        }
    }
}



