package com.interfast.ui.screens.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.interfast.domain.model.FastingProtocol
import com.interfast.ui.components.DotProgressBar
import com.interfast.ui.components.InterfastButton
import com.interfast.ui.components.PrimaryActionButton
import com.interfast.ui.theme.InterfastColors
import com.interfast.ui.theme.InterfastTheme
import com.interfast.ui.theme.InterfastTypography
import com.interfast.ui.theme.Spacing

/**
 * Onboarding screen for first-time users.
 *
 * Design philosophy: Education + Action.
 * Users should understand why they're fasting and feel confident to start.
 *
 * Flow:
 * 1. Welcome - What is intermittent fasting?
 * 2. Protocol - Choose your fasting schedule
 * 3. Ready - Enable notifications and start first fast
 */
@Composable
fun OnboardingScreen(
    onComplete: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    OnboardingContent(
        currentStep = uiState.currentStep,
        selectedProtocol = uiState.selectedProtocol,
        notificationsEnabled = uiState.notificationsEnabled,
        onNextStep = viewModel::nextStep,
        onPreviousStep = viewModel::previousStep,
        onProtocolSelected = viewModel::selectProtocol,
        onNotificationsToggle = viewModel::toggleNotifications,
        onComplete = {
            viewModel.completeOnboarding()
            onComplete()
        }
    )
}

@Composable
private fun OnboardingContent(
    currentStep: Int,
    selectedProtocol: FastingProtocol,
    notificationsEnabled: Boolean,
    onNextStep: () -> Unit,
    onPreviousStep: () -> Unit,
    onProtocolSelected: (FastingProtocol) -> Unit,
    onNotificationsToggle: () -> Unit,
    onComplete: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(InterfastColors.VoidBlack)
            .padding(Spacing.lg)
    ) {
        // Progress indicators
        OnboardingProgress(currentStep = currentStep, totalSteps = 3)

        Spacer(modifier = Modifier.height(Spacing.xl))

        // Step content
        AnimatedContent(
            targetState = currentStep,
            transitionSpec = {
                if (targetState > initialState) {
                    slideInHorizontally { it } + fadeIn() togetherWith
                            slideOutHorizontally { -it } + fadeOut()
                } else {
                    slideInHorizontally { -it } + fadeIn() togetherWith
                            slideOutHorizontally { it } + fadeOut()
                }
            },
            modifier = Modifier.weight(1f),
            label = "onboarding_step"
        ) { step ->
            when (step) {
                0 -> WelcomeStep()
                1 -> ProtocolStep(
                    selectedProtocol = selectedProtocol,
                    onProtocolSelected = onProtocolSelected
                )
                2 -> ReadyStep(
                    notificationsEnabled = notificationsEnabled,
                    onNotificationsToggle = onNotificationsToggle
                )
            }
        }

        Spacer(modifier = Modifier.height(Spacing.lg))

        // Navigation buttons
        OnboardingNavigation(
            currentStep = currentStep,
            onNext = onNextStep,
            onPrevious = onPreviousStep,
            onComplete = onComplete
        )
    }
}

@Composable
private fun OnboardingProgress(
    currentStep: Int,
    totalSteps: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        repeat(totalSteps) { index ->
            val isActive = index <= currentStep
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(
                        if (isActive) InterfastColors.GlyphRed
                        else InterfastColors.Gray20
                    )
            )
            if (index < totalSteps - 1) {
                Spacer(modifier = Modifier.width(Spacing.sm))
            }
        }
    }
}

@Composable
private fun WelcomeStep() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(Spacing.xxl))

        // Bold manifesto opener
        Text(
            text = "INTERFAST",
            style = InterfastTypography.displayLarge,
            color = InterfastColors.PureWhite
        )

        Spacer(modifier = Modifier.height(Spacing.md))

        // Tagline with attitude
        Text(
            text = "DISCIPLINE. NOT DIETS.",
            style = InterfastTypography.headlineMedium,
            color = InterfastColors.GlyphRed
        )

        Spacer(modifier = Modifier.height(Spacing.xxxl))

        // Manifesto section
        Text(
            text = "THE TRUTH",
            style = InterfastTypography.labelMedium,
            color = InterfastColors.Gray60
        )

        Spacer(modifier = Modifier.height(Spacing.md))

        Text(
            text = "Your hunger is a suggestion, not a command.",
            style = InterfastTypography.headlineLarge,
            color = InterfastColors.PureWhite,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(Spacing.md))

        Text(
            text = "For millions of years, humans ate when food was available—not on a schedule. Your body knows how to thrive without constant feeding. Modern convenience trained you to eat by the clock. It's time to unlearn.",
            style = InterfastTypography.bodyLarge,
            color = InterfastColors.Gray80,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(Spacing.xl))

        // Bold beliefs instead of benefits
        ManifestoPoint(
            text = "CLARITY COMES FROM EMPTINESS",
            subtext = "The best thinking happens when you're not digesting."
        )

        ManifestoPoint(
            text = "CONTROL IS FREEDOM",
            subtext = "Master your hunger. Master your day."
        )

        ManifestoPoint(
            text = "LESS IS MORE",
            subtext = "No counting. No tracking. Just the clock."
        )
    }
}

@Composable
private fun ManifestoPoint(
    text: String,
    subtext: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Spacing.md)
    ) {
        Text(
            text = text,
            style = InterfastTypography.labelLarge,
            color = InterfastColors.PhosphorGreen
        )
        Text(
            text = subtext,
            style = InterfastTypography.bodyMedium,
            color = InterfastColors.Gray60
        )
    }
}

