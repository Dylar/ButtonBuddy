package de.bitb.buttonbuddy.ui.base.styles

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun ButtonBuddyAppTheme(useDarkTheme: Boolean, content: @Composable () -> Unit) {
    MaterialTheme(
        colors = if (useDarkTheme) darkColorPalette else lightColorPalette,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}