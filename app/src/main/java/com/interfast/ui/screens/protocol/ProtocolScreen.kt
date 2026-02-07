package com.interfast.ui.screens.protocol

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.interfast.domain.model.FastingProtocol
import com.interfast.ui.components.DotProgressBar
import com.interfast.ui.components.InterfastButton
import com.interfast.ui.theme.InterfastColors
import com.interfast.ui.theme.InterfastTheme
import com.interfast.ui.theme.InterfastTypography
import com.interfast.ui.theme.Spacing

@Composable
fun ProtocolScreen(
    onNavigateBack: () -> Unit,
    onProtocolSelected: (FastingProtocol) -> Unit,
    viewModel: ProtocolViewModel = hiltViewModel()
) {
    val selectedProtocolId by viewModel.selectedProtocolId.collectAsStateWithLifecycle()
    var showCustomDialog by remember { mutableStateOf(false) }

    ProtocolScreenContent(
        selectedProtocolId = selectedProtocolId,
        onNavigateBack = onNavigateBack,
        onProtocolSelected = { protocol ->
            viewModel.selectProtocol(protocol)
            onProtocolSelected(protocol)
        },
        onCustomClick = { showCustomDialog = true }
    )

    if (showCustomDialog) {
        CustomProtocolDialog(
            onDismiss = { showCustomDialog = false },
            onConfirm = { fastingHours, eatingHours ->
                val protocol = FastingProtocol.custom(fastingHours, eatingHours)
                viewModel.selectProtocol(protocol)
                onProtocolSelected(protocol)
                showCustomDialog = false
            }
        )
    }
}

@Composable
private fun ProtocolScreenContent(
    selectedProtocolId: String,
    onNavigateBack: () -> Unit,
    onProtocolSelected: (FastingProtocol) -> Unit,
    onCustomClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(InterfastColors.VoidBlack)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.md, vertical = Spacing.md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = InterfastColors.PureWhite
                )
            }
            Spacer(modifier = Modifier.width(Spacing.sm))
            Text(
                text = "PROTOCOL",
                style = InterfastTypography.headlineMedium,
                color = InterfastColors.PureWhite
            )
        }

        // Divider
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(InterfastColors.Gray15)
        )

        // Protocol list
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(Spacing.md)
        ) {
            item { Spacer(modifier = Modifier.height(Spacing.md)) }

            items(FastingProtocol.DEFAULT_PROTOCOLS) { protocol ->
                ProtocolCard(
                    protocol = protocol,
                    isSelected = protocol.id == selectedProtocolId,
                    onClick = { onProtocolSelected(protocol) }
                )
            }

            item {
                CustomProtocolCard(onClick = onCustomClick)
            }

            item { Spacer(modifier = Modifier.height(Spacing.lg)) }
        }
    }
}

@Composable
private fun ProtocolCard(
    protocol: FastingProtocol,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected) InterfastColors.GlyphRed else InterfastColors.Gray15
    val fastingProgress = protocol.fastingHours.toFloat() / 24f

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(12.dp)
            )
            .background(InterfastColors.Gray10)
            .clickable(onClick = onClick)
            .padding(Spacing.md)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            // Selection indicator
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) InterfastColors.GlyphRed else InterfastColors.Gray15),
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Selected",
                        tint = InterfastColors.PureWhite,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(Spacing.md))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = protocol.name,
                        style = InterfastTypography.headlineMedium,
                        color = InterfastColors.PureWhite
                    )
                    if (isSelected) {
                        Text(
                            text = "SELECTED",
                            style = InterfastTypography.labelSmall,
                            color = InterfastColors.GlyphRed
                        )
                    }
                }

                Spacer(modifier = Modifier.height(Spacing.xs))

                Text(
                    text = "${protocol.fastingHours}h fast  ·  ${protocol.eatingHours}h eat",
                    style = InterfastTypography.bodyMedium,
                    color = InterfastColors.Gray60
                )

                Spacer(modifier = Modifier.height(Spacing.sm))

                // Visual progress representation
                DotProgressBar(
                    progress = fastingProgress,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp),
                    dotCount = 24,
                    activeColor = InterfastColors.GlyphRed,
                    inactiveColor = InterfastColors.SignalCyan.copy(alpha = 0.3f),
                    dotSize = 6.dp,
                    dotSpacing = 2.dp
                )
            }
        }
    }
}

