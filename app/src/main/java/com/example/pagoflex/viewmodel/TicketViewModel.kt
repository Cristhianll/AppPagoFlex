package com.example.pagoflex.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pagoflex.data.remote.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TicketUiState(
    val tickets: List<TicketResponse> = emptyList(),
    val mensajes: List<MensajeResponse> = emptyList(),
    val cargando: Boolean = false,
    val error: String? = null,
    val creado: Boolean = false
)

class TicketViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(TicketUiState())
    val uiState: StateFlow<TicketUiState> = _uiState.asStateFlow()

    fun cargarMisTickets(usuarioId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(cargando = true, error = null) }
            try {
                val lista = RetrofitClient.ticketApi.listar(usuarioId = usuarioId)
                _uiState.update { it.copy(cargando = false, tickets = lista) }
            } catch (e: Exception) {
                _uiState.update { it.copy(cargando = false, error = e.message) }
            }
        }
    }

    fun cargarParaGestor(estado: String? = "ABIERTO") {
        viewModelScope.launch {
            _uiState.update { it.copy(cargando = true, error = null) }
            try {
                val lista = RetrofitClient.ticketApi.listar(estado = estado)
                _uiState.update { it.copy(cargando = false, tickets = lista) }
            } catch (e: Exception) {
                _uiState.update { it.copy(cargando = false, error = e.message) }
            }
        }
    }

    fun reportar(usuarioId: Long, titulo: String, descripcion: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(cargando = true, error = null, creado = false) }
            try {
                RetrofitClient.ticketApi.crear(TicketRequest(usuarioId, titulo, descripcion))
                _uiState.update { it.copy(cargando = false, creado = true) }
                cargarMisTickets(usuarioId)
            } catch (e: Exception) {
                _uiState.update { it.copy(cargando = false, error = e.message) }
            }
        }
    }

    fun cargarMensajes(ticketId: Long) {
        viewModelScope.launch {
            try {
                val lista = RetrofitClient.ticketApi.mensajes(ticketId)
                _uiState.update { it.copy(mensajes = lista) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun enviarMensaje(ticketId: Long, autorId: Long, rol: String, texto: String) {
        viewModelScope.launch {
            try {
                RetrofitClient.ticketApi.enviarMensaje(
                    ticketId, MensajeRequest(autorId, rol, texto)
                )
                cargarMensajes(ticketId)
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun cerrar(ticketId: Long, resuelto: Boolean) {
        viewModelScope.launch {
            try {
                RetrofitClient.ticketApi.cerrar(ticketId, CerrarTicketRequest(resuelto))
                cargarParaGestor(null)
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }
}