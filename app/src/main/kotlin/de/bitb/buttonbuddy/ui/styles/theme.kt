package de.bitb.buttonbuddy.ui.styles

import android.view.View
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment

fun Fragment.createComposeView(darkTheme: Boolean? = null, content: @Composable () -> Unit): View =
    ComposeView(requireContext()).apply {
        setContent { ButtonBuddyAppTheme(darkTheme = darkTheme) { content() } }
    }

@Composable
fun ButtonBuddyAppTheme(darkTheme: Boolean?, content: @Composable () -> Unit) {
    val useDarkTheme = darkTheme ?: isSystemInDarkTheme()
    MaterialTheme(
        colors = if (useDarkTheme) darkColorPalette else lightColorPalette,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}