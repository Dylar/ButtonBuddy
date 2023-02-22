package de.bitb.buttonbuddy.ui.base

import androidx.lifecycle.ViewModel
import de.bitb.buttonbuddy.ui.BACK_ID

abstract class BaseViewModel : ViewModel() {
    lateinit var navigate: (Int, Int?) -> Unit
    fun navigateBack() = navigate(BACK_ID, null)
}