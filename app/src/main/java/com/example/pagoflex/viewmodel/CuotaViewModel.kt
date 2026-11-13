package com.example.pagoflex.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pagoflex.data.remote.CuotaResponse
import com.example.pagoflex.data.remote.GenerarCuotasRequest
import com.example.pagoflex.data.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CuotaUiState(
    val cuotas: List<CuotaResponse> = emptyList(),
    val siguiente: CuotaResponse? = null,
    val cargando: Boolean = false,
    val error: String? = null,
    val pagoExitoso: Boolean = false
)

class CuotaViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(CuotaUiState())
    val uiState: StateFlow<CuotaUiState> = _uiState.asStateFlow()

    fun cargarPorDeuda(deudaId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(cargando = true, error = null) }
            try {
                val lista = RetrofitClient.cuotaApi.porDeuda(deudaId)
                val sig = try { RetrofitClient.cuotaApi.siguiente(deudaId) } catch (_: Exception) { null }
                _uiState.update { it.copy(cargando = false, cuotas = lista, siguiente = sig) }
            } catch (e: Exception) {
                _uiState.update { it.copy(cargando = false, error = e.message) }
            }
        }
    }

    fun generar(deudaId: Long, usuarioId: Long, cantidad: Int, primerVenc: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(cargando = true, error = null) }
            try {
                RetrofitClient.cuotaApi.generar(
                    GenerarCuotasRequest(deudaId, usuarioId, cantidad, primerVenc)
                )
                cargarPorDeuda(deudaId)
            } catch (e: Exception) {
                _uiState.update { it.copy(cargando = false, error = e.message) }
            }
        }
    }

    fun pagar(cuotaId: Long, deudaId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(cargando = true, error = null, pagoExitoso = false) }
            try {
                RetrofitClient.cuotaApi.pagar(cuotaId)
                _uiState.update { it.copy(pagoExitoso = true) }
                cargarPorDeuda(deudaId)
            } catch (e: Exception) {
                _uiState.update { it.copy(cargando = false, error = e.message) }
            }
        }
    }
}