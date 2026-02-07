package com.interfast.ui.screens.settings

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.interfast.ui.theme.InterfastColors
import com.interfast.ui.theme.InterfastTheme
import com.interfast.ui.theme.InterfastTypography
import com.interfast.ui.theme.Spacing

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val notificationsEnabled by viewModel.notificationsEnabled.collectAsStateWithLifecycle()
    val milestoneNotifications by viewModel.milestoneNotifications.collectAsStateWithLifecycle()
    val shakeToStartEnabled by viewModel.shakeToStartEnabled.collectAsStateWithLifecycle()
    val hapticFeedbackEnabled by viewModel.hapticFeedbackEnabled.collectAsStateWithLifecycle()
    val darkMode by viewModel.darkMode.collectAsStateWithLifecycle()

    SettingsScreenContent(
        notificationsEnabled = notificationsEnabled,
        milestoneNotifications = milestoneNotifications,
        shakeToStartEnabled = shakeToStartEnabled,
        hapticFeedbackEnabled = hapticFeedbackEnabled,
        darkMode = darkMode,
        onNotificationsToggle = viewModel::setNotificationsEnabled,
        onMilestoneToggle = viewModel::setMilestoneNotifications,
        onShakeToStartToggle = viewModel::setShakeToStartEnabled,
        onHapticFeedbackToggle = viewModel::setHapticFeedbackEnabled,
        onDarkModeChange = viewModel::setDarkMode,
        onExportJson = viewModel::exportToJson,
        onExportCsv = viewModel::exportToCsv
    )
}

@Composable
private fun SettingsScreenContent(
    notificationsEnabled: Boolean,
    milestoneNotifications: Boolean,
    shakeToStartEnabled: Boolean,
    hapticFeedbackEnabled: Boolean,
    darkMode: String,
    onNotificationsToggle: (Boolean) -> Unit,
    onMilestoneToggle: (Boolean) -> Unit,
    onShakeToStartToggle: (Boolean) -> Unit,
    onHapticFeedbackToggle: (Boolean) -> Unit,
    onDarkModeChange: (String) -> Unit,
    onExportJson: () -> Unit,
    onExportCsv: () -> Unit
) {
    var showThemeDialog by remember { mutableStateOf(false) }

    if (showThemeDialog) {
        ThemePickerDialog(
            currentMode = darkMode,
            onModeSelected = { mode ->
                onDarkModeChange(mode)
                showThemeDialog = false
            },
            onDismiss = { showThemeDialog = false }
        )
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(InterfastColors.VoidBlack)
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Text(
            text = "SETTINGS",
            style = InterfastTypography.headlineMedium,
            color = InterfastColors.PureWhite,
            modifier = Modifier.padding(Spacing.lg)
        )

        // Divider
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(InterfastColors.Gray15)
        )

        Spacer(modifier = Modifier.height(Spacing.lg))

        // Notifications Section
        SectionHeader(title = "NOTIFICATIONS")

        SettingsToggle(
            title = "Notifications",
            subtitle = "Enable all notifications",
            checked = notificationsEnabled,
            onCheckedChange = onNotificationsToggle
        )

        SettingsToggle(
            title = "Milestone Alerts",
            subtitle = "Notify at 25%, 50%, 75%, 100%",
            checked = milestoneNotifications && notificationsEnabled,
            enabled = notificationsEnabled,
            onCheckedChange = onMilestoneToggle
        )

        Spacer(modifier = Modifier.height(Spacing.lg))

        // Interaction Section
        SectionHeader(title = "INTERACTION")

        SettingsToggle(
            title = "Shake to Start",
            subtitle = "Shake phone to start a fast",
            checked = shakeToStartEnabled,
            onCheckedChange = onShakeToStartToggle
        )

        SettingsToggle(
            title = "Haptic Feedback",
            subtitle = "Vibration for button presses",
            checked = hapticFeedbackEnabled,
            onCheckedChange = onHapticFeedbackToggle
        )

        Spacer(modifier = Modifier.height(Spacing.lg))

        // Appearance Section
        SectionHeader(title = "APPEARANCE")

        SettingsItem(
            title = "Theme",
            value = darkMode.replaceFirstChar { it.uppercase() },
            onClick = { showThemeDialog = true }
        )

        Spacer(modifier = Modifier.height(Spacing.lg))

        // Data Section
        SectionHeader(title = "DATA")

        SettingsItem(
            title = "Export as JSON",
            value = "Backup format",
            onClick = onExportJson
        )

        SettingsItem(
            title = "Export as CSV",
            value = "Spreadsheet format",
            onClick = onExportCsv
        )

        Spacer(modifier = Modifier.height(Spacing.lg))

        // About Section
        SectionHeader(title = "ABOUT")

        SettingsItem(
            title = "Version",
            value = "1.0.0",
            showChevron = false,
            onClick = {}
        )

        Spacer(modifier = Modifier.height(Spacing.xxl))

        // Credits
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.lg),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "INTERFAST",
                style = InterfastTypography.labelMedium,
                color = InterfastColors.Gray40
            )
            Spacer(modifier = Modifier.height(Spacing.xs))
            Text(
                text = "A practical work of art",
                style = InterfastTypography.bodySmall,
                color = InterfastColors.Gray40
            )
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = InterfastTypography.labelMedium,
        color = InterfastColors.Gray60,
        modifier = Modifier.padding(horizontal = Spacing.lg, vertical = Spacing.sm)
    )
}

