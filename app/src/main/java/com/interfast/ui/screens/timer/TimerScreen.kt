package com.interfast.ui.screens.timer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.interfast.domain.model.FastingProtocol
import com.interfast.domain.model.FastingStats
import com.interfast.domain.model.TimerState
import com.interfast.ui.components.AnimatedDotProgressRing
import com.interfast.ui.components.FullTimerDisplay
import com.interfast.ui.components.InterfastButton
import com.interfast.ui.components.PrimaryActionButton
import com.interfast.ui.components.StatsFooter
import com.interfast.ui.components.StatusDot
import com.interfast.ui.components.TimerControlButtons
import com.interfast.ui.components.TimerDisplay
import com.interfast.ui.theme.InterfastColors
import com.interfast.ui.theme.InterfastTheme
import com.interfast.ui.theme.InterfastTypography
import com.interfast.ui.theme.Spacing
import com.interfast.ui.util.HapticPatterns
import com.interfast.ui.util.ShakeToStart
import com.interfast.ui.util.rememberHapticFeedback
import java.time.Duration

@Composable
fun TimerScreen(
    onNavigateToProtocols: () -> Unit,
    viewModel: TimerViewModel = hiltViewModel()
) {
    val timerState by viewModel.timerState.collectAsStateWithLifecycle()
    val selectedProtocol by viewModel.selectedProtocol.collectAsStateWithLifecycle()
    val stats by viewModel.stats.collectAsStateWithLifecycle()
    val shakeToStartEnabled by viewModel.shakeToStartEnabled.collectAsStateWithLifecycle()
    val hapticFeedbackEnabled by viewModel.hapticFeedbackEnabled.collectAsStateWithLifecycle()

    val haptics = rememberHapticFeedback()

    // Shake-to-start gesture
    ShakeToStart(
        enabled = shakeToStartEnabled && timerState is TimerState.Idle,
        onShake = {
            if (hapticFeedbackEnabled) {
                haptics.perform(HapticPatterns.HEAVY_CLICK)
            }
            viewModel.startFast()
        }
    )

    TimerScreenContent(
        timerState = timerState,
        selectedProtocol = selectedProtocol,
        stats = stats,
        hapticFeedbackEnabled = hapticFeedbackEnabled,
        onStartFast = {
            if (hapticFeedbackEnabled) {
                haptics.perform(HapticPatterns.HEAVY_CLICK)
            }
            viewModel.startFast()
        },
        onEndFast = {
            if (hapticFeedbackEnabled) {
                haptics.perform(HapticPatterns.CLICK)
            }
            viewModel.endFast()
        },
        onProtocolClick = {
            if (hapticFeedbackEnabled) {
                haptics.perform(HapticPatterns.TICK)
            }
            onNavigateToProtocols()
        }
    )
}

@Composable
private fun TimerScreenContent(
    timerState: TimerState,
    selectedProtocol: FastingProtocol,
    stats: FastingStats,
    hapticFeedbackEnabled: Boolean = true,
    onStartFast: () -> Unit,
    onEndFast: () -> Unit,
    onProtocolClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(InterfastColors.VoidBlack)
    ) {
        // Main content area
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = Spacing.lg),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(Spacing.xl))

            // Protocol selector / Status header
            TimerHeader(
                timerState = timerState,
                selectedProtocol = selectedProtocol,
                onProtocolClick = onProtocolClick
            )

            Spacer(modifier = Modifier.weight(1f))

            // Timer display with progress ring
            TimerContent(
                timerState = timerState,
                selectedProtocol = selectedProtocol
            )

            Spacer(modifier = Modifier.weight(1f))

            // Action buttons
            TimerActions(
                timerState = timerState,
                onStartFast = onStartFast,
                onEndFast = onEndFast
            )

            Spacer(modifier = Modifier.height(Spacing.xl))
        }

        // Stats footer
        StatsFooter(
            streakDays = stats.currentStreak,
            weeklyPercentage = (stats.weeklyCompletionRate * 100).toInt(),
            totalHours = stats.totalHoursFasted.toHours().toInt()
        )
    }
}

@Composable
private fun TimerHeader(
    timerState: TimerState,
    selectedProtocol: FastingProtocol,
    onProtocolClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Protocol name (clickable when idle)
        Text(
            text = selectedProtocol.name,
            style = InterfastTypography.headlineMedium,
            color = InterfastColors.Gray80,
            modifier = Modifier.clickable(
                enabled = timerState is TimerState.Idle,
                onClick = onProtocolClick
            )
        )

        // Status indicator
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            StatusDot(
                color = when (timerState) {
                    is TimerState.Fasting -> InterfastColors.GlyphRed
                    is TimerState.EatingWindow -> InterfastColors.SignalCyan
                    else -> InterfastColors.Gray40
                },
                pulsing = timerState is TimerState.Fasting
            )
            Spacer(modifier = Modifier.width(Spacing.sm))
            Text(
                text = when (timerState) {
                    is TimerState.Fasting -> "FASTING"
                    is TimerState.EatingWindow -> "EATING"
                    else -> "READY"
                },
                style = InterfastTypography.labelMedium,
                color = InterfastColors.Gray60
            )
        }
    }
}

@Composable
private fun TimerContent(
    timerState: TimerState,
    selectedProtocol: FastingProtocol
) {
    when (timerState) {
        is TimerState.Idle -> IdleTimerDisplay(selectedProtocol)
        is TimerState.Fasting -> FastingTimerDisplay(timerState)
        is TimerState.EatingWindow -> EatingWindowDisplay(timerState)
        is TimerState.Paused -> {} // Not implemented yet
    }
}

