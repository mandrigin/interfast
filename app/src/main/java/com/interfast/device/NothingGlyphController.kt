package com.interfast.device

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log

/**
 * Controller for Nothing Phone Glyph LED integration.
 *
 * This module provides infrastructure for Glyph API integration.
 * Actual SDK calls require:
 * 1. NothingKey API registration from nothing.tech
 * 2. GlyphSDK.aar in libs directory
 * 3. Nothing Phone hardware
 *
 * Features when enabled:
 * - Progress display on LED ring during fasting
 * - Breathing animation for active state
 * - Celebration pattern on fast completion
 * - Subtle notification glow for milestones
 *
 * Design philosophy:
 * The Glyph integration should enhance, not distract.
 * LEDs provide ambient awareness without requiring attention.
 */
class NothingGlyphController(
    private val context: Context
) {
    companion object {
        private const val TAG = "NothingGlyph"

        // Nothing Phone package identifiers
        private const val NOTHING_LAUNCHER_PACKAGE = "com.nothing.launcher"
        private const val NOTHING_GLYPH_PACKAGE = "com.nothing.glyph"

        // Device model identifiers
        private const val NOTHING_PHONE_1 = "A063" // Phone (1)
        private const val NOTHING_PHONE_2 = "A065" // Phone (2)
        private const val NOTHING_PHONE_2A = "A142" // Phone (2a)
        private const val NOTHING_PHONE_2A_PLUS = "A143" // Phone (2a) Plus
    }

    /**
     * Check if this device is a Nothing Phone with Glyph support.
     */
    val isNothingPhone: Boolean by lazy {
        isNothingPhoneDevice() && hasGlyphCapability()
    }

    /**
     * Check if Glyph SDK is available (compiled with SDK).
     */
    val isGlyphSdkAvailable: Boolean by lazy {
        try {
            Class.forName("com.nothing.ketchum.GlyphManager")
            true
        } catch (e: ClassNotFoundException) {
            false
        }
    }

    /**
     * Check if Glyph integration is fully functional.
     */
    val isGlyphEnabled: Boolean
        get() = isNothingPhone && isGlyphSdkAvailable

    /**
     * Get the device's Glyph configuration.
     */
    val glyphConfig: GlyphConfig by lazy {
        when {
            isPhone1() -> GlyphConfig.PHONE_1
            isPhone2() -> GlyphConfig.PHONE_2
            isPhone2a() -> GlyphConfig.PHONE_2A
            else -> GlyphConfig.UNKNOWN
        }
    }

    /**
     * Display fasting progress on Glyph LEDs.
     *
     * @param progress Value from 0f to 1f
     */
    fun displayProgress(progress: Float) {
        if (!isGlyphEnabled) return

        Log.d(TAG, "Displaying progress: ${(progress * 100).toInt()}%")
        // SDK integration point:
        // val frame = GlyphFrame.Builder()
        //     .buildProgress(progress, glyphConfig.progressZone)
        // GlyphManager.getInstance().displayProgress(frame)
    }

    /**
     * Start breathing animation for active fasting state.
     */
    fun startBreathingAnimation() {
        if (!isGlyphEnabled) return

        Log.d(TAG, "Starting breathing animation")
        // SDK integration point:
        // val frame = GlyphFrame.Builder()
        //     .setChannel(glyphConfig.breathingChannel)
        //     .setCycle(0) // Infinite
        //     .setInterval(1500)
        //     .build()
        // GlyphManager.getInstance().animate(frame)
    }

    /**
     * Stop all Glyph animations.
     */
    fun stopAnimation() {
        if (!isGlyphEnabled) return

        Log.d(TAG, "Stopping animation")
        // SDK integration point:
        // GlyphManager.getInstance().turnOff()
    }

    /**
     * Play celebration pattern for fast completion.
     */
    fun playCelebration() {
        if (!isGlyphEnabled) return

        Log.d(TAG, "Playing celebration pattern")
        // SDK integration point:
        // Play a sweeping pattern across all zones
        // val frames = listOf(...)
        // GlyphManager.getInstance().toggle(frames[0])
        // ... sequence through frames with delays
    }

    /**
     * Flash notification pattern for milestones.
     */
    fun flashMilestone(milestone: Int) {
        if (!isGlyphEnabled) return

        Log.d(TAG, "Flashing milestone: $milestone%")
        // SDK integration point:
        // Quick double-flash on appropriate zone
    }

    private fun isNothingPhoneDevice(): Boolean {
        val manufacturer = Build.MANUFACTURER.lowercase()
        val brand = Build.BRAND.lowercase()
        return manufacturer == "nothing" || brand == "nothing"
    }

    private fun hasGlyphCapability(): Boolean {
        return try {
            context.packageManager.getPackageInfo(NOTHING_GLYPH_PACKAGE, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    private fun isPhone1(): Boolean {
        return Build.DEVICE.contains(NOTHING_PHONE_1, ignoreCase = true)
    }

    private fun isPhone2(): Boolean {
        return Build.DEVICE.contains(NOTHING_PHONE_2, ignoreCase = true)
    }

    private fun isPhone2a(): Boolean {
        return Build.DEVICE.contains(NOTHING_PHONE_2A, ignoreCase = true) ||
                Build.DEVICE.contains(NOTHING_PHONE_2A_PLUS, ignoreCase = true)
    }
}

/**
 * Glyph configuration for different Nothing Phone models.
 */
enum class GlyphConfig(
    val ledCount: Int,
    val progressZone: String,
    val breathingChannel: Int
) {
    PHONE_1(
        ledCount = 15,
        progressZone = "C", // C1-C4 zone for progress
        breathingChannel = 0 // Zone A
    ),
    PHONE_2(
        ledCount = 33,
        progressZone = "C", // Extended C zone with 16-LED grid
        breathingChannel = 0
    ),
    PHONE_2A(
        ledCount = 26,
        progressZone = "MATRIX", // 24-LED central grid
        breathingChannel = 1
    ),
    UNKNOWN(
        ledCount = 0,
        progressZone = "",
        breathingChannel = 0
    )
}

/**
 * Glyph state for composable integration.
 */
data class GlyphState(
    val isAvailable: Boolean,
    val isEnabled: Boolean,
    val config: GlyphConfig
)
