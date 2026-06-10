package com.interfast.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

/**
 * State persisted by [ScheduleRepository].
 *
 * Note: [startEpochMillis] is the user's chosen scrubber position (when the
 * window started). [activatedAtMillis] is the snapshot of [startEpochMillis]
 * at the moment ACTIVATE was tapped, so we can compute target trigger times
 * even if the user later scrubs around while inactive.
 */
data class ScheduleState(
    val startEpochMillis: Long,
    val checkedHours: Set<Int>,
    val active: Boolean,
    val activatedAtMillis: Long?,
    val reachedHours: Set<Int>,
    val scrubHintDismissed: Boolean = false,
)

private val Context.scheduleDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "interfast_schedule"
)

/**
 * Persistence layer for the single-screen scheduler.
 *
 * Backed by Preferences DataStore. All writes are coroutine-suspending; reads
 * are exposed as a [Flow] of [ScheduleState] for Compose collection.
 */
class ScheduleRepository(private val context: Context) {

    private val dataStore: DataStore<Preferences> = context.scheduleDataStore

    val state: Flow<ScheduleState> = dataStore.data.map { prefs ->
        prefs.toScheduleState()
    }

    suspend fun snapshot(): ScheduleState = state.first()

    suspend fun setStartTime(millis: Long) {
        // Snap to whole-minute. Display is HH:mm, notifications trigger at the
        // precise millis — without truncation, residual seconds push the
        // actual delivery into the next minute, breaking the row's promised
        // time.
        dataStore.edit { it[KEY_START] = (millis / 60_000L) * 60_000L }
    }

    suspend fun toggleHour(hour: Int) {
        dataStore.edit { prefs ->
            val current = prefs[KEY_CHECKED_HOURS]?.toMutableSet() ?: mutableSetOf()
            val asString = hour.toString()
            if (current.contains(asString)) current.remove(asString) else current.add(asString)
            prefs[KEY_CHECKED_HOURS] = current
        }
    }

    /** Replaces the checked set wholesale — used when activation prunes past hours. */
    suspend fun setCheckedHours(hours: Set<Int>) {
        dataStore.edit { prefs ->
            prefs[KEY_CHECKED_HOURS] = hours.map { it.toString() }.toSet()
        }
    }

    suspend fun dismissScrubHint() {
        dataStore.edit { it[KEY_SCRUB_HINT_DISMISSED] = true }
    }

    suspend fun activate(activatedAt: Long) {
        dataStore.edit { prefs ->
            prefs[KEY_ACTIVE] = true
            prefs[KEY_ACTIVATED_AT] = activatedAt
            prefs[KEY_REACHED_HOURS] = emptySet()
        }
    }

    suspend fun deactivate() {
        dataStore.edit { prefs ->
            prefs[KEY_ACTIVE] = false
            prefs.remove(KEY_ACTIVATED_AT)
            prefs[KEY_REACHED_HOURS] = emptySet()
        }
    }

    /**
     * Disarms after the final milestone fires, keeping reachedHours so DONE
     * badges survive until the next activation. This is what makes
     * "set and forget" true — the tape rewinds itself.
     */
    suspend fun completeFast() {
        dataStore.edit { prefs ->
            prefs[KEY_ACTIVE] = false
            prefs.remove(KEY_ACTIVATED_AT)
        }
    }

    suspend fun markReached(hour: Int) {
        dataStore.edit { prefs ->
            val current = prefs[KEY_REACHED_HOURS]?.toMutableSet() ?: mutableSetOf()
            current.add(hour.toString())
            prefs[KEY_REACHED_HOURS] = current
        }
    }

    private fun Preferences.toScheduleState(): ScheduleState {
        val start = this[KEY_START] ?: ((System.currentTimeMillis() / 60_000L) * 60_000L)
        val checked = this[KEY_CHECKED_HOURS]
            ?.mapNotNull { it.toIntOrNull() }
            ?.toSet()
            ?: emptySet()
        val active = this[KEY_ACTIVE] ?: false
        val activatedAt = this[KEY_ACTIVATED_AT]
        val reached = this[KEY_REACHED_HOURS]
            ?.mapNotNull { it.toIntOrNull() }
            ?.toSet()
            ?: emptySet()
        return ScheduleState(
            startEpochMillis = start,
            checkedHours = checked,
            active = active,
            activatedAtMillis = activatedAt,
            reachedHours = reached,
            scrubHintDismissed = this[KEY_SCRUB_HINT_DISMISSED] ?: false,
        )
    }

    companion object {
        val ALL_HOURS: List<Int> = listOf(12, 16, 18, 20, 22)

        private val KEY_START = longPreferencesKey("start_epoch_millis")
        private val KEY_CHECKED_HOURS = stringSetPreferencesKey("checked_hours")
        private val KEY_ACTIVE = booleanPreferencesKey("active")
        private val KEY_ACTIVATED_AT = longPreferencesKey("activated_at_millis")
        private val KEY_REACHED_HOURS = stringSetPreferencesKey("reached_hours")
        private val KEY_SCRUB_HINT_DISMISSED = booleanPreferencesKey("scrub_hint_dismissed")
    }
}
