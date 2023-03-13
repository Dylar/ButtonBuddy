package de.bitb.buttonbuddy.ui.base.styles

import android.view.View
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import de.bitb.buttonbuddy.ui.base.BaseFragment
import de.bitb.buttonbuddy.ui.base.BaseViewModel

fun <T : BaseViewModel> BaseFragment<T>.createComposeView(
    content: @Composable () -> Unit
): View =
    ComposeView(requireContext()).apply {
        setContent {
            scaffoldState = rememberScaffoldState()
            ButtonBuddyAppTheme(useDarkTheme = isDarkMode()) { content() }
        }
    }

@Composable
fun ButtonBuddyAppTheme(useDarkTheme: Boolean, content: @Composable () -> Unit) {
    MaterialTheme(
        colors = if (useDarkTheme) darkColorPalette else lightColorPalette,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}