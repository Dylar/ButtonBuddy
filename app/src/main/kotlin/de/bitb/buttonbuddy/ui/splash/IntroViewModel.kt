package de.bitb.buttonbuddy.ui.splash

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.bitb.buttonbuddy.R
import de.bitb.buttonbuddy.data.InfoRepository
import de.bitb.buttonbuddy.misc.Resource
import de.bitb.buttonbuddy.ui.composable.UiText
import de.bitb.buttonbuddy.usecase.buddies.BuddyUseCases
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IntroViewModel @Inject constructor(
    private val infoRepo: InfoRepository,
    private val useCases: BuddyUseCases,
) : ViewModel() {
    lateinit var navigate: (Int) -> Unit

    var firstName by mutableStateOf("")
    var lastName by mutableStateOf("")

    var error by mutableStateOf<UiText?>(null)

    fun loadData() {
        viewModelScope.launch {
            useCases.loadBuddies()
            delay(3000) //TODO delete me
            val info = infoRepo.getInfo()
            if (info == null) {
                navigate(R.id.splash_to_buddies)
            } else {
                navigate(R.id.splash_to_login) // TODO ja klär das mit navi
            }
        }
    }

    fun login() {
        viewModelScope.launch {
            val result = useCases.login(firstName, lastName)
            if (result is Resource.Success) {
                navigate(R.id.login_to_buddies)
            } else {
                error = result.message
            }
        }
    }
}

