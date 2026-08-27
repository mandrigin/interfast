package com.interfast.ui.theme

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

class FitTierTest {

    // ── tier boundaries ──

    @Test
    fun `below tight threshold is TIGHT`() {
        assertEquals(FitTier.TIGHT, fitTierFor(0))
        assertEquals(FitTier.TIGHT, fitTierFor(320))
        assertEquals(FitTier.TIGHT, fitTierFor(639))
    }

    @Test
    fun `exact tight threshold starts COMPACT`() {
        assertEquals(FitTier.COMPACT, fitTierFor(FIT_TIGHT_MAX))
        assertEquals(FitTier.COMPACT, fitTierFor(FIT_TIGHT_MAX + 1))
    }

    @Test
    fun `fairphone 6 class usable height is COMPACT`() {
        // FP6: 1116x2484 @480dpi -> 372x828 dp window; after system bars a
        // bit over 750 usable dp. The deck must not need the REGULAR budget.
        val fp6Usable = 828 - 36 - 24
        assertEquals(FitTier.COMPACT, fitTierFor(fp6Usable))
    }

    @Test
    fun `tall flagship stays REGULAR`() {
        assertEquals(FitTier.REGULAR, fitTierFor(FIT_COMPACT_MAX))
        assertEquals(FitTier.REGULAR, fitTierFor(1200))
    }

    @Test
    fun `below compact threshold never returns REGULAR`() {
        assertEquals(FitTier.COMPACT, fitTierFor(FIT_COMPACT_MAX - 1))
    }

    // ── cross-tier contracts ──

    @Test
    fun `smaller tiers are uniformly smaller or equal`() {
        val order = listOf(FitTier.REGULAR, FitTier.COMPACT, FitTier.TIGHT)
        val regular = layoutFitFor(FitTier.REGULAR)
        val compact = layoutFitFor(FitTier.COMPACT)
        val tight = layoutFitFor(FitTier.TIGHT)

        assertTrue(regular.sectionGap.value >= compact.sectionGap.value)
        assertTrue(compact.sectionGap.value >= tight.sectionGap.value)

        assertTrue(regular.deckHeight.value >= compact.deckHeight.value)
        assertTrue(compact.deckHeight.value >= tight.deckHeight.value)

        assertTrue(regular.heroFontSize.value >= compact.heroFontSize.value)
        assertTrue(compact.heroFontSize.value >= tight.heroFontSize.value)

        assertTrue(regular.rowVPad.value >= compact.rowVPad.value)
        assertTrue(compact.rowVPad.value >= tight.rowVPad.value)

        assertTrue(regular.buttonHeight.value >= compact.buttonHeight.value)
        assertTrue(compact.buttonHeight.value >= tight.buttonHeight.value)

        assertTrue(regular.clockFontSize.value >= compact.clockFontSize.value)
        assertTrue(compact.clockFontSize.value >= tight.clockFontSize.value)

        assertTrue(regular.reelSize.value >= compact.reelSize.value)
        assertTrue(compact.reelSize.value >= tight.reelSize.value)

        assertTrue(regular.rowHourFont.value >= compact.rowHourFont.value)
        assertTrue(compact.rowHourFont.value >= tight.rowHourFont.value)

        assertTrue(regular.rowGap.value >= compact.rowGap.value)
        assertTrue(compact.rowGap.value >= tight.rowGap.value)
    }

    @Test
    fun `button stays above minimum touch target in every tier`() {
        for (tier in FitTier.values()) {
            assertTrue(
                "buttonHeight ${layoutFitFor(tier).buttonHeight} below 48dp touch target",
                layoutFitFor(tier).buttonHeight.value >= 48f,
            )
        }
    }

    @Test
    fun `deck keeps working wheel area in every tier`() {
        for (tier in FitTier.values()) {
            assertTrue(layoutFitFor(tier).deckHeight.value >= 110f)
        }
    }
}

@RunWith(Parameterized::class)
class FitTierBoundaryTest(private val inputDp: Int, private val expected: FitTier) {
    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}dp -> {1}")
        fun data(): Collection<Array<Any>> = listOf(
            arrayOf(639, FitTier.TIGHT),
            arrayOf(FIT_TIGHT_MAX, FitTier.COMPACT),
            arrayOf(779, FitTier.COMPACT),
            arrayOf(FIT_COMPACT_MAX, FitTier.REGULAR),
        )
    }

    @Test
    fun mapsToExpectedTier() {
        assertEquals(expected, fitTierFor(inputDp))
    }
}
