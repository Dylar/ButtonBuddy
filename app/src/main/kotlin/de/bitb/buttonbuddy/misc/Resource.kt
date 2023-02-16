package de.bitb.buttonbuddy.misc

import de.bitb.buttonbuddy.ui.composable.UiText

sealed class Resource<T>(val data: T? = null, val message: UiText? = null) {
    class Success<T>(data: T?): Resource<T>(data)
    class Error<T>(message: UiText, data: T? = null): Resource<T>(data, message)
}
