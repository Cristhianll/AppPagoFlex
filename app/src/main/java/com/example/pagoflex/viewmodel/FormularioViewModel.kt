package com.example.pagoflex.viewmodel

import androidx.lifecycle.ViewModel
import com.example.pagoflex.data.ValidacionesCuenta
import com.example.pagoflex.model.FormularioUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class FormularioViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(FormularioUiState())
    val uiState: StateFlow<FormularioUiState> = _uiState.asStateFlow()

    fun onNombreCambia(valor: String) {
        _uiState.update { it.copy(nombre = valor, errorNombre = null, registroExitoso = false) }
    }

    fun onRutCambia(valor: String) {
        val filtrado = valor.filter { it.isDigit() || it in ".-kK" }.take(12)
        _uiState.update { it.copy(rut = filtrado, errorRut = null, registroExitoso = false) }
    }

    fun onCorreoCambia(valor: String) {
        _uiState.update { it.copy(correo = valor, errorCorreo = null, registroExitoso = false) }
    }

    fun onContrasenaCambia(valor: String) {
        _uiState.update { it.copy(contrasena = valor, errorContrasena = null, registroExitoso = false) }
    }

    fun onRepetirCambia(valor: String) {
        _uiState.update { it.copy(repetirContrasena = valor, errorRepetir = null, registroExitoso = false) }
    }

    fun onPregunta1Cambia(valor: String) {
        _uiState.update { it.copy(pregunta1 = valor, errorPregunta1 = null, registroExitoso = false) }
    }

    fun onRespuesta1Cambia(valor: String) {
        _uiState.update { it.copy(respuesta1 = valor, errorRespuesta1 = null, registroExitoso = false) }
    }

    fun onPregunta2Cambia(valor: String) {
        _uiState.update { it.copy(pregunta2 = valor, errorPregunta2 = null, registroExitoso = false) }
    }

    fun onRespuesta2Cambia(valor: String) {
        _uiState.update { it.copy(respuesta2 = valor, errorRespuesta2 = null, registroExitoso = false) }
    }

    fun onAlternarVisibilidad() {
        _uiState.update { it.copy(contrasenaVisible = !it.contrasenaVisible) }
    }

    fun onAlternarRepetirVisibilidad() {
        _uiState.update { it.copy(repetirVisible = !it.repetirVisible) }
    }

    fun onNoticiasCambia(valor: Boolean) {
        _uiState.update { it.copy(recibirNoticias = valor) }
    }

    fun onTerminosCambia(valor: Boolean) {
        _uiState.update { it.copy(aceptaTerminos = valor, errorTerminos = null) }
    }

    fun onRegistrar() {
        val e = _uiState.value

        val errorNombre = ValidacionesCuenta.nombre(e.nombre)
        val errorRut = ValidacionesCuenta.rut(e.rut)
        val errorCorreo = ValidacionesCuenta.correo(e.correo)
        val errorContrasena = ValidacionesCuenta.contrasena(e.contrasena)
        val errorRepetir = ValidacionesCuenta.repetir(e.contrasena, e.repetirContrasena)

        val errorPregunta1 = if (e.pregunta1.isBlank()) "Selecciona una pregunta" else null
        val errorPregunta2 = when {
            e.pregunta2.isBlank() -> "Selecciona una pregunta"
            e.pregunta2 == e.pregunta1 -> "Elige una pregunta distinta a la primera"
            else -> null
        }
        val errorRespuesta1 = ValidacionesCuenta.respuesta(e.respuesta1)
        val errorRespuesta2 = ValidacionesCuenta.respuesta(e.respuesta2)
        val errorTerminos = if (!e.aceptaTerminos) "Debes aceptar los términos" else null

        val hayErrores = listOf(
            errorNombre, errorRut, errorCorreo, errorContrasena, errorRepetir,
            errorPregunta1, errorPregunta2, errorRespuesta1, errorRespuesta2, errorTerminos
        ).any { it != null }

        _uiState.update {
            it.copy(
                errorNombre = errorNombre,
                errorRut = errorRut,
                errorCorreo = errorCorreo,
                errorContrasena = errorContrasena,
                errorRepetir = errorRepetir,
                errorPregunta1 = errorPregunta1,
                errorPregunta2 = errorPregunta2,
                errorRespuesta1 = errorRespuesta1,
                errorRespuesta2 = errorRespuesta2,
                errorTerminos = errorTerminos,
                errorRegistro = null,
                registroExitoso = !hayErrores
            )
        }
    }

    fun onErrorRegistro(mensaje: String) {
        _uiState.update { it.copy(errorRegistro = mensaje, registroExitoso = false) }
    }

    fun onLimpiar() {
        _uiState.value = FormularioUiState()
    }
}