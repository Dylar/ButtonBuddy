package de.bitb.buttonbuddy.core

import androidx.annotation.StringRes
import de.bitb.buttonbuddy.core.misc.Resource

fun getString(@StringRes id: Int, vararg args: Any): String {
    return id.toString() + args.joinToString { " $it" }
}

fun Resource<*>.getMessageString(): String {
    return message?.asString(::getString) ?: throw NullPointerException()
}

