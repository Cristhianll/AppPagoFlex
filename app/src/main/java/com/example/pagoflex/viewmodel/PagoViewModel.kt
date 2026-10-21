package com.example.pagoflex.viewmodel

import androidx.lifecycle.ViewModel
import com.example.pagoflex.model.PagoUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class PagoViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(PagoUiState())
    val uiState: StateFlow<PagoUiState> = _uiState.asStateFlow()

    fun cargarUsuario(@Suppress("UNUSED_PARAMETER") usuarioId: Int) {
        _uiState.value = PagoUiState()
    }

    fun limpiarSesion() {
        _uiState.value = PagoUiState()
    }

    fun alternarNotificaciones() {
        _uiState.update { it.copy(notificacionesActivas = !it.notificacionesActivas) }
    }
}