package de.bitb.buttonbuddy.core

import androidx.annotation.StringRes
import androidx.test.platform.app.InstrumentationRegistry

fun getString(@StringRes id: Int, vararg args: Any): String {
    return InstrumentationRegistry.getInstrumentation().targetContext.resources.getString(id, *args)
}
