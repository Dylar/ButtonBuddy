package de.bitb.buttonbuddy.core.misc

import androidx.annotation.StringRes
import de.bitb.buttonbuddy.ui.base.composable.ResString

sealed class Resource<T>(val data: T? = null, val message: ResString? = null) {
    class Success<T>(data: T?) : Resource<T>(data) {
        constructor() : this(null)
    }

    class Error<T> constructor(message: ResString, data: T? = null) : Resource<T>(data, message) {
        constructor(e: Throwable, data: T? = null) : this(e.message ?: e.toString(), data)
        constructor(message: String, data: T? = null)
                : this(ResString.DynamicString(message), data)

        constructor(@StringRes stringId: Int, data: T? = null)
                : this(ResString.ResourceString(stringId), data)

        fun <E> castTo(): Error<E> {
            return Error(message!!)
        }
    }

    val hasData: Boolean
        get() = data != null
}

fun <T> Int.asResourceError(): Resource.Error<T> = Resource.Error(this)
fun <T> String.asResourceError(): Resource.Error<T> = Resource.Error(this)
fun <T> Throwable.asResourceError(): Resource.Error<T> = Resource.Error(this)

suspend fun <T> tryIt(onTry: suspend () -> Resource<T>): Resource<T> {
    return try {
        onTry()
    } catch (e: Exception) {
        e.printStackTrace()
        e.asResourceError()
    }
}