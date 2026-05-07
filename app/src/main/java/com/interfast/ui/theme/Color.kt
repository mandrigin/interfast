package com.interfast.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Interfast Color System
 *
 * Design Direction: "Glyph Matrix"
 * - Nothing Phone aesthetics (dot matrix, glyph interface)
 * - Swiss design (clean, stark)
 * - Bauhaus geometry
 * - Cypherpunk data-forward aesthetic
 */
object InterfastColors {
    // Primary Palette (5 colors max)
    val GlyphRed = Color(0xFFFF3B30)      // Active fasting state, primary CTAs
    val SignalCyan = Color(0xFF00D4FF)    // Eating window, progress complete
    val PhosphorGreen = Color(0xFF39FF14) // Success, streaks, positive feedback
    val AmberWarning = Color(0xFFFFB800)  // Milestones, warnings
    val VoidBlack = Color(0xFF0A0A0A)     // Primary background

    // Neutral Palette
    val PureWhite = Color(0xFFFFFFFF)     // Primary text
    val Gray80 = Color(0xFFCCCCCC)        // Secondary text
    val Gray60 = Color(0xFF999999)        // Tertiary text
    val Gray40 = Color(0xFF666666)        // Disabled states
    val Gray20 = Color(0xFF333333)        // Subtle borders
    val Gray15 = Color(0xFF262626)        // Elevated surfaces
    val Gray10 = Color(0xFF1A1A1A)        // Cards, containers
    val Gray05 = Color(0xFF0D0D0D)        // Subtle elevation

    // Light-mode neutrals
    val Gray95 = Color(0xFFF2F2F2)        // Near-white surface for light theme

    // Semantic Colors
    val FastingActive = GlyphRed
    val EatingActive = SignalCyan
    val Success = PhosphorGreen
    val Warning = AmberWarning
    val Error = GlyphRed

    // State Colors for Progress
    val ProgressInactive = Gray15
    val ProgressFasting = GlyphRed
    val ProgressEating = SignalCyan
    val ProgressComplete = PhosphorGreen
}

// Extension for alpha variants
fun Color.alpha(alpha: Float): Color = this.copy(alpha = alpha)
