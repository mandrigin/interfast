package com.interfast.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Duration

class TimerStateTest {

    @Test
    fun `formatDuration formats hours minutes seconds correctly`() {
        val duration = Duration.ofHours(16).plusMinutes(42).plusSeconds(8)

        val formatted = TimerState.formatDuration(duration)

        assertEquals("16:42:08", formatted)
    }

    @Test
    fun `formatDuration pads single digits with zeros`() {
        val duration = Duration.ofHours(1).plusMinutes(5).plusSeconds(3)

        val formatted = TimerState.formatDuration(duration)

        assertEquals("01:05:03", formatted)
    }

    @Test
    fun `formatDuration handles zero duration`() {
        val duration = Duration.ZERO

        val formatted = TimerState.formatDuration(duration)

        assertEquals("00:00:00", formatted)
    }

    @Test
    fun `formatDurationShort formats hours and minutes only`() {
        val duration = Duration.ofHours(18).plusMinutes(30)

        val formatted = TimerState.formatDurationShort(duration)

        assertEquals("18:30", formatted)
    }

    @Test
    fun `Fasting state formattedElapsed returns correct string`() {
        val session = createTestSession()
        val state = TimerState.Fasting(
            session = session,
            elapsed = Duration.ofHours(10).plusMinutes(15).plusSeconds(30),
            remaining = Duration.ofHours(5).plusMinutes(44).plusSeconds(30),
            progress = 0.65f
        )

        assertEquals("10:15:30", state.formattedElapsed)
    }

    @Test
    fun `Fasting state formattedRemaining returns correct string`() {
        val session = createTestSession()
        val state = TimerState.Fasting(
            session = session,
            elapsed = Duration.ofHours(10),
            remaining = Duration.ofHours(6).plusMinutes(0).plusSeconds(0),
            progress = 0.625f
        )

        assertEquals("06:00:00", state.formattedRemaining)
    }

    @Test
    fun `Fasting state percentageText formats correctly`() {
        val session = createTestSession()
        val state = TimerState.Fasting(
            session = session,
            elapsed = Duration.ZERO,
            remaining = Duration.ZERO,
            progress = 0.923f
        )

        assertEquals("92.3%", state.percentageText)
    }

    @Test
    fun `Fasting state formattedEndTime returns clock time`() {
        val startTime = java.time.Instant.parse("2026-02-07T18:00:00Z")
        val session = FastSession(
            id = 1,
            protocolId = "16_8",
            protocolName = "16:8",
            fastingHours = 16,
            eatingHours = 8,
            startedAt = startTime
        )
        val state = TimerState.Fasting(
            session = session,
            elapsed = java.time.Duration.ofHours(4),
            remaining = java.time.Duration.ofHours(12),
            progress = 0.25f
        )

        // formattedEndTime should be non-empty and contain AM or PM
        val endTime = state.formattedEndTime
        assert(endTime.contains("AM") || endTime.contains("PM")) {
            "Expected end time to contain AM/PM but was: $endTime"
        }
    }

    @Test
    fun `Idle state contains selected protocol`() {
        val protocol = FastingProtocol.PROTOCOL_18_6
        val state = TimerState.Idle(protocol)

        assertEquals(protocol, state.selectedProtocol)
    }

    @Test
    fun `EatingWindow state formattedRemaining returns correct string`() {
        val session = createTestSession()
        val state = TimerState.EatingWindow(
            completedSession = session,
            eatingTimeRemaining = Duration.ofHours(7).plusMinutes(30).plusSeconds(0)
        )

        assertEquals("07:30:00", state.formattedRemaining)
    }

    private fun createTestSession(): FastSession {
        return FastSession(
            id = 1,
            protocolId = "16_8",
            protocolName = "16:8",
            fastingHours = 16,
            eatingHours = 8,
            startedAt = java.time.Instant.now()
        )
    }
}
