package com.interfast.ui.screens.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.interfast.data.repository.FastingRepository
import com.interfast.domain.model.FastStatus
import com.interfast.domain.model.FastingStats
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import javax.inject.Inject

@HiltViewModel
class StatsViewModel @Inject constructor(
    private val repository: FastingRepository
) : ViewModel() {

    val stats: StateFlow<FastingStats> = repository.observeStats()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = FastingStats()
        )

    val weeklyData: StateFlow<List<Float>> = repository.observeAllSessions()
        .map { sessions ->
            val today = LocalDate.now()
            val zone = ZoneId.systemDefault()

            // Get data for the last 7 days (Mon-Sun)
            val startOfWeek = today.minusDays(today.dayOfWeek.value.toLong() - 1)

            (0..6).map { dayOffset ->
                val date = startOfWeek.plusDays(dayOffset.toLong())
                val startOfDay = date.atStartOfDay(zone).toInstant()
                val endOfDay = date.plusDays(1).atStartOfDay(zone).toInstant()

                sessions
                    .filter { session ->
                        val sessionStart = session.startedAt
                        sessionStart.isAfter(startOfDay) && sessionStart.isBefore(endOfDay)
                    }
                    .filter { it.status == FastStatus.COMPLETED }
                    .sumOf { it.actualDuration.toHours().toInt() }
                    .toFloat()
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = List(7) { 0f }
        )
}
