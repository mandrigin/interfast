package com.interfast.ui.screens.settings

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.interfast.data.export.DataExporter
import com.interfast.data.export.ExportResult
import com.interfast.data.preferences.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferences: UserPreferences,
    private val dataExporter: DataExporter,
    @ApplicationContext private val context: Context
) : ViewModel() {

    val notificationsEnabled: StateFlow<Boolean> = preferences.notificationsEnabled
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = true
        )

    val milestoneNotifications: StateFlow<Boolean> = preferences.milestoneNotificationsEnabled
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = true
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

    val darkMode: StateFlow<String> = preferences.darkMode
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = "dark"
        )

    private val _exportState = MutableStateFlow<ExportState>(ExportState.Idle)
    val exportState: StateFlow<ExportState> = _exportState.asStateFlow()

    fun setNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferences.setNotificationsEnabled(enabled)
        }
    }

    fun setMilestoneNotifications(enabled: Boolean) {
        viewModelScope.launch {
            preferences.setMilestoneNotifications(enabled)
        }
    }

    fun setShakeToStartEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferences.setShakeToStartEnabled(enabled)
        }
    }

    fun setHapticFeedbackEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferences.setHapticFeedbackEnabled(enabled)
        }
    }

    fun setDarkMode(mode: String) {
        viewModelScope.launch {
            preferences.setDarkMode(mode)
        }
    }

    fun exportToJson() {
        viewModelScope.launch {
            _exportState.value = ExportState.Exporting
            when (val result = dataExporter.exportToJson()) {
                is ExportResult.Success -> {
                    val intent = dataExporter.createShareIntent(result)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(Intent.createChooser(intent, "Share Interfast Data").apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    })
                    _exportState.value = ExportState.Success
                }
                is ExportResult.Error -> {
                    _exportState.value = ExportState.Error(result.message)
                }
            }
        }
    }

    fun exportToCsv() {
        viewModelScope.launch {
            _exportState.value = ExportState.Exporting
            when (val result = dataExporter.exportToCsv()) {
                is ExportResult.Success -> {
                    val intent = dataExporter.createShareIntent(result)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(Intent.createChooser(intent, "Share Interfast Data").apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    })
                    _exportState.value = ExportState.Success
                }
                is ExportResult.Error -> {
                    _exportState.value = ExportState.Error(result.message)
                }
            }
        }
    }

    fun clearExportState() {
        _exportState.value = ExportState.Idle
    }
}

sealed class ExportState {
    object Idle : ExportState()
    object Exporting : ExportState()
    object Success : ExportState()
    data class Error(val message: String) : ExportState()
}
