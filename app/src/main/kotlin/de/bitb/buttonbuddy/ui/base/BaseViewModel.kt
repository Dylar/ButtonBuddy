package de.bitb.buttonbuddy.ui.base

import androidx.lifecycle.ViewModel
import de.bitb.buttonbuddy.ui.base.composable.ResString

abstract class BaseViewModel : ViewModel() {
    lateinit var navigate: (Int, Int?) -> Unit
    fun navigateBack() = navigate(BACK_ID, null)

    open lateinit var showSnackBar: (ResString) -> Unit
}