package com.interfast.ui.screens.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.interfast.data.preferences.UserPreferences
import com.interfast.data.repository.FastingRepository
import com.interfast.domain.model.FastingProtocol
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the onboarding flow.
 *
 * Manages:
 * - Current step navigation
 * - Protocol selection
 * - Notification preferences
 * - Onboarding completion flag
 */
@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val repository: FastingRepository,
    private val preferences: UserPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    init {
        loadInitialState()
    }

    private fun loadInitialState() {
        viewModelScope.launch {
            // Load default protocol preference if any
            val protocol = repository.getSelectedProtocol()
            _uiState.update {
                it.copy(selectedProtocol = protocol)
            }
        }
    }

    fun nextStep() {
        _uiState.update {
            it.copy(currentStep = (it.currentStep + 1).coerceAtMost(2))
        }
    }

    fun previousStep() {
        _uiState.update {
            it.copy(currentStep = (it.currentStep - 1).coerceAtLeast(0))
        }
    }

    fun selectProtocol(protocol: FastingProtocol) {
        viewModelScope.launch {
            repository.setSelectedProtocol(protocol)
            _uiState.update {
                it.copy(selectedProtocol = protocol)
            }
        }
    }

    fun toggleNotifications() {
        viewModelScope.launch {
            val newValue = !_uiState.value.notificationsEnabled
            preferences.setNotificationsEnabled(newValue)
            preferences.setMilestoneNotifications(newValue)
            _uiState.update {
                it.copy(notificationsEnabled = newValue)
            }
        }
    }

    fun completeOnboarding() {
        viewModelScope.launch {
            preferences.setOnboardingCompleted(true)
        }
    }
}

data class OnboardingUiState(
    val currentStep: Int = 0,
    val selectedProtocol: FastingProtocol = FastingProtocol.PROTOCOL_16_8,
    val notificationsEnabled: Boolean = false
)
