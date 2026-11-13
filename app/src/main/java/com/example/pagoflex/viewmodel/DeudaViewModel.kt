package com.example.pagoflex.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pagoflex.data.remote.DeudaResponse
import com.example.pagoflex.data.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DeudaUiState(
    val deudas: List<DeudaResponse> = emptyList(),
    val cargando: Boolean = false,
    val error: String? = null,
    // filtros
    val filtroEstado: String? = null,
    val filtroDesde: String? = null,
    val filtroHasta: String? = null,
    val filtroMontoMin: Double? = null,
    val filtroMontoMax: Double? = null
)

class DeudaViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(DeudaUiState())
    val uiState: StateFlow<DeudaUiState> = _uiState.asStateFlow()

    fun setFiltroEstado(v: String?) = _uiState.update { it.copy(filtroEstado = v) }
    fun setFiltroDesde(v: String?) = _uiState.update { it.copy(filtroDesde = v) }
    fun setFiltroHasta(v: String?) = _uiState.update { it.copy(filtroHasta = v) }
    fun setFiltroMontoMin(v: Double?) = _uiState.update { it.copy(filtroMontoMin = v) }
    fun setFiltroMontoMax(v: Double?) = _uiState.update { it.copy(filtroMontoMax = v) }

    fun cargar(usuarioId: Long) {
        val s = _uiState.value
        viewModelScope.launch {
            _uiState.update { it.copy(cargando = true, error = null) }
            try {
                val lista = RetrofitClient.deudaApi.listar(
                    usuarioId = usuarioId,
                    estado = s.filtroEstado,
                    desde = s.filtroDesde,
                    hasta = s.filtroHasta,
                    montoMin = s.filtroMontoMin,
                    montoMax = s.filtroMontoMax
                )
                _uiState.update { it.copy(cargando = false, deudas = lista) }
            } catch (e: Exception) {
                _uiState.update { it.copy(cargando = false, error = e.message) }
            }
        }
    }

    fun marcarPagada(deudaId: Long, usuarioId: Long) {
        viewModelScope.launch {
            try {
                RetrofitClient.deudaApi.marcarPagada(deudaId)
                cargar(usuarioId)
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }
}