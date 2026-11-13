package com.example.pagoflex.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pagoflex.data.remote.NotificacionResponse
import com.example.pagoflex.data.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class NotificacionUiState(
    val notificaciones: List<NotificacionResponse> = emptyList(),
    val noLeidas: Long = 0,
    val cargando: Boolean = false,
    val error: String? = null
)

class NotificacionViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(NotificacionUiState())
    val uiState: StateFlow<NotificacionUiState> = _uiState.asStateFlow()

    fun cargar(usuarioId: Long, soloNoLeidas: Boolean = false) {
        viewModelScope.launch {
            _uiState.update { it.copy(cargando = true, error = null) }
            try {
                val lista = RetrofitClient.notificacionApi.listar(usuarioId, soloNoLeidas)
                val contar = RetrofitClient.notificacionApi.contar(usuarioId)
                _uiState.update {
                    it.copy(cargando = false, notificaciones = lista, noLeidas = contar.noLeidas)
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(cargando = false, error = e.message) }
            }
        }
    }

    fun marcarLeida(id: Long, usuarioId: Long) {
        viewModelScope.launch {
            try {
                RetrofitClient.notificacionApi.marcarLeida(id)
                cargar(usuarioId)
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }
}