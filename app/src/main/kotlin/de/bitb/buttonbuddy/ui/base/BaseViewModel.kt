package de.bitb.buttonbuddy.ui.base

import androidx.lifecycle.ViewModel
import de.bitb.buttonbuddy.ui.base.composable.ResString

abstract class BaseViewModel : ViewModel() {
    lateinit var navigate: (Int) -> Unit
    lateinit var navigateBack: (Int?) -> Unit
    open lateinit var showSnackbar: (ResString) -> Unit
}