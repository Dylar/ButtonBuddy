package de.bitb.buttonbuddy.core.misc

import androidx.annotation.StringRes
import de.bitb.buttonbuddy.ui.base.composable.ResString

sealed class Resource<T>(val data: T? = null, val message: ResString? = null) {
    class Success<T>(data: T?) : Resource<T>(data)
    class Error<T> constructor(message: ResString, data: T? = null) : Resource<T>(data, message) {
        constructor(message: String, data: T? = null) : this(ResString.DynamicString(message), data)
        constructor(@StringRes stringId: Int, data: T? = null) : this(ResString.ResourceString(stringId), data)
        constructor(e: Throwable, data: T? = null) : this(e.toString(), data)
    }

    val hasData: Boolean
        get() = data != null
}
