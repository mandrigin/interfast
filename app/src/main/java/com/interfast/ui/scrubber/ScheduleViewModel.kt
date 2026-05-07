package com.interfast.ui.scrubber

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.interfast.InterfastApplication
import com.interfast.data.ScheduleRepository
import com.interfast.data.ScheduleState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel for the single ScrubberScreen.
 *
 * Reads/writes via [ScheduleRepository] and triggers/cancels alarms via the
 * AlarmScheduler stashed on the [InterfastApplication].
 */
class ScheduleViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as InterfastApplication
    private val repo = app.scheduleRepository
    private val scheduler = app.alarmScheduler

    val state: StateFlow<ScheduleState> = repo.state.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = ScheduleState(
            startEpochMillis = (System.currentTimeMillis() / 60_000L) * 60_000L,
            checkedHours = emptySet(),
            active = false,
            activatedAtMillis = null,
            reachedHours = emptySet(),
        )
    )

    /** Wall-clock tick that updates every second so PAST/DONE labels refresh. */
    val now: StateFlow<Long> = flow {
        while (true) {
            emit(System.currentTimeMillis())
            delay(1_000L)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = System.currentTimeMillis()
    )

    fun setStartTime(millis: Long) {
        viewModelScope.launch(Dispatchers.IO) { repo.setStartTime(millis) }
    }

    fun toggleHour(hour: Int) {
        viewModelScope.launch(Dispatchers.IO) { repo.toggleHour(hour) }
    }

    fun activate() {
        viewModelScope.launch(Dispatchers.IO) {
            val snap = repo.snapshot()
            val activatedAt = snap.startEpochMillis
            val now = System.currentTimeMillis()
            snap.checkedHours.forEach { hour ->
                val target = activatedAt + hour * 3_600_000L
                if (target > now) {
                    scheduler.scheduleHour(hour, target)
                }
            }
            repo.activate(activatedAt)
        }
    }

    fun deactivate() {
        viewModelScope.launch(Dispatchers.IO) {
            scheduler.cancelAll()
            repo.deactivate()
        }
    }

    fun canScheduleExact(): Boolean = scheduler.canScheduleExact()
}
