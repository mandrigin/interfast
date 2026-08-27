package com.interfast.ui.theme

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * The theme decision for the ambient-brightness skin. The interesting cases
 * are modern phones with adaptive brightness, where the classic settings
 * int is stale and the light sensor is the only live signal.
 */
class DecideDarkTest {

    @Test
    fun `dim backlight always wins`() {
        assertTrue(decideDark(11, lux = 5_000f, currentlyDark = false))
        assertTrue(decideDark(99, lux = null, currentlyDark = false))
    }

    @Test
    fun `bright backlight and lit room is light`() {
        assertFalse(decideDark(200, lux = 500f, currentlyDark = true))
        assertFalse(decideDark(255, lux = 1_000f, currentlyDark = true))
    }

    @Test
    fun `bright settings value but dim room means adaptive held the panel down`() {
        // The Fairphone 6 case: settings says 212, real backlight ~4%.
        assertTrue(decideDark(212, lux = 8f, currentlyDark = false))
    }

    @Test
    fun `no lux signal keeps current state in the hysteresis band`() {
        assertTrue(decideDark(128, lux = null, currentlyDark = true))
        assertFalse(decideDark(128, lux = null, currentlyDark = false))
    }

    @Test
    fun `mid lux in the bright band also keeps current state`() {
        assertTrue(decideDark(212, lux = 120f, currentlyDark = true))
        assertFalse(decideDark(212, lux = 120f, currentlyDark = false))
    }

    @Test
    fun `thresholds are exact boundaries`() {
        // Strictly below 100 → dark; strictly above 156 with bright lux → light.
        assertTrue(decideDark(99, lux = 500f, currentlyDark = false))
        assertFalse(decideDark(100, lux = 500f, currentlyDark = false))
        // Exactly DARK_LUX is not "dim room"; exactly LIGHT_LUX is not "lit".
        assertFalse(decideDark(212, lux = DARK_LUX, currentlyDark = false))
        assertFalse(decideDark(212, lux = LIGHT_LUX, currentlyDark = false))
        assertFalse(decideDark(157, lux = 251f, currentlyDark = false))
    }
}
