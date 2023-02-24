package de.bitb.buttonbuddy.misc

import de.bitb.buttonbuddy.ui.composable.ResString

sealed class Resource<T>(val data: T? = null, val message: ResString? = null) {
    class Success<T>(data: T?) : Resource<T>(data)
    class Error<T> constructor(message: ResString, data: T? = null) : Resource<T>(data, message) {
        constructor(message: String, data: T? = null) : this(ResString.DynamicString(message), data)
        constructor(e: Throwable, data: T? = null) : this(e.toString(), data)
    }

    val hasData: Boolean
        get() = data != null
}
