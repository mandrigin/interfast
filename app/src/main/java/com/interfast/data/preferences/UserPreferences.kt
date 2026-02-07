package com.interfast.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.interfast.domain.model.FastingProtocol
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "interfast_prefs")

@Singleton
class UserPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore = context.dataStore

    // Keys
    private object Keys {
        val SELECTED_PROTOCOL_ID = stringPreferencesKey("selected_protocol_id")
        val CUSTOM_FASTING_HOURS = intPreferencesKey("custom_fasting_hours")
        val CUSTOM_EATING_HOURS = intPreferencesKey("custom_eating_hours")
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        val MILESTONE_NOTIFICATIONS = booleanPreferencesKey("milestone_notifications")
        val START_REMINDER_ENABLED = booleanPreferencesKey("start_reminder_enabled")
        val START_REMINDER_HOUR = intPreferencesKey("start_reminder_hour")
        val START_REMINDER_MINUTE = intPreferencesKey("start_reminder_minute")
        val DARK_MODE = stringPreferencesKey("dark_mode") // "system", "dark", "light"
        val DYNAMIC_COLORS = booleanPreferencesKey("dynamic_colors")
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        val SHAKE_TO_START_ENABLED = booleanPreferencesKey("shake_to_start_enabled")
        val HAPTIC_FEEDBACK_ENABLED = booleanPreferencesKey("haptic_feedback_enabled")
    }

    // Selected Protocol
    val selectedProtocolId: Flow<String> = dataStore.data.map { prefs ->
        prefs[Keys.SELECTED_PROTOCOL_ID] ?: FastingProtocol.PROTOCOL_16_8.id
    }

    suspend fun setSelectedProtocol(protocolId: String) {
        dataStore.edit { prefs ->
            prefs[Keys.SELECTED_PROTOCOL_ID] = protocolId
        }
    }

    // Custom Protocol
    val customFastingHours: Flow<Int> = dataStore.data.map { prefs ->
        prefs[Keys.CUSTOM_FASTING_HOURS] ?: 16
    }

    val customEatingHours: Flow<Int> = dataStore.data.map { prefs ->
        prefs[Keys.CUSTOM_EATING_HOURS] ?: 8
    }

    suspend fun setCustomProtocol(fastingHours: Int, eatingHours: Int) {
        dataStore.edit { prefs ->
            prefs[Keys.CUSTOM_FASTING_HOURS] = fastingHours
            prefs[Keys.CUSTOM_EATING_HOURS] = eatingHours
        }
    }

    // Notifications
    val notificationsEnabled: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[Keys.NOTIFICATIONS_ENABLED] ?: true
    }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        dataStore.edit { prefs ->
            prefs[Keys.NOTIFICATIONS_ENABLED] = enabled
        }
    }

    val milestoneNotificationsEnabled: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[Keys.MILESTONE_NOTIFICATIONS] ?: true
    }

    suspend fun setMilestoneNotifications(enabled: Boolean) {
        dataStore.edit { prefs ->
            prefs[Keys.MILESTONE_NOTIFICATIONS] = enabled
        }
    }

    val startReminderEnabled: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[Keys.START_REMINDER_ENABLED] ?: false
    }

    data class ReminderTime(val hour: Int, val minute: Int)

    val startReminderTime: Flow<ReminderTime> = dataStore.data.map { prefs ->
        ReminderTime(
            hour = prefs[Keys.START_REMINDER_HOUR] ?: 7,
            minute = prefs[Keys.START_REMINDER_MINUTE] ?: 0
        )
    }

    suspend fun setStartReminder(enabled: Boolean, hour: Int = 7, minute: Int = 0) {
        dataStore.edit { prefs ->
            prefs[Keys.START_REMINDER_ENABLED] = enabled
            prefs[Keys.START_REMINDER_HOUR] = hour
            prefs[Keys.START_REMINDER_MINUTE] = minute
        }
    }

    // Theme
    val darkMode: Flow<String> = dataStore.data.map { prefs ->
        prefs[Keys.DARK_MODE] ?: "dark" // Default to dark for our aesthetic
    }

    suspend fun setDarkMode(mode: String) {
        dataStore.edit { prefs ->
            prefs[Keys.DARK_MODE] = mode
        }
    }

    val dynamicColorsEnabled: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[Keys.DYNAMIC_COLORS] ?: false // Disabled by default to preserve brand colors
    }

    suspend fun setDynamicColors(enabled: Boolean) {
        dataStore.edit { prefs ->
            prefs[Keys.DYNAMIC_COLORS] = enabled
        }
    }

    // Onboarding
    val onboardingCompleted: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[Keys.ONBOARDING_COMPLETED] ?: false
    }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        dataStore.edit { prefs ->
            prefs[Keys.ONBOARDING_COMPLETED] = completed
        }
    }

    // Shake to Start
    val shakeToStartEnabled: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[Keys.SHAKE_TO_START_ENABLED] ?: false // Disabled by default
    }

    suspend fun setShakeToStartEnabled(enabled: Boolean) {
        dataStore.edit { prefs ->
            prefs[Keys.SHAKE_TO_START_ENABLED] = enabled
        }
    }

    // Haptic Feedback
    val hapticFeedbackEnabled: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[Keys.HAPTIC_FEEDBACK_ENABLED] ?: true // Enabled by default
    }

    suspend fun setHapticFeedbackEnabled(enabled: Boolean) {
        dataStore.edit { prefs ->
            prefs[Keys.HAPTIC_FEEDBACK_ENABLED] = enabled
        }
    }
}
