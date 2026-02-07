package com.interfast.ui.util

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.HapticFeedbackConstants
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView

/**
 * Haptic feedback patterns for Interfast interactions.
 *
 * Design philosophy: Haptics should feel intentional, not decorative.
 * Each pattern corresponds to a specific user action or system event.
 */
object HapticPatterns {
    // Light feedback for minor interactions
    val TICK = HapticType.Tick

    // Standard click for button presses
    val CLICK = HapticType.Click

    // Heavy click for significant actions (start/stop fast)
    val HEAVY_CLICK = HapticType.HeavyClick

    // Double tap for milestones
    val DOUBLE_TAP = HapticType.DoubleTap

    // Success pattern for fast completion
    val SUCCESS = HapticType.Success

    // Rejection pattern for invalid actions
    val REJECT = HapticType.Reject
}

sealed class HapticType {
    object Tick : HapticType()
    object Click : HapticType()
    object HeavyClick : HapticType()
    object DoubleTap : HapticType()
    object Success : HapticType()
    object Reject : HapticType()
}

/**
 * Haptic feedback controller for Compose.
 *
 * Usage:
 * ```
 * val haptics = rememberHapticFeedback()
 * Button(onClick = { haptics.perform(HapticPatterns.CLICK) }) { ... }
 * ```
 */
class HapticFeedbackController(
    private val view: View,
    private val vibrator: Vibrator?
) {
    /**
     * Performs haptic feedback based on the pattern type.
     */
    fun perform(type: HapticType) {
        when (type) {
            is HapticType.Tick -> performTick()
            is HapticType.Click -> performClick()
            is HapticType.HeavyClick -> performHeavyClick()
            is HapticType.DoubleTap -> performDoubleTap()
            is HapticType.Success -> performSuccess()
            is HapticType.Reject -> performReject()
        }
    }

    private fun performTick() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
        } else {
            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
        }
    }

    private fun performClick() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
        } else {
            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
        }
    }

    private fun performHeavyClick() {
        vibrator?.let { vib ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                vib.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_HEAVY_CLICK))
            } else {
                @Suppress("DEPRECATION")
                vib.vibrate(50)
            }
        }
    }

    private fun performDoubleTap() {
        vibrator?.let { vib ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val timings = longArrayOf(0, 40, 80, 40)
                val amplitudes = intArrayOf(0, 180, 0, 180)
                vib.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
            } else {
                @Suppress("DEPRECATION")
                vib.vibrate(longArrayOf(0, 40, 80, 40), -1)
            }
        }
    }

    private fun performSuccess() {
        vibrator?.let { vib ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // Ascending pattern: short-medium-long
                val timings = longArrayOf(0, 30, 60, 50, 80, 80)
                val amplitudes = intArrayOf(0, 120, 0, 180, 0, 255)
                vib.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
            } else {
                @Suppress("DEPRECATION")
                vib.vibrate(longArrayOf(0, 30, 60, 50, 80, 80), -1)
            }
        }
    }

    private fun performReject() {
        vibrator?.let { vib ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // Quick double buzz
                val timings = longArrayOf(0, 50, 50, 50)
                val amplitudes = intArrayOf(0, 200, 0, 200)
                vib.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
            } else {
                @Suppress("DEPRECATION")
                vib.vibrate(longArrayOf(0, 50, 50, 50), -1)
            }
        }
    }
}

/**
 * Remember a HapticFeedbackController instance.
 */
@Composable
fun rememberHapticFeedback(): HapticFeedbackController {
    val view = LocalView.current
    val context = LocalContext.current

    return remember(view, context) {
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }

        HapticFeedbackController(view, vibrator)
    }
}

/**
 * Extension function for convenient haptic feedback in click handlers.
 */
fun HapticFeedbackController.click(action: () -> Unit): () -> Unit = {
    perform(HapticPatterns.CLICK)
    action()
}

/**
 * Extension function for heavy click feedback.
 */
fun HapticFeedbackController.heavyClick(action: () -> Unit): () -> Unit = {
    perform(HapticPatterns.HEAVY_CLICK)
    action()
}
