package com.interfast.ui.theme

/**
 * Vertical space classes for the single-screen deck.
 *
 * The instrument metaphor works only when everything fits on one screen — a
 * deck you have to scroll is a form, not an instrument. These tiers select
 * one of three size regimes ([layoutFitFor]) based on how much vertical
 * space the window actually offers after system bars, so the same single
 * screen stays whole on tall flagships, mid-rangers like the Fairphone 6,
 * and small devices alike. Horizontal space is left alone: the column is
 * naturally scroll-resistant sideways-down to ~320dp widths.
 */
enum class FitTier {
    /** Usable height below [FIT_TIGHT_MAX] dp. */
    TIGHT,

    /** Usable height up to [FIT_COMPACT_MAX] dp. */
    COMPACT,

    /** Generous vertical space — the full-size regime. */
    REGULAR,
}

/** Below this many usable dp, only the tight regime fits comfortably. */
const val FIT_TIGHT_MAX = 640

/** Below this many usable dp, the compact regime applies. */
const val FIT_COMPACT_MAX = 780

/**
 * Pure, Compose-free tier selection so release builds of logic stay unit
 * testable. [availableHeightDp] is the window height minus system bars.
 */
fun fitTierFor(availableHeightDp: Int): FitTier = when {
    availableHeightDp < FIT_TIGHT_MAX -> FitTier.TIGHT
    availableHeightDp < FIT_COMPACT_MAX -> FitTier.COMPACT
    else -> FitTier.REGULAR
}
