package com.interfast.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * Surface tokens — the screens read these instead of branching on
 * `isSystemInDarkTheme()` themselves. Accent ([accent]) is the single brand
 * color and stays the same across light/dark.
 */
data class SurfaceTokens(
    val background: Color,
    val surface: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val divider: Color,
    val accent: Color,
    val onAccent: Color,
)

internal val DarkSurfaceTokens = SurfaceTokens(
    background = InterfastColors.VoidBlack,
    surface = InterfastColors.Gray10,
    textPrimary = InterfastColors.PureWhite,
    textSecondary = InterfastColors.Gray60,
    divider = InterfastColors.Gray20,
    accent = InterfastColors.GlyphRed,
    onAccent = InterfastColors.PureWhite,
)

internal val LightSurfaceTokens = SurfaceTokens(
    background = InterfastColors.PureWhite,
    surface = InterfastColors.Gray95,
    textPrimary = InterfastColors.VoidBlack,
    textSecondary = InterfastColors.Gray40,
    divider = InterfastColors.Gray20,
    accent = InterfastColors.GlyphRed,
    onAccent = InterfastColors.PureWhite,
)

val LocalSurfaceTokens = staticCompositionLocalOf { DarkSurfaceTokens }

/**
 * Interfast Dark Color Scheme
 *
 * Primary design is dark-mode first, matching the aesthetic influences:
 * Nothing Phone's dark UI, terminal aesthetics, Swiss modernism
 */
private val DarkColorScheme = darkColorScheme(
    primary = InterfastColors.GlyphRed,
    onPrimary = InterfastColors.PureWhite,
    primaryContainer = InterfastColors.Gray15,
    onPrimaryContainer = InterfastColors.GlyphRed,

    secondary = InterfastColors.SignalCyan,
    onSecondary = InterfastColors.VoidBlack,
    secondaryContainer = InterfastColors.Gray15,
    onSecondaryContainer = InterfastColors.SignalCyan,

    tertiary = InterfastColors.PhosphorGreen,
    onTertiary = InterfastColors.VoidBlack,
    tertiaryContainer = InterfastColors.Gray15,
    onTertiaryContainer = InterfastColors.PhosphorGreen,

    error = InterfastColors.GlyphRed,
    onError = InterfastColors.PureWhite,
    errorContainer = InterfastColors.Gray15,
    onErrorContainer = InterfastColors.GlyphRed,

    background = InterfastColors.VoidBlack,
    onBackground = InterfastColors.PureWhite,

    surface = InterfastColors.VoidBlack,
    onSurface = InterfastColors.PureWhite,
    surfaceVariant = InterfastColors.Gray10,
    onSurfaceVariant = InterfastColors.Gray80,

    outline = InterfastColors.Gray20,
    outlineVariant = InterfastColors.Gray15,

    scrim = InterfastColors.VoidBlack,
    inverseSurface = InterfastColors.Gray80,
    inverseOnSurface = InterfastColors.VoidBlack,
    inversePrimary = InterfastColors.GlyphRed
)

/**
 * Light scheme — accent stays GlyphRed; surfaces flip to white/near-white.
 */
private val LightColorScheme = lightColorScheme(
    primary = InterfastColors.GlyphRed,
    onPrimary = InterfastColors.PureWhite,
    primaryContainer = InterfastColors.GlyphRed.copy(alpha = 0.1f),
    onPrimaryContainer = InterfastColors.GlyphRed,

    secondary = InterfastColors.SignalCyan,
    onSecondary = InterfastColors.VoidBlack,
    secondaryContainer = InterfastColors.SignalCyan.copy(alpha = 0.1f),
    onSecondaryContainer = InterfastColors.SignalCyan,

    tertiary = InterfastColors.PhosphorGreen,
    onTertiary = InterfastColors.VoidBlack,
    tertiaryContainer = InterfastColors.PhosphorGreen.copy(alpha = 0.1f),
    onTertiaryContainer = InterfastColors.PhosphorGreen,

    background = InterfastColors.PureWhite,
    onBackground = InterfastColors.VoidBlack,

    surface = InterfastColors.PureWhite,
    onSurface = InterfastColors.VoidBlack,
    surfaceVariant = InterfastColors.Gray95,
    onSurfaceVariant = InterfastColors.Gray40
)

@Composable
fun InterfastTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Disabled to preserve brand colors
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val surfaceTokens = if (darkTheme) DarkSurfaceTokens else LightSurfaceTokens

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = surfaceTokens.background.toArgb()
            window.navigationBarColor = surfaceTokens.background.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    CompositionLocalProvider(LocalSurfaceTokens provides surfaceTokens) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}

/**
 * Spacing constants following 8dp grid system
 */
object Spacing {
    val xs = androidx.compose.ui.unit.Dp(4f)
    val sm = androidx.compose.ui.unit.Dp(8f)
    val md = androidx.compose.ui.unit.Dp(16f)
    val lg = androidx.compose.ui.unit.Dp(24f)
    val xl = androidx.compose.ui.unit.Dp(32f)
    val xxl = androidx.compose.ui.unit.Dp(48f)
    val xxxl = androidx.compose.ui.unit.Dp(64f)
}

/**
 * Animation durations
 */
object Motion {
    const val DURATION_FAST = 150
    const val DURATION_STANDARD = 250
    const val DURATION_EMPHASIS = 400

    // Timer pulse duration
    const val DURATION_PULSE = 500
}
