package de.bitb.buttonbuddy.ui.base

import androidx.lifecycle.ViewModel

abstract class BaseViewModel : ViewModel() {
    lateinit var navigate: (Int, Int?) -> Unit
    fun navigateBack() = navigate(BACK_ID, null)

    lateinit var showSnackbar: (String) -> Unit
}