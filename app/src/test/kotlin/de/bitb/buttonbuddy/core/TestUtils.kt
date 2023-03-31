package de.bitb.buttonbuddy.core

import androidx.annotation.StringRes

fun getString(@StringRes id: Int, vararg args: Any): String {
    return id.toString() + args.joinToString { " $it" }
}

