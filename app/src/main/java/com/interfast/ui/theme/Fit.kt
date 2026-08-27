package com.interfast.ui.theme

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * The size regime for one FitTier. Every metric the deck needs in order to
 * keep its whole single-screen layout inside the available height.
 */
data class LayoutFit(
    val tier: FitTier,
    /** Gap between top-level sections of the deck column. */
    val sectionGap: Dp,
    /** Vertical padding of the deck column itself. */
    val outerPadV: Dp,
    /** Hero headline ("start a fast.") — lineHeight follows this size. */
    val heroFontSize: TextUnit,
    /** Fixed height of the tape deck. */
    val deckHeight: Dp,
    /** Tape reel diameter. */
    val reelSize: Dp,
    /** Big HH:mm readout inside the wheel area. */
    val clockFontSize: TextUnit,
    /** Vertical padding of a TARGETS hour row. */
    val rowVPad: Dp,
    /** "18H" label size in a TARGETS row. */
    val rowHourFont: TextUnit,
    /** Gap between TARGETS rows. */
    val rowGap: Dp,
    /** ACTIVATE button height. */
    val buttonHeight: Dp,
)

/**
 * Full-size regime — close to the original v1 metrics, trimmed just enough
 * that a tall-but-not-giant phone keeps all twelve deck elements on screen
 * even with both permission hint lines visible.
 */
private val RegularFit = LayoutFit(
    tier = FitTier.REGULAR,
    sectionGap = 13.dp,
    outerPadV = 12.dp,
    heroFontSize = 54.sp,
    deckHeight = 168.dp,
    reelSize = 38.dp,
    clockFontSize = 34.sp,
    rowVPad = 10.dp,
    rowHourFont = 21.sp,
    rowGap = 6.dp,
    buttonHeight = 64.dp,
)

/** Mid-ranger regime (Fairphone-class, ~740–780 usable dp). */
private val CompactFit = LayoutFit(
    tier = FitTier.COMPACT,
    sectionGap = 11.dp,
    outerPadV = 10.dp,
    heroFontSize = 42.sp,
    deckHeight = 146.dp,
    reelSize = 32.dp,
    clockFontSize = 28.sp,
    rowVPad = 8.dp,
    rowHourFont = 18.sp,
    rowGap = 5.dp,
    buttonHeight = 58.dp,
)

/** Small-device regime; scroll remains as the fallback for huge font scales. */
private val TightFit = LayoutFit(
    tier = FitTier.TIGHT,
    sectionGap = 9.dp,
    outerPadV = 8.dp,
    heroFontSize = 34.sp,
    deckHeight = 124.dp,
    reelSize = 26.dp,
    clockFontSize = 23.sp,
    rowVPad = 6.dp,
    rowHourFont = 16.sp,
    rowGap = 4.dp,
    buttonHeight = 52.dp,
)

/** Compose-free so JVM unit tests can assert cross-tier contracts. */
fun layoutFitFor(tier: FitTier): LayoutFit = when (tier) {
    FitTier.REGULAR -> RegularFit
    FitTier.COMPACT -> CompactFit
    FitTier.TIGHT -> TightFit
}
