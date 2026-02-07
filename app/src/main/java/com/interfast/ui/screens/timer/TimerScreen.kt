package com.interfast.ui.screens.timer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Spring
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
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.interfast.domain.model.FastingProtocol
import com.interfast.domain.model.FastingStats
import com.interfast.domain.model.TimerState
import com.interfast.ui.components.AnimatedDotProgressRing
import com.interfast.ui.components.AnimatedTimerText
import com.interfast.ui.components.BouncyBox
import com.interfast.ui.components.BreathingDot
import com.interfast.ui.components.CelebrationBurst
import com.interfast.ui.components.FullTimerDisplay
import com.interfast.ui.components.InterfastButton
import com.interfast.ui.components.PrimaryActionButton
import com.interfast.ui.components.PulsingGlow
import com.interfast.ui.components.StatsFooter
import com.interfast.ui.components.StatusDot
import com.interfast.ui.components.TimerControlButtons
import com.interfast.ui.components.TimerDisplay
import com.interfast.ui.components.WigglyText
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
    val stateColor = when (timerState) {
        is TimerState.Fasting -> InterfastColors.GlyphRed
        is TimerState.EatingWindow -> InterfastColors.PhosphorGreen
        else -> InterfastColors.Gray60
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Bold protocol display
        Column(
            modifier = Modifier.clickable(
                enabled = timerState is TimerState.Idle,
                onClick = onProtocolClick
            )
        ) {
            Text(
                text = selectedProtocol.name,
                style = InterfastTypography.headlineLarge,
                color = InterfastColors.PureWhite
            )
            Text(
                text = if (timerState is TimerState.Idle) "TAP TO CHANGE" else "${selectedProtocol.fastingHours}:${selectedProtocol.eatingHours} PROTOCOL",
                style = InterfastTypography.labelSmall,
                color = InterfastColors.Gray40
            )
        }

        // Status badge - bolder
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            StatusDot(
                color = stateColor,
                pulsing = timerState is TimerState.Fasting,
                size = 12.dp  // Bigger dot
            )
            Spacer(modifier = Modifier.width(Spacing.sm))
            Text(
                text = when (timerState) {
                    is TimerState.Fasting -> "BURNING"
                    is TimerState.EatingWindow -> "REFUELING"
                    else -> "STANDBY"
                },
                style = InterfastTypography.labelLarge,
                color = stateColor
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
    // Tappable for fidget satisfaction
    var tapCount by remember { mutableStateOf(0) }
    val easterEggMessage = when {
        tapCount >= 10 -> "OK OK, start already! 🔥"
        tapCount >= 5 -> "Eager, aren't we?"
        else -> null
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Subtle pulsing glow behind the ring
        Box(contentAlignment = Alignment.Center) {
            PulsingGlow(
                color = InterfastColors.GlyphRed,
                size = 340.dp,
                pulseSpeed = 3000
            )

            BouncyBox(
                onTap = { tapCount++ },
                onDoubleTap = { tapCount += 3 }
            ) {
                AnimatedDotProgressRing(
                    progress = 0f,
                    modifier = Modifier.size(320.dp),
                    activeColor = InterfastColors.Gray40,
                    inactiveColor = InterfastColors.Gray15
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Animated timer ready state
                        AnimatedTimerText(
                            hours = 0,
                            minutes = 0,
                            textStyle = InterfastTypography.displayLarge,
                            color = InterfastColors.Gray40,
                            colonColor = InterfastColors.Gray20
                        )
                        Spacer(modifier = Modifier.height(Spacing.xs))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            BreathingDot(
                                color = InterfastColors.GlyphRed,
                                size = 8.dp,
                                breathingSpeed = 1500
                            )
                            Spacer(modifier = Modifier.width(Spacing.xs))
                            Text(
                                text = "${protocol.fastingHours}H TARGET",
                                style = InterfastTypography.labelMedium,
                                color = InterfastColors.Gray40
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(Spacing.lg))

        // Easter egg or provocative copy
        if (easterEggMessage != null) {
            WigglyText(
                text = easterEggMessage,
                textStyle = InterfastTypography.headlineMedium,
                color = InterfastColors.AmberWarning,
                wiggleAmount = 2f
            )
        } else {
            Text(
                text = "HUNGER IS A LIE",
                style = InterfastTypography.headlineMedium,
                color = InterfastColors.GlyphRed,
                textAlign = TextAlign.Center
            )
        }
        Spacer(modifier = Modifier.height(Spacing.xs))
        Text(
            text = "Your body is stronger than your cravings.",
            style = InterfastTypography.bodySmall,
            color = InterfastColors.Gray60,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun FastingTimerDisplay(state: TimerState.Fasting) {
    val motivationalMessage = when {
        state.progress < 0.25f -> "STAY HARD"
        state.progress < 0.50f -> "HALFWAY TO GREATNESS"
        state.progress < 0.75f -> "THE WEAK QUIT HERE"
        state.progress < 0.90f -> "ALMOST THERE"
        else -> "FINISH STRONG 🔥"
    }

    // Track milestone celebrations
    val showCelebration = remember(state.progress) {
        state.progress >= 0.25f && state.progress < 0.26f ||
        state.progress >= 0.50f && state.progress < 0.51f ||
        state.progress >= 0.75f && state.progress < 0.76f ||
        state.progress >= 0.99f
    }

    // Parse elapsed time for animated display
    val totalSeconds = state.elapsed.toMillis() / 1000
    val hours = (totalSeconds / 3600).toInt()
    val minutes = ((totalSeconds % 3600) / 60).toInt()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(contentAlignment = Alignment.Center) {
            // Pulsing glow that intensifies as you progress
            PulsingGlow(
                color = InterfastColors.GlyphRed,
                size = (340 + state.progress * 40).dp,
                pulseSpeed = (2000 - (state.progress * 1000).toInt()).coerceAtLeast(800)
            )

            // Celebration particles at milestones!
            CelebrationBurst(
                isActive = showCelebration,
                particleCount = 16,
                color = if (state.progress >= 0.99f) InterfastColors.PhosphorGreen else InterfastColors.GlyphRed
            )

            BouncyBox {
                AnimatedDotProgressRing(
                    progress = state.progress,
                    modifier = Modifier.size(320.dp),
                    activeColor = InterfastColors.GlyphRed,
                    inactiveColor = InterfastColors.Gray15
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Animated rolling digits!
                        AnimatedTimerText(
                            hours = hours,
                            minutes = minutes,
                            textStyle = InterfastTypography.displayLarge,
                            color = InterfastColors.PureWhite,
                            colonColor = InterfastColors.GlyphRed
                        )
                        Spacer(modifier = Modifier.height(Spacing.xs))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            BreathingDot(
                                color = InterfastColors.GlyphRed,
                                size = 10.dp,
                                breathingSpeed = 1000
                            )
                            Spacer(modifier = Modifier.width(Spacing.xs))
                            Text(
                                text = "OF ${state.session.fastingHours}H",
                                style = InterfastTypography.labelMedium,
                                color = InterfastColors.Gray60
                            )
                        }
                        Spacer(modifier = Modifier.height(Spacing.xs))
                        Text(
                            text = "ENDS ${state.formattedEndTime}",
                            style = InterfastTypography.labelSmall,
                            color = InterfastColors.Gray40
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(Spacing.lg))

        // Giant animated percentage
        BouncyBox {
            Text(
                text = state.percentageText,
                style = InterfastTypography.displaySmall,
                color = if (state.progress >= 0.99f) InterfastColors.PhosphorGreen else InterfastColors.GlyphRed
            )
        }

        Spacer(modifier = Modifier.height(Spacing.sm))

        // Motivation with wiggle at high progress
        if (state.progress >= 0.90f) {
            WigglyText(
                text = motivationalMessage,
                textStyle = InterfastTypography.labelLarge,
                color = InterfastColors.PhosphorGreen,
                wiggleAmount = 2f
            )
        } else {
            Text(
                text = motivationalMessage,
                style = InterfastTypography.labelLarge,
                color = InterfastColors.Gray60,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun EatingWindowDisplay(state: TimerState.EatingWindow) {
    // Parse remaining time for animated display
    val totalSeconds = state.eatingTimeRemaining.toMillis() / 1000
    val hours = (totalSeconds / 3600).toInt()
    val minutes = ((totalSeconds % 3600) / 60).toInt()

    // Celebration on entering eating window
    var showInitialCelebration by remember { mutableStateOf(true) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(contentAlignment = Alignment.Center) {
            // Victory glow - calm and satisfied
            PulsingGlow(
                color = InterfastColors.PhosphorGreen,
                size = 360.dp,
                pulseSpeed = 2500
            )

            // Celebration burst when window starts!
            CelebrationBurst(
                isActive = showInitialCelebration,
                particleCount = 20,
                color = InterfastColors.PhosphorGreen
            )

            BouncyBox(
                onTap = { showInitialCelebration = true },
                onDoubleTap = { showInitialCelebration = true }
            ) {
                AnimatedDotProgressRing(
                    progress = 1f,
                    modifier = Modifier.size(320.dp),
                    activeColor = InterfastColors.PhosphorGreen,  // Victory green
                    inactiveColor = InterfastColors.Gray15
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Wiggly victory text!
                        WigglyText(
                            text = "VICTORY",
                            textStyle = InterfastTypography.headlineLarge,
                            color = InterfastColors.PhosphorGreen,
                            wiggleAmount = 1.5f
                        )
                        Spacer(modifier = Modifier.height(Spacing.sm))
                        // Animated countdown for eating window
                        AnimatedTimerText(
                            hours = hours,
                            minutes = minutes,
                            textStyle = InterfastTypography.displayMedium,
                            color = InterfastColors.SignalCyan,
                            colonColor = InterfastColors.PhosphorGreen
                        )
                        Spacer(modifier = Modifier.height(Spacing.xs))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            BreathingDot(
                                color = InterfastColors.PhosphorGreen,
                                size = 8.dp,
                                breathingSpeed = 2000
                            )
                            Spacer(modifier = Modifier.width(Spacing.xs))
                            Text(
                                text = "REFUEL WINDOW",
                                style = InterfastTypography.labelMedium,
                                color = InterfastColors.Gray60
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(Spacing.lg))

        BouncyBox {
            Text(
                text = "YOU EARNED THIS",
                style = InterfastTypography.headlineMedium,
                color = InterfastColors.PhosphorGreen,
                textAlign = TextAlign.Center
            )
        }
        Spacer(modifier = Modifier.height(Spacing.xs))
        Text(
            text = "Fuel your body. It served you well.",
            style = InterfastTypography.bodySmall,
            color = InterfastColors.Gray60,
            textAlign = TextAlign.Center
        )
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
