package de.bitb.buttonbuddy.core

import android.content.Context
import androidx.annotation.StringRes
import androidx.test.platform.app.InstrumentationRegistry

fun getString(@StringRes id: Int, ctx: Context? = null): String {
    val context = ctx ?: InstrumentationRegistry.getInstrumentation().targetContext
    return context.resources.getString(id)
}