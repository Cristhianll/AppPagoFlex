package com.example.pagoflex.viewmodel

import androidx.lifecycle.ViewModel
import com.example.pagoflex.model.PagoUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class PagoViewModel : ViewModel() {

    // Estado privado: solo este ViewModel puede cambiarlo.
    private val _uiState = MutableStateFlow(PagoUiState())

    val uiState: StateFlow<PagoUiState> = _uiState.asStateFlow()

    // Se ejecuta al confirmar el pago. El Dashboard reaccionará al cambio.
    fun confirmarPago() {
        _uiState.update { estadoActual ->
            estadoActual.copy(deudaPendiente = false)
        }
    }

    // Restablece la deuda para una nueva sesión de prueba.
    fun reiniciarPago() {
        _uiState.update { estadoActual ->
            estadoActual.copy(deudaPendiente = true)
        }
    }
}

