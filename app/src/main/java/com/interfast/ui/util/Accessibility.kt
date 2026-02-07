package com.interfast.ui.util

import android.content.Context
import android.view.accessibility.AccessibilityManager
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import java.time.Duration

/**
 * Accessibility utilities for Interfast.
 *
 * Design philosophy: Accessibility is not an afterthought.
 * Every user should be able to track their fasting regardless of ability.
 *
 * Key considerations:
 * - TalkBack screen reader support
 * - High contrast mode
 * - Large text scaling
 * - Clear focus indicators
 * - Meaningful content descriptions
 */

/**
 * Accessibility state holder providing context about device accessibility settings.
 */
data class AccessibilityState(
    val isTalkBackEnabled: Boolean,
    val isHighContrastEnabled: Boolean,
    val fontScale: Float
) {
    val isLargeText: Boolean
        get() = fontScale >= 1.3f

    val useHighContrast: Boolean
        get() = isHighContrastEnabled || isTalkBackEnabled
}

/**
 * CompositionLocal for accessibility state.
 */
val LocalAccessibility = compositionLocalOf {
    AccessibilityState(
        isTalkBackEnabled = false,
        isHighContrastEnabled = false,
        fontScale = 1f
    )
}

/**
 * Provider for accessibility state that reads from system settings.
 */
@Composable
fun AccessibilityProvider(
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val accessibilityState = remember(context) {
        val accessibilityManager = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        val fontScale = context.resources.configuration.fontScale

        AccessibilityState(
            isTalkBackEnabled = accessibilityManager.isTouchExplorationEnabled,
            isHighContrastEnabled = accessibilityManager.isEnabled &&
                    context.resources.configuration.uiMode and
                    android.content.res.Configuration.UI_MODE_NIGHT_MASK ==
                    android.content.res.Configuration.UI_MODE_NIGHT_YES,
            fontScale = fontScale
        )
    }

    CompositionLocalProvider(LocalAccessibility provides accessibilityState) {
        content()
    }
}

/**
 * Semantic wrapper for timer display.
 *
 * Provides clear, speakable descriptions for screen readers.
 */
@Composable
fun TimerSemantics(
    elapsed: Duration,
    target: Duration,
    progress: Float,
    isFasting: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val description = buildTimerDescription(elapsed, target, progress, isFasting)

    Box(
        modifier = modifier.semantics {
            contentDescription = description
        }
    ) {
        content()
    }
}

private fun buildTimerDescription(
    elapsed: Duration,
    target: Duration,
    progress: Float,
    isFasting: Boolean
): String {
    val hours = elapsed.toHours()
    val minutes = elapsed.toMinutesPart()

    val targetHours = target.toHours()
    val percentComplete = (progress * 100).toInt()

    return if (isFasting) {
        "Fasting timer. $hours hours and $minutes minutes elapsed. " +
        "$percentComplete percent complete of $targetHours hour goal."
    } else {
        "Timer ready. Goal is $targetHours hours. Tap Start Fast to begin."
    }
}

/**
 * Semantic wrapper for buttons with role and state.
 */
fun Modifier.buttonSemantics(
    label: String,
    enabled: Boolean = true,
    isToggle: Boolean = false,
    toggleState: Boolean = false
): Modifier = this.semantics {
    contentDescription = label
    role = Role.Button
    if (isToggle) {
        stateDescription = if (toggleState) "On" else "Off"
    }
}

/**
 * Semantic wrapper for streak display.
 */
fun Modifier.streakSemantics(
    days: Int
): Modifier = this.semantics {
    contentDescription = when (days) {
        0 -> "No current streak"
        1 -> "1 day streak"
        else -> "$days day streak"
    }
}

/**
 * Semantic wrapper for completion percentage.
 */
fun Modifier.progressSemantics(
    percentage: Float
): Modifier = this.semantics {
    val percent = (percentage * 100).toInt()
    contentDescription = "$percent percent complete"
}

/**
 * Semantic wrapper for protocol cards.
 */
fun Modifier.protocolSemantics(
    name: String,
    fastingHours: Int,
    eatingHours: Int,
    isSelected: Boolean
): Modifier = this.semantics {
    contentDescription = "$name protocol. $fastingHours hours fasting, $eatingHours hours eating window." +
            if (isSelected) " Currently selected." else ""
    role = Role.RadioButton
}

/**
 * Formats duration for accessibility announcement.
 */
fun Duration.toAccessibleString(): String {
    val hours = this.toHours()
    val minutes = this.toMinutesPart()
    val seconds = this.toSecondsPart()

    return buildString {
        if (hours > 0) {
            append("$hours hour")
            if (hours > 1) append("s")
            append(" ")
        }
        if (minutes > 0) {
            append("$minutes minute")
            if (minutes > 1) append("s")
            append(" ")
        }
        if (hours == 0L && seconds > 0) {
            append("$seconds second")
            if (seconds > 1) append("s")
        }
    }.trim()
}

/**
 * Extension to add appropriate touch target size for accessibility.
 * Note: Material Design requires minimum 48dp touch targets.
 */
object AccessibilityDefaults {
    val MinTouchTarget = androidx.compose.ui.unit.Dp(48f)
    val MinFocusIndicator = androidx.compose.ui.unit.Dp(2f)
}
