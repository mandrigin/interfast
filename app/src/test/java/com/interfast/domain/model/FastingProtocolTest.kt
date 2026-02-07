package com.interfast.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FastingProtocolTest {

    @Test
    fun `default protocols have correct fasting hours`() {
        assertEquals(16, FastingProtocol.PROTOCOL_16_8.fastingHours)
        assertEquals(18, FastingProtocol.PROTOCOL_18_6.fastingHours)
        assertEquals(20, FastingProtocol.PROTOCOL_20_4.fastingHours)
        assertEquals(23, FastingProtocol.PROTOCOL_23_1.fastingHours)
    }

    @Test
    fun `default protocols have correct eating hours`() {
        assertEquals(8, FastingProtocol.PROTOCOL_16_8.eatingHours)
        assertEquals(6, FastingProtocol.PROTOCOL_18_6.eatingHours)
        assertEquals(4, FastingProtocol.PROTOCOL_20_4.eatingHours)
        assertEquals(1, FastingProtocol.PROTOCOL_23_1.eatingHours)
    }

    @Test
    fun `totalHours equals 24 for default protocols`() {
        FastingProtocol.DEFAULT_PROTOCOLS.forEach { protocol ->
            assertEquals(24, protocol.totalHours)
        }
    }

    @Test
    fun `default protocols are not custom`() {
        FastingProtocol.DEFAULT_PROTOCOLS.forEach { protocol ->
            assertFalse(protocol.isCustom)
        }
    }

    @Test
    fun `custom protocol is marked as custom`() {
        val custom = FastingProtocol.custom(14, 10)

        assertTrue(custom.isCustom)
    }

    @Test
    fun `custom protocol has correct hours`() {
        val custom = FastingProtocol.custom(20, 4)

        assertEquals(20, custom.fastingHours)
        assertEquals(4, custom.eatingHours)
    }

    @Test
    fun `custom protocol id is generated correctly`() {
        val custom = FastingProtocol.custom(14, 10)

        assertEquals("custom_14_10", custom.id)
    }

    @Test
    fun `custom protocol name is formatted correctly`() {
        val custom = FastingProtocol.custom(14, 10)

        assertEquals("14:10", custom.name)
    }

    @Test
    fun `DEFAULT_PROTOCOLS contains all four standard protocols`() {
        assertEquals(4, FastingProtocol.DEFAULT_PROTOCOLS.size)

        val ids = FastingProtocol.DEFAULT_PROTOCOLS.map { it.id }
        assertTrue(ids.contains("16_8"))
        assertTrue(ids.contains("18_6"))
        assertTrue(ids.contains("20_4"))
        assertTrue(ids.contains("23_1"))
    }
}
