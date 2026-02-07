package com.interfast.ui.screens.timer

import android.content.Context
import androidx.glance.appwidget.updateAll
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.interfast.data.preferences.UserPreferences
import com.interfast.data.repository.FastingRepository
import com.interfast.domain.model.FastSession
import com.interfast.domain.model.FastStatus
import com.interfast.domain.model.FastingProtocol
import com.interfast.domain.model.FastingStats
import com.interfast.domain.model.TimerState
import com.interfast.ui.widgets.InterfastBannerWidget
import com.interfast.ui.widgets.InterfastCompactWidget
import com.interfast.ui.widgets.InterfastDashboardWidget
import com.interfast.ui.widgets.WidgetDataProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.Instant
import javax.inject.Inject

@HiltViewModel
class TimerViewModel @Inject constructor(
    private val repository: FastingRepository,
    private val preferences: UserPreferences,
    private val widgetDataProvider: WidgetDataProvider,
    @ApplicationContext private val appContext: Context
) : ViewModel() {

    private val _timerState = MutableStateFlow<TimerState>(TimerState.Idle())
    val timerState: StateFlow<TimerState> = _timerState.asStateFlow()

    private val _selectedProtocol = MutableStateFlow(FastingProtocol.PROTOCOL_16_8)
    val selectedProtocol: StateFlow<FastingProtocol> = _selectedProtocol.asStateFlow()

    val stats: StateFlow<FastingStats> = repository.observeStats()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = FastingStats()
        )

    val shakeToStartEnabled: StateFlow<Boolean> = preferences.shakeToStartEnabled
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    val hapticFeedbackEnabled: StateFlow<Boolean> = preferences.hapticFeedbackEnabled
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = true
        )

    private var timerJob: Job? = null

    init {
        loadInitialState()
    }

    private fun loadInitialState() {
        viewModelScope.launch {
            // Load selected protocol
            val protocol = repository.getSelectedProtocol()
            _selectedProtocol.value = protocol

            // Check for active session
            val activeSession = repository.getActiveSession()
            if (activeSession != null) {
                startTimerUpdates(activeSession)
            } else {
                _timerState.value = TimerState.Idle(protocol)
            }
        }
    }

    fun startFast() {
        viewModelScope.launch {
            val protocol = _selectedProtocol.value
            val session = repository.startFast(protocol)
            startTimerUpdates(session)
            updateWidgets() // Update widgets when fast starts
        }
    }

    fun endFast() {
        viewModelScope.launch {
            val currentState = _timerState.value
            if (currentState is TimerState.Fasting) {
                val progress = currentState.progress
                val completed = progress >= 1.0f

                if (completed) {
                    repository.completeFast(currentState.session.id)
                } else {
                    repository.endFast(currentState.session.id, completed = false)
                }

                stopTimerUpdates()
                _timerState.value = TimerState.Idle(_selectedProtocol.value)
                updateWidgets() // Update widgets when fast ends
            }
        }
    }

    fun selectProtocol(protocol: FastingProtocol) {
        viewModelScope.launch {
            _selectedProtocol.value = protocol
            repository.setSelectedProtocol(protocol)

            // Update idle state if not fasting
            if (_timerState.value is TimerState.Idle) {
                _timerState.value = TimerState.Idle(protocol)
            }
        }
    }

    private var widgetUpdateCounter = 0

    private fun startTimerUpdates(session: FastSession) {
        timerJob?.cancel()
        widgetUpdateCounter = 0
        timerJob = viewModelScope.launch {
            while (true) {
                val now = Instant.now()
                val elapsed = session.elapsedDuration(now)
                val remaining = session.remainingDuration(now)
                val progress = session.calculateProgress(now)

                // Check if fast is complete
                if (progress >= 1.0f) {
                    repository.completeFast(session.id)
                    _timerState.value = TimerState.EatingWindow(
                        completedSession = session.copy(
                            status = FastStatus.COMPLETED,
                            completedAt = now,
                            completionPercentage = 1f
                        ),
                        eatingTimeRemaining = Duration.ofHours(session.eatingHours.toLong())
                    )
                    updateWidgets() // Update widgets on completion
                    // Start eating window countdown
                    startEatingWindowCountdown(session)
                    break
                }

                _timerState.value = TimerState.Fasting(
                    session = session,
                    elapsed = elapsed,
                    remaining = remaining,
                    progress = progress,
                    currentTime = now
                )

                // Update widgets every 60 seconds (not every tick for battery)
                widgetUpdateCounter++
                if (widgetUpdateCounter >= 60) {
                    widgetUpdateCounter = 0
                    updateWidgets()
                }

                delay(1000) // Update every second
            }
        }
    }

    private fun startEatingWindowCountdown(completedSession: FastSession) {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            val eatingDuration = Duration.ofHours(completedSession.eatingHours.toLong())
            val eatingStartTime = Instant.now()

            while (true) {
                val elapsed = Duration.between(eatingStartTime, Instant.now())
                val remaining = eatingDuration.minus(elapsed)

                if (remaining.isNegative || remaining.isZero) {
                    // Eating window ended, return to idle
                    _timerState.value = TimerState.Idle(_selectedProtocol.value)
                    break
                }

                _timerState.value = TimerState.EatingWindow(
                    completedSession = completedSession.copy(
                        status = FastStatus.COMPLETED,
                        completionPercentage = 1f
                    ),
                    eatingTimeRemaining = remaining
                )

                delay(1000)
            }
        }
    }

    private fun stopTimerUpdates() {
        timerJob?.cancel()
        timerJob = null
    }

    /**
     * Updates all home screen widgets with current fasting data
     */
    private fun updateWidgets() {
        viewModelScope.launch {
            try {
                widgetDataProvider.updateWidgetData(appContext)
                InterfastCompactWidget().updateAll(appContext)
                InterfastBannerWidget().updateAll(appContext)
                InterfastDashboardWidget().updateAll(appContext)
            } catch (e: Exception) {
                // Widget update failed, not critical
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopTimerUpdates()
    }
}

/**
 * UI State for Timer Screen
 */
data class TimerUiState(
    val timerState: TimerState = TimerState.Idle(),
    val selectedProtocol: FastingProtocol = FastingProtocol.PROTOCOL_16_8,
    val stats: FastingStats = FastingStats()
)
