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

    fun confirmarPago() {
        _uiState.update { it.copy(deudaPendiente = false) }
    }

    fun reiniciarPago() {
        _uiState.update { it.copy(deudaPendiente = true) }
    }

    fun vincularTarjeta(ultimosDigitos: String) {
        val limpios = ultimosDigitos.filter { it.isDigit() }.takeLast(4)
        if (limpios.length == 4) {
            _uiState.update {
                it.copy(
                    tarjetaVisaRegistrada = true,
                    ultimosDigitosVisa = limpios,
                    tarjetaPrincipal = true
                )
            }
        }
    }

    fun alternarNotificaciones() {
        _uiState.update { it.copy(notificacionesActivas = !it.notificacionesActivas) }
    }

    fun alternarModoOscuro() {
        _uiState.update { it.copy(modoOscuroAutomatico = !it.modoOscuroAutomatico) }
    }

    fun alternarBiometria() {
        _uiState.update { it.copy(biometriaActiva = !it.biometriaActiva) }
    }
}