package com.example.pagoflex.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pagoflex.data.remote.PagoRequest
import com.example.pagoflex.data.remote.PagoResponse
import com.example.pagoflex.data.remote.RetrofitClient
import com.example.pagoflex.data.remote.TarjetaResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PagoUiState(
    val pagos: List<PagoResponse> = emptyList(),
    val tarjetas: List<TarjetaResponse> = emptyList(),
    val cargando: Boolean = false,
    val error: String? = null,
    val pagoExitoso: Boolean = false,
    val notificacionesActivas: Boolean = true
)

class PagoViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(PagoUiState())
    val uiState: StateFlow<PagoUiState> = _uiState.asStateFlow()

    fun cargarUsuario(usuarioId: Int) {
        cargarHistorial(usuarioId.toLong())
    }

    fun cargarHistorial(usuarioId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(cargando = true, error = null) }
            try {
                val pagos = RetrofitClient.pagoApi.listar(usuarioId)
                val tarjetas = RetrofitClient.tarjetaApi.listar(usuarioId)
                _uiState.update {
                    it.copy(cargando = false, pagos = pagos, tarjetas = tarjetas)
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(cargando = false, error = e.message) }
            }
        }
    }

    fun pagar(deudaId: Long, usuarioId: Long, monto: Double, tarjetaId: Long?) {
        viewModelScope.launch {
            _uiState.update { it.copy(cargando = true, error = null, pagoExitoso = false) }
            try {
                RetrofitClient.pagoApi.registrar(
                    PagoRequest(deudaId, usuarioId, monto, "TARJETA", tarjetaId)
                )
                RetrofitClient.deudaApi.marcarPagada(deudaId)
                _uiState.update { it.copy(cargando = false, pagoExitoso = true) }
                cargarHistorial(usuarioId)
            } catch (e: Exception) {
                _uiState.update { it.copy(cargando = false, error = e.message) }
            }
        }
    }

    fun alternarNotificaciones() {
        _uiState.update { it.copy(notificacionesActivas = !it.notificacionesActivas) }
    }

    fun limpiarSesion() {
        _uiState.value = PagoUiState()
    }
}