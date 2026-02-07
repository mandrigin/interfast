package com.interfast.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Interfast Typography System
 *
 * Three font families:
 * - Space Grotesk: Display and headings (geometric sans)
 * - Inter: Body text (neutral, readable)
 * - JetBrains Mono: Data display (monospace, technical)
 *
 * NOTE: For production, download these fonts from Google Fonts:
 * - https://fonts.google.com/specimen/Space+Grotesk
 * - https://fonts.google.com/specimen/Inter
 * - https://fonts.google.com/specimen/JetBrains+Mono
 *
 * Then uncomment the Font() declarations below.
 */

// Production font configuration (uncomment after adding font files):
// val SpaceGrotesk = FontFamily(
//     Font(R.font.space_grotesk_regular, FontWeight.Normal),
//     Font(R.font.space_grotesk_medium, FontWeight.Medium),
//     Font(R.font.space_grotesk_bold, FontWeight.Bold)
// )
//
// val Inter = FontFamily(
//     Font(R.font.inter_regular, FontWeight.Normal),
//     Font(R.font.inter_medium, FontWeight.Medium),
//     Font(R.font.inter_semibold, FontWeight.SemiBold)
// )
//
// val JetBrainsMono = FontFamily(
//     Font(R.font.jetbrains_mono_regular, FontWeight.Normal),
//     Font(R.font.jetbrains_mono_medium, FontWeight.Medium)
// )

// Fallback to system fonts for development builds
val SpaceGrotesk = FontFamily.SansSerif
val Inter = FontFamily.SansSerif
val JetBrainsMono = FontFamily.Monospace

// Custom text styles for the app
object InterfastTypography {
    // Display - Timer digits
    val displayLarge = TextStyle(
        fontFamily = SpaceGrotesk,
        fontWeight = FontWeight.Bold,
        fontSize = 72.sp,
        letterSpacing = (-2).sp,
        lineHeight = 80.sp
    )

    val displayMedium = TextStyle(
        fontFamily = SpaceGrotesk,
        fontWeight = FontWeight.Bold,
        fontSize = 56.sp,
        letterSpacing = (-1.5).sp,
        lineHeight = 64.sp
    )

    val displaySmall = TextStyle(
        fontFamily = SpaceGrotesk,
        fontWeight = FontWeight.Bold,
        fontSize = 40.sp,
        letterSpacing = (-1).sp,
        lineHeight = 48.sp
    )

    // Headings
    val headlineLarge = TextStyle(
        fontFamily = SpaceGrotesk,
        fontWeight = FontWeight.Medium,
        fontSize = 24.sp,
        letterSpacing = (-0.5).sp,
        lineHeight = 32.sp
    )

    val headlineMedium = TextStyle(
        fontFamily = SpaceGrotesk,
        fontWeight = FontWeight.Medium,
        fontSize = 20.sp,
        letterSpacing = 0.sp,
        lineHeight = 28.sp
    )

    val headlineSmall = TextStyle(
        fontFamily = SpaceGrotesk,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        letterSpacing = 0.sp,
        lineHeight = 24.sp
    )

    // Body - Inter
    val bodyLarge = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        letterSpacing = 0.sp,
        lineHeight = 24.sp
    )

    val bodyMedium = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        letterSpacing = 0.sp,
        lineHeight = 20.sp
    )

    val bodySmall = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        letterSpacing = 0.sp,
        lineHeight = 16.sp
    )

    // Labels
    val labelLarge = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        letterSpacing = 0.5.sp,
        lineHeight = 20.sp
    )

    val labelMedium = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        letterSpacing = 0.5.sp,
        lineHeight = 16.sp
    )

    val labelSmall = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.Medium,
        fontSize = 10.sp,
        letterSpacing = 0.5.sp,
        lineHeight = 14.sp
    )

    // Data - JetBrains Mono
    val dataLarge = TextStyle(
        fontFamily = JetBrainsMono,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        letterSpacing = 0.5.sp,
        lineHeight = 24.sp
    )

    val dataMedium = TextStyle(
        fontFamily = JetBrainsMono,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        letterSpacing = 0.5.sp,
        lineHeight = 20.sp
    )

    val dataSmall = TextStyle(
        fontFamily = JetBrainsMono,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        letterSpacing = 0.5.sp,
        lineHeight = 16.sp
    )

    // Timer specific
    val timerPrimary = TextStyle(
        fontFamily = SpaceGrotesk,
        fontWeight = FontWeight.Bold,
        fontSize = 48.sp,
        letterSpacing = (-1).sp,
        lineHeight = 56.sp
    )

    val timerSecondary = TextStyle(
        fontFamily = SpaceGrotesk,
        fontWeight = FontWeight.Medium,
        fontSize = 24.sp,
        letterSpacing = 0.sp,
        lineHeight = 32.sp
    )
}

// Material 3 Typography integration
val Typography = Typography(
    displayLarge = InterfastTypography.displayLarge,
    displayMedium = InterfastTypography.displayMedium,
    displaySmall = InterfastTypography.displaySmall,
    headlineLarge = InterfastTypography.headlineLarge,
    headlineMedium = InterfastTypography.headlineMedium,
    headlineSmall = InterfastTypography.headlineSmall,
    bodyLarge = InterfastTypography.bodyLarge,
    bodyMedium = InterfastTypography.bodyMedium,
    bodySmall = InterfastTypography.bodySmall,
    labelLarge = InterfastTypography.labelLarge,
    labelMedium = InterfastTypography.labelMedium,
    labelSmall = InterfastTypography.labelSmall,
    titleLarge = InterfastTypography.headlineLarge,
    titleMedium = InterfastTypography.headlineMedium,
    titleSmall = InterfastTypography.headlineSmall
)