@Composable
private fun ProtocolStep(
    selectedProtocol: FastingProtocol,
    onProtocolSelected: (FastingProtocol) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "PICK YOUR BATTLE",
            style = InterfastTypography.displaySmall,
            color = InterfastColors.PureWhite
        )

        Spacer(modifier = Modifier.height(Spacing.sm))

        Text(
            text = "More hours fasting = harder challenge. Start where you can win, then level up.",
            style = InterfastTypography.bodyMedium,
            color = InterfastColors.Gray60
        )

        Spacer(modifier = Modifier.height(Spacing.xl))

        FastingProtocol.DEFAULT_PROTOCOLS.forEach { protocol ->
            ProtocolCard(
                protocol = protocol,
                isSelected = protocol.id == selectedProtocol.id,
                onClick = { onProtocolSelected(protocol) }
            )
            Spacer(modifier = Modifier.height(Spacing.md))
        }
    }
}

@Composable
private fun ProtocolCard(
    protocol: FastingProtocol,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) InterfastColors.Gray15 else InterfastColors.Gray10
    val borderColor = if (isSelected) InterfastColors.GlyphRed else InterfastColors.Gray20

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(Spacing.md)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = protocol.name,
                    style = InterfastTypography.headlineMedium,
                    color = InterfastColors.PureWhite
                )
                Text(
                    text = "${protocol.fastingHours}h fast - ${protocol.eatingHours}h eat",
                    style = InterfastTypography.bodySmall,
                    color = InterfastColors.Gray60
                )
            }

            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(InterfastColors.GlyphRed),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Selected",
                        tint = InterfastColors.PureWhite,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(Spacing.sm))

        // Visual representation
        DotProgressBar(
            progress = protocol.fastingHours / 24f,
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp),
            dotCount = 24,
            activeColor = InterfastColors.GlyphRed,
            inactiveColor = InterfastColors.SignalCyan.copy(alpha = 0.3f),
            dotSize = 8.dp,
            dotSpacing = 2.dp
        )

        Spacer(modifier = Modifier.height(Spacing.xs))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Fasting",
                style = InterfastTypography.labelSmall,
                color = InterfastColors.GlyphRed
            )
            Text(
                text = "Eating",
                style = InterfastTypography.labelSmall,
                color = InterfastColors.SignalCyan
            )
        }

        // Recommendation
        if (protocol.id == "16_8") {
            Spacer(modifier = Modifier.height(Spacing.sm))
            Text(
                text = "Recommended for beginners",
                style = InterfastTypography.labelSmall,
                color = InterfastColors.PhosphorGreen
            )
        }
    }
}

@Composable
private fun ReadyStep(
    notificationsEnabled: Boolean,
    onNotificationsToggle: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(Spacing.xxl))

        Text(
            text = "READY TO BURN",
            style = InterfastTypography.displaySmall,
            color = InterfastColors.GlyphRed
        )

        Spacer(modifier = Modifier.height(Spacing.md))

        Text(
            text = "Notifications will push you through the hard moments. Recommended for warriors in training.",
            style = InterfastTypography.bodyLarge,
            color = InterfastColors.Gray80,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(Spacing.xl))

        // Notifications toggle
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(InterfastColors.Gray15)
                .clickable(onClick = onNotificationsToggle)
                .padding(Spacing.md),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = null,
                    tint = InterfastColors.AmberWarning,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(Spacing.md))
                Column {
                    Text(
                        text = "Enable Notifications",
                        style = InterfastTypography.bodyLarge,
                        color = InterfastColors.PureWhite
                    )
                    Text(
                        text = "Get milestone alerts at 25%, 50%, 75%, 100%",
                        style = InterfastTypography.bodySmall,
                        color = InterfastColors.Gray60
                    )
                }
            }

            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(
                        if (notificationsEnabled) InterfastColors.PhosphorGreen
                        else InterfastColors.Gray40
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (notificationsEnabled) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Enabled",
                        tint = InterfastColors.VoidBlack,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Pro tip - more aggressive
        Text(
            text = "PROTIP",
            style = InterfastTypography.labelMedium,
            color = InterfastColors.AmberWarning
        )
        Spacer(modifier = Modifier.height(Spacing.xs))
        Text(
            text = "Start after dinner. Sleep is free fasting hours. Wake up already halfway done.",
            style = InterfastTypography.bodyMedium,
            color = InterfastColors.Gray80,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun OnboardingNavigation(
    currentStep: Int,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onComplete: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Spacing.md)
    ) {
        AnimatedVisibility(
            visible = currentStep > 0,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            InterfastButton(
                text = "Back",
                onClick = onPrevious,
                modifier = Modifier.width(100.dp)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        if (currentStep < 2) {
            PrimaryActionButton(
                text = "CONTINUE",
                onClick = onNext,
                icon = Icons.AutoMirrored.Filled.ArrowForward,
                modifier = Modifier.width(160.dp)
            )
        } else {
            PrimaryActionButton(
                text = "LET'S GO",
                onClick = onComplete,
                icon = Icons.Default.PlayArrow,
                modifier = Modifier.width(180.dp)
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun WelcomeStepPreview() {
    InterfastTheme {
        OnboardingContent(
            currentStep = 0,
            selectedProtocol = FastingProtocol.PROTOCOL_16_8,
            notificationsEnabled = false,
            onNextStep = {},
            onPreviousStep = {},
            onProtocolSelected = {},
            onNotificationsToggle = {},
            onComplete = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ProtocolStepPreview() {
    InterfastTheme {
        OnboardingContent(
            currentStep = 1,
            selectedProtocol = FastingProtocol.PROTOCOL_16_8,
            notificationsEnabled = false,
            onNextStep = {},
            onPreviousStep = {},
            onProtocolSelected = {},
            onNotificationsToggle = {},
            onComplete = {}
        )
    }
}
