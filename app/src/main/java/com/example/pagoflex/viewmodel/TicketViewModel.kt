package com.example.pagoflex.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pagoflex.data.remote.AdjuntoResponse
import com.example.pagoflex.data.remote.CerrarTicketRequest
import com.example.pagoflex.data.remote.MensajeRequest
import com.example.pagoflex.data.remote.MensajeResponse
import com.example.pagoflex.data.remote.RetrofitClient
import com.example.pagoflex.data.remote.TicketRequest
import com.example.pagoflex.data.remote.TicketResponse
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException

data class TicketUiState(
    val tickets: List<TicketResponse> = emptyList(),
    val ticketActual: TicketResponse? = null,
    val mensajes: List<MensajeResponse> = emptyList(),
    val adjuntos: List<AdjuntoResponse> = emptyList(),
    val cargando: Boolean = false,
    val enviando: Boolean = false,
    val error: String? = null,
    val aviso: String? = null,
    val ticketCreadoId: Long? = null
)

class TicketViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(TicketUiState())
    val uiState: StateFlow<TicketUiState> = _uiState.asStateFlow()

    fun limpiar() {
        _uiState.value = TicketUiState()
    }

    fun limpiarMensajes() {
        _uiState.update { it.copy(error = null, aviso = null) }
    }

    fun limpiarCreado() {
        _uiState.update { it.copy(ticketCreadoId = null) }
    }

    fun cerrarDetalle() {
        _uiState.update {
            it.copy(ticketActual = null, mensajes = emptyList(), adjuntos = emptyList())
        }
    }

    fun cargarTickets(estado: String? = null) {
        viewModelScope.launch {
            _uiState.update { it.copy(cargando = true, error = null) }
            try {
                val lista = RetrofitClient.ticketApi.listar(estado = estado)
                _uiState.update { it.copy(cargando = false, tickets = lista) }
            } catch (c: CancellationException) {
                throw c
            } catch (e: Exception) {
                _uiState.update { it.copy(cargando = false, error = mensajeError(e)) }
            }
        }
    }

    fun abrirTicket(ticketId: Long) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    cargando = true,
                    error = null,
                    ticketActual = null,
                    mensajes = emptyList(),
                    adjuntos = emptyList()
                )
            }
            try {
                refrescarActual(ticketId)
                _uiState.update { it.copy(cargando = false) }
            } catch (c: CancellationException) {
                throw c
            } catch (e: Exception) {
                _uiState.update { it.copy(cargando = false, error = mensajeError(e)) }
            }
        }
    }

    fun crear(
        titulo: String,
        descripcion: String,
        deudaId: Long?,
        referencia: String?,
        fotos: List<ByteArray>
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(enviando = true, error = null, aviso = null) }
            try {
                val creado = RetrofitClient.ticketApi.crear(
                    TicketRequest(titulo.trim(), descripcion.trim(), deudaId, referencia)
                )
                var fallidas = 0
                fotos.forEachIndexed { indice, bytes ->
                    try {
                        subirAdjuntoInterno(creado.id, bytes, indice)
                    } catch (c: CancellationException) {
                        throw c
                    } catch (_: Exception) {
                        fallidas++
                    }
                }
                val lista = RetrofitClient.ticketApi.listar()
                val aviso = if (fallidas == 0) {
                    "Tu reporte fue enviado. Un gestor lo revisará pronto."
                } else {
                    "Tu reporte fue enviado, pero $fallidas foto(s) no se pudieron subir."
                }
                _uiState.update {
                    it.copy(
                        enviando = false,
                        tickets = lista,
                        ticketCreadoId = creado.id,
                        aviso = aviso
                    )
                }
            } catch (c: CancellationException) {
                throw c
            } catch (e: Exception) {
                _uiState.update { it.copy(enviando = false, error = mensajeError(e)) }
            }
        }
    }

    fun responder(ticketId: Long, texto: String, fotos: List<ByteArray>) {
        viewModelScope.launch {
            _uiState.update { it.copy(enviando = true, error = null, aviso = null) }
            try {
                if (texto.isNotBlank()) {
                    RetrofitClient.ticketApi.enviarMensaje(ticketId, MensajeRequest(texto.trim()))
                }
                var fallidas = 0
                fotos.forEachIndexed { indice, bytes ->
                    try {
                        subirAdjuntoInterno(ticketId, bytes, indice)
                    } catch (c: CancellationException) {
                        throw c
                    } catch (_: Exception) {
                        fallidas++
                    }
                }
                refrescarActual(ticketId)
                _uiState.update {
                    it.copy(
                        enviando = false,
                        error = if (fallidas > 0) "$fallidas foto(s) no se pudieron subir." else null
                    )
                }
            } catch (c: CancellationException) {
                throw c
            } catch (e: Exception) {
                _uiState.update { it.copy(enviando = false, error = mensajeError(e)) }
            }
        }
    }

    fun cerrar(ticketId: Long, resuelto: Boolean) {
        viewModelScope.launch {
            _uiState.update { it.copy(enviando = true, error = null, aviso = null) }
            try {
                RetrofitClient.ticketApi.cerrar(ticketId, CerrarTicketRequest(resuelto))
                refrescarActual(ticketId)
                val aviso = if (resuelto) {
                    "Ticket cerrado como resuelto."
                } else {
                    "Ticket cerrado sin resolver."
                }
                _uiState.update { it.copy(enviando = false, aviso = aviso) }
            } catch (c: CancellationException) {
                throw c
            } catch (e: Exception) {
                _uiState.update { it.copy(enviando = false, error = mensajeError(e)) }
            }
        }
    }

    suspend fun descargarAdjunto(ticketId: Long, adjuntoId: Long): ByteArray? {
        return withContext(Dispatchers.IO) {
            try {
                RetrofitClient.ticketApi.descargarAdjunto(ticketId, adjuntoId).use { it.bytes() }
            } catch (c: CancellationException) {
                throw c
            } catch (_: Exception) {
                null
            }
        }
    }

    private suspend fun refrescarActual(ticketId: Long) {
        val ticket = RetrofitClient.ticketApi.obtener(ticketId)
        val mensajes = RetrofitClient.ticketApi.mensajes(ticketId)
        val adjuntos = RetrofitClient.ticketApi.adjuntos(ticketId)
        _uiState.update {
            it.copy(ticketActual = ticket, mensajes = mensajes, adjuntos = adjuntos)
        }
    }

    private suspend fun subirAdjuntoInterno(ticketId: Long, bytes: ByteArray, indice: Int) {
        val cuerpo = bytes.toRequestBody("image/jpeg".toMediaTypeOrNull())
        val parte = MultipartBody.Part.createFormData(
            "archivo",
            "prueba_${System.currentTimeMillis()}_$indice.jpg",
            cuerpo
        )
        RetrofitClient.ticketApi.subirAdjunto(ticketId, parte)
    }

    private fun mensajeError(e: Exception): String {
        if (e is HttpException) {
            return when (e.code()) {
                400 -> "Revisa los datos ingresados."
                401, 403 -> "No tienes permiso para realizar esta acción."
                404 -> "El ticket no existe."
                409 -> "El ticket ya está cerrado."
                else -> "El servidor respondió con un error (${e.code()})."
            }
        }
        return "No se pudo conectar con el servidor."
    }
}