@Composable
private fun IdleTimerDisplay(protocol: FastingProtocol) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AnimatedDotProgressRing(
            progress = 0f,
            modifier = Modifier.size(280.dp),
            activeColor = InterfastColors.Gray40,
            inactiveColor = InterfastColors.Gray15
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "00:00:00",
                    style = InterfastTypography.timerPrimary,
                    color = InterfastColors.Gray40
                )
                Text(
                    text = "─────",
                    style = InterfastTypography.bodySmall,
                    color = InterfastColors.Gray20
                )
                Text(
                    text = "%02d:00:00".format(protocol.fastingHours),
                    style = InterfastTypography.timerSecondary,
                    color = InterfastColors.Gray40
                )
            }
        }

        Spacer(modifier = Modifier.height(Spacing.md))

        Text(
            text = "Ready to start",
            style = InterfastTypography.bodyMedium,
            color = InterfastColors.Gray60
        )
    }
}

@Composable
private fun FastingTimerDisplay(state: TimerState.Fasting) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AnimatedDotProgressRing(
            progress = state.progress,
            modifier = Modifier.size(280.dp),
            activeColor = InterfastColors.GlyphRed,
            inactiveColor = InterfastColors.Gray15
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TimerDisplay(
                    duration = state.elapsed,
                    isActive = true,
                    textStyle = InterfastTypography.timerPrimary,
                    color = InterfastColors.PureWhite
                )
                Text(
                    text = "─────",
                    style = InterfastTypography.bodySmall,
                    color = InterfastColors.Gray20
                )
                TimerDisplay(
                    duration = state.session.targetDuration,
                    isActive = false,
                    textStyle = InterfastTypography.timerSecondary,
                    color = InterfastColors.Gray60,
                    showPulsingColon = false
                )
            }
        }

        Spacer(modifier = Modifier.height(Spacing.md))

        Text(
            text = state.percentageText,
            style = InterfastTypography.dataMedium,
            color = InterfastColors.GlyphRed
        )
    }
}

@Composable
private fun EatingWindowDisplay(state: TimerState.EatingWindow) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AnimatedDotProgressRing(
            progress = 1f,
            modifier = Modifier.size(280.dp),
            activeColor = InterfastColors.SignalCyan,
            inactiveColor = InterfastColors.Gray15
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "COMPLETE",
                    style = InterfastTypography.labelLarge,
                    color = InterfastColors.PhosphorGreen
                )
                Spacer(modifier = Modifier.height(Spacing.sm))
                TimerDisplay(
                    duration = state.eatingTimeRemaining,
                    isActive = true,
                    textStyle = InterfastTypography.timerPrimary,
                    color = InterfastColors.SignalCyan
                )
                Text(
                    text = "eating window",
                    style = InterfastTypography.bodySmall,
                    color = InterfastColors.Gray60
                )
            }
        }
    }
}

@Composable
private fun TimerActions(
    timerState: TimerState,
    onStartFast: () -> Unit,
    onEndFast: () -> Unit
) {
    AnimatedVisibility(
        visible = timerState is TimerState.Idle,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        PrimaryActionButton(
            text = "Start Fast",
            onClick = onStartFast,
            icon = Icons.Default.PlayArrow,
            modifier = Modifier.fillMaxWidth()
        )
    }

    AnimatedVisibility(
        visible = timerState is TimerState.Fasting,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        InterfastButton(
            text = "End Fast",
            onClick = onEndFast,
            icon = Icons.Default.Stop,
            modifier = Modifier.fillMaxWidth()
        )
    }

    AnimatedVisibility(
        visible = timerState is TimerState.EatingWindow,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        PrimaryActionButton(
            text = "Start Next Fast",
            onClick = onStartFast,
            icon = Icons.Default.PlayArrow,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun TimerScreenIdlePreview() {
    InterfastTheme {
        TimerScreenContent(
            timerState = TimerState.Idle(FastingProtocol.PROTOCOL_16_8),
            selectedProtocol = FastingProtocol.PROTOCOL_16_8,
            stats = FastingStats(
                currentStreak = 12,
                weeklyCompletionRate = 0.98f,
                totalHoursFasted = Duration.ofHours(1842)
            ),
            onStartFast = {},
            onEndFast = {},
            onProtocolClick = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun TimerScreenFastingPreview() {
    InterfastTheme {
        TimerScreenContent(
            timerState = TimerState.Fasting(
                session = com.interfast.domain.model.FastSession(
                    id = 1,
                    protocolId = "16_8",
                    protocolName = "16:8",
                    fastingHours = 16,
                    eatingHours = 8,
                    startedAt = java.time.Instant.now().minusSeconds(60120)
                ),
                elapsed = Duration.ofHours(16).plusMinutes(42).plusSeconds(0),
                remaining = Duration.ofHours(1).plusMinutes(18),
                progress = 0.923f
            ),
            selectedProtocol = FastingProtocol.PROTOCOL_16_8,
            stats = FastingStats(
                currentStreak = 12,
                weeklyCompletionRate = 0.98f,
                totalHoursFasted = Duration.ofHours(1842)
            ),
            onStartFast = {},
            onEndFast = {},
            onProtocolClick = {}
        )
    }
}
