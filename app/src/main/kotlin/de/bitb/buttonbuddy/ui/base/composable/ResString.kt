package de.bitb.buttonbuddy.ui.base.composable

import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

sealed class ResString {
    data class DynamicString(val value: String) : ResString()
    class ResourceString(
        @StringRes val id: Int,
        val args: Array<Any> = emptyArray()
    ) : ResString()

    @Composable
    fun asString(): String {
        return when (this) {
            is DynamicString -> value
            is ResourceString -> stringResource(id, args)
        }
    }

    fun asString(stringResource: (Int, args: Array<Any>) -> String): String {
        return when (this) {
            is DynamicString -> value
            is ResourceString -> stringResource(id, args)
        }
    }
}

fun Int.asResString() : ResString = ResString.ResourceString(this)
fun String.asResString() : ResString = ResString.DynamicString(this)