@Composable
private fun CustomProtocolCard(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(
                width = 1.dp,
                color = InterfastColors.Gray15,
                shape = RoundedCornerShape(12.dp)
            )
            .background(InterfastColors.Gray10)
            .clickable(onClick = onClick)
            .padding(Spacing.md)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(InterfastColors.Gray15),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add",
                    tint = InterfastColors.Gray60,
                    modifier = Modifier.size(16.dp)
                )
            }

            Spacer(modifier = Modifier.width(Spacing.md))

            Column {
                Text(
                    text = "CUSTOM",
                    style = InterfastTypography.headlineMedium,
                    color = InterfastColors.Gray60
                )
                Text(
                    text = "Create your own protocol",
                    style = InterfastTypography.bodyMedium,
                    color = InterfastColors.Gray40
                )
            }
        }
    }
}

@Composable
private fun CustomProtocolDialog(
    onDismiss: () -> Unit,
    onConfirm: (fastingHours: Int, eatingHours: Int) -> Unit
) {
    var fastingHours by remember { mutableIntStateOf(16) }
    var eatingHours by remember { mutableIntStateOf(8) }

    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(InterfastColors.Gray10)
                .padding(Spacing.lg)
        ) {
            Text(
                text = "CUSTOM PROTOCOL",
                style = InterfastTypography.headlineMedium,
                color = InterfastColors.PureWhite
            )

            Spacer(modifier = Modifier.height(Spacing.lg))

            // Fasting hours
            Text(
                text = "FASTING HOURS",
                style = InterfastTypography.labelMedium,
                color = InterfastColors.Gray60
            )
            Spacer(modifier = Modifier.height(Spacing.sm))
            HourSelector(
                value = fastingHours,
                onValueChange = {
                    fastingHours = it
                    eatingHours = 24 - it
                },
                range = 12..23
            )

            Spacer(modifier = Modifier.height(Spacing.lg))

            // Eating hours (calculated)
            Text(
                text = "EATING HOURS",
                style = InterfastTypography.labelMedium,
                color = InterfastColors.Gray60
            )
            Spacer(modifier = Modifier.height(Spacing.sm))
            Text(
                text = "$eatingHours hours",
                style = InterfastTypography.headlineMedium,
                color = InterfastColors.SignalCyan
            )

            Spacer(modifier = Modifier.height(Spacing.xl))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.md)
            ) {
                InterfastButton(
                    text = "Cancel",
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f),
                    isPrimary = false
                )
                InterfastButton(
                    text = "Apply",
                    onClick = { onConfirm(fastingHours, eatingHours) },
                    modifier = Modifier.weight(1f),
                    backgroundColor = InterfastColors.GlyphRed
                )
            }
        }
    }
}

@Composable
private fun HourSelector(
    value: Int,
    onValueChange: (Int) -> Unit,
    range: IntRange
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = { if (value > range.first) onValueChange(value - 1) },
            enabled = value > range.first
        ) {
            Text(
                text = "-",
                style = InterfastTypography.headlineLarge,
                color = if (value > range.first) InterfastColors.PureWhite else InterfastColors.Gray40
            )
        }

        Text(
            text = "$value hours",
            style = InterfastTypography.headlineLarge,
            color = InterfastColors.GlyphRed
        )

        IconButton(
            onClick = { if (value < range.last) onValueChange(value + 1) },
            enabled = value < range.last
        ) {
            Text(
                text = "+",
                style = InterfastTypography.headlineLarge,
                color = if (value < range.last) InterfastColors.PureWhite else InterfastColors.Gray40
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ProtocolScreenPreview() {
    InterfastTheme {
        ProtocolScreenContent(
            selectedProtocolId = FastingProtocol.PROTOCOL_16_8.id,
            onNavigateBack = {},
            onProtocolSelected = {},
            onCustomClick = {}
        )
    }
}
