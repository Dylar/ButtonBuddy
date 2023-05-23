package de.bitb.buttonbuddy.ui.base.styles

import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.ui.graphics.Color

object BaseColors {
    val White = Color(0xFFFFFFFF)
    val Black = Color(0xFF000000)

    val LightGray = Color(0xFFE0E0E0)
    val DarkGray = Color(0xFF202020)

    val AdultBlue = Color(0xff0B5394)
    val LightGreen = Color(0xffe7ed9b)
    val BabyBlue = Color(0xff89CFF0)
    val SunYellow = Color(0xFFFFFF00)
    val ZergPurple = Color(0xffA259FF)
    val FireRed = Color(0xFFFF4500)
}

val lightColorPalette = lightColors(
    primary = BaseColors.AdultBlue,
//    primaryVariant = AdultBlue.copy(alpha = 0.70f),
    secondary = BaseColors.LightGreen,
//    secondaryVariant = LightGreen.copy(alpha = 0.70f),
//    background = White,
//    surface = DarkGray,
//    error = FireRed,
//
//    onPrimary = Black,
//    onSecondary = Black,
//    onBackground = Black,
//    onSurface = Black,
//    onError = White,
)

val darkColorPalette = darkColors(
    primary = BaseColors.BabyBlue,
//    primaryVariant = BabyBlue.copy(alpha = 0.70f),
    secondary = BaseColors.SunYellow,
//    secondaryVariant = SunYellow.copy(alpha = 0.70f),
//    background = Black,
//    surface = LightGray,
//    error = FireRed,
//
//    onPrimary = White,
//    onSecondary = White,
//    onBackground = White,
//    onSurface = White,
//    onError = White,
)