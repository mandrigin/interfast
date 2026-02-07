package com.interfast.ui.screens.protocol

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.interfast.data.repository.FastingRepository
import com.interfast.domain.model.FastingProtocol
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProtocolViewModel @Inject constructor(
    private val repository: FastingRepository
) : ViewModel() {

    val selectedProtocolId: StateFlow<String> = repository.observeSelectedProtocolId()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = FastingProtocol.PROTOCOL_16_8.id
        )

    fun selectProtocol(protocol: FastingProtocol) {
        viewModelScope.launch {
            repository.setSelectedProtocol(protocol)
        }
    }
}
