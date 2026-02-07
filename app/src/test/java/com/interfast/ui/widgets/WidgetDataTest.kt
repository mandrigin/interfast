package com.interfast.ui.widgets

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class WidgetDataTest {

    @Test
    fun `default WidgetData has correct initial values`() {
        val data = WidgetData()

        assertFalse(data.isFasting)
        assertEquals("00:00:00", data.elapsed)
        assertEquals("00:00", data.elapsedShort)
        assertEquals("00:00:00", data.target)
        assertEquals(0f, data.progress, 0.01f)
        assertEquals(0, data.streak)
        assertEquals(0, data.weeklyPercent)
    }

    @Test
    fun `WidgetData correctly stores fasting state`() {
        val data = WidgetData(
            isFasting = true,
            elapsed = "16:30:45",
            elapsedShort = "16:30",
            target = "18:00:00",
            progress = 0.92f,
            streak = 12,
            weeklyPercent = 98
        )

        assertTrue(data.isFasting)
        assertEquals("16:30:45", data.elapsed)
        assertEquals("16:30", data.elapsedShort)
        assertEquals("18:00:00", data.target)
        assertEquals(0.92f, data.progress, 0.01f)
        assertEquals(12, data.streak)
        assertEquals(98, data.weeklyPercent)
    }

    @Test
    fun `WidgetData progress is bounded correctly`() {
        val dataZero = WidgetData(progress = 0f)
        val dataFull = WidgetData(progress = 1f)
        val dataMid = WidgetData(progress = 0.5f)

        assertEquals(0f, dataZero.progress, 0.01f)
        assertEquals(1f, dataFull.progress, 0.01f)
        assertEquals(0.5f, dataMid.progress, 0.01f)
    }

    @Test
    fun `WidgetData handles non-fasting state`() {
        val data = WidgetData(
            isFasting = false,
            elapsed = "00:00:00",
            target = "00:00:00",
            progress = 0f,
            streak = 5
        )

        assertFalse(data.isFasting)
        assertEquals(5, data.streak)
    }
}