@Composable
private fun SettingsItem(
    title: String,
    value: String,
    showChevron: Boolean = true,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = Spacing.lg, vertical = Spacing.md),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = InterfastTypography.bodyLarge,
            color = InterfastColors.PureWhite
        )
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (value.isNotEmpty()) {
                Text(
                    text = value,
                    style = InterfastTypography.bodyMedium,
                    color = InterfastColors.Gray60
                )
            }
            if (showChevron) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = InterfastColors.Gray40,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun SettingsToggle(
    title: String,
    subtitle: String,
    checked: Boolean,
    enabled: Boolean = true,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = enabled) { onCheckedChange(!checked) }
            .padding(horizontal = Spacing.lg, vertical = Spacing.md),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = InterfastTypography.bodyLarge,
                color = if (enabled) InterfastColors.PureWhite else InterfastColors.Gray40
            )
            Text(
                text = subtitle,
                style = InterfastTypography.bodySmall,
                color = InterfastColors.Gray40
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = enabled,
            colors = SwitchDefaults.colors(
                checkedThumbColor = InterfastColors.PureWhite,
                checkedTrackColor = InterfastColors.GlyphRed,
                uncheckedThumbColor = InterfastColors.Gray60,
                uncheckedTrackColor = InterfastColors.Gray15,
                uncheckedBorderColor = InterfastColors.Gray20
            )
        )
    }
}

@Composable
private fun ThemePickerDialog(
    currentMode: String,
    onModeSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val options = listOf("dark", "light", "system")

    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = InterfastColors.Gray10,
        title = {
            Text(
                text = "THEME",
                style = InterfastTypography.headlineSmall,
                color = InterfastColors.PureWhite
            )
        },
        text = {
            Column {
                options.forEach { mode ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onModeSelected(mode) }
                            .padding(vertical = Spacing.md),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        androidx.compose.material3.RadioButton(
                            selected = currentMode == mode,
                            onClick = { onModeSelected(mode) },
                            colors = androidx.compose.material3.RadioButtonDefaults.colors(
                                selectedColor = InterfastColors.GlyphRed,
                                unselectedColor = InterfastColors.Gray40
                            )
                        )
                        Text(
                            text = mode.replaceFirstChar { it.uppercase() },
                            style = InterfastTypography.bodyLarge,
                            color = InterfastColors.PureWhite,
                            modifier = Modifier.padding(start = Spacing.sm)
                        )
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            Text(
                text = "Cancel",
                style = InterfastTypography.labelLarge,
                color = InterfastColors.Gray60,
                modifier = Modifier
                    .clickable { onDismiss() }
                    .padding(Spacing.md)
            )
        }
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun SettingsScreenPreview() {
    InterfastTheme {
        SettingsScreenContent(
            notificationsEnabled = true,
            milestoneNotifications = true,
            shakeToStartEnabled = false,
            hapticFeedbackEnabled = true,
            darkMode = "dark",
            onNotificationsToggle = {},
            onMilestoneToggle = {},
            onShakeToStartToggle = {},
            onHapticFeedbackToggle = {},
            onDarkModeChange = {},
            onExportJson = {},
            onExportCsv = {}
        )
    }
}
