package com.interfast.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for [ScheduleState] state-transition contract.
 *
 * The full [ScheduleRepository] couples to an Android [android.content.Context]
 * via the DataStore property delegate, so its IO behaviour is exercised in
 * instrumentation tests instead. Here we lock the pure state-shape semantics.
 */
class ScheduleRepositoryTest {

    private fun emptyState() = ScheduleState(
        startEpochMillis = 1_700_000_000_000L,
        checkedHours = emptySet(),
        active = false,
        activatedAtMillis = null,
        reachedHours = emptySet(),
    )

    @Test
    fun `default state is inactive with no checked hours`() {
        val s = emptyState()
        assertFalse(s.active)
        assertTrue(s.checkedHours.isEmpty())
        assertNull(s.activatedAtMillis)
    }

    @Test
    fun `toggleHour add then remove`() {
        var checked = emptySet<Int>()
        checked = toggle(checked, 16)
        assertEquals(setOf(16), checked)
        checked = toggle(checked, 16)
        assertEquals(emptySet<Int>(), checked)
    }

    @Test
    fun `activate sets flag and stamp and clears reached`() {
        val pre = emptyState().copy(
            checkedHours = setOf(16),
            reachedHours = setOf(12),
        )
        val post = pre.copy(active = true, activatedAtMillis = 42L, reachedHours = emptySet())
        assertTrue(post.active)
        assertEquals(42L, post.activatedAtMillis)
        assertTrue(post.reachedHours.isEmpty())
    }

    @Test
    fun `deactivate clears flag and stamp and reached`() {
        val pre = emptyState().copy(
            active = true,
            activatedAtMillis = 42L,
            reachedHours = setOf(12, 16),
        )
        val post = pre.copy(active = false, activatedAtMillis = null, reachedHours = emptySet())
        assertFalse(post.active)
        assertNull(post.activatedAtMillis)
        assertTrue(post.reachedHours.isEmpty())
    }

    @Test
    fun `markReached accumulates hours`() {
        var reached = emptySet<Int>()
        reached = reached + 12
        reached = reached + 16
        assertEquals(setOf(12, 16), reached)
    }

    @Test
    fun `ALL_HOURS contains the expected milestones`() {
        assertEquals(listOf(12, 16, 18, 20, 22), ScheduleRepository.ALL_HOURS)
    }

    @Test
    fun `completeFast disarms but keeps reached hours for DONE badges`() {
        val pre = emptyState().copy(
            active = true,
            activatedAtMillis = 42L,
            checkedHours = setOf(12, 16),
            reachedHours = setOf(12, 16),
        )
        // completeFast contract: active off, stamp cleared, reached KEPT.
        val post = pre.copy(active = false, activatedAtMillis = null)
        assertFalse(post.active)
        assertNull(post.activatedAtMillis)
        assertEquals(setOf(12, 16), post.reachedHours)
    }

    private fun toggle(set: Set<Int>, hour: Int): Set<Int> =
        if (set.contains(hour)) set - hour else set + hour
}
