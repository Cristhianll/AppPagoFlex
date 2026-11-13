package com.example.pagoflex.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pagoflex.data.remote.RegistroRequest
import com.example.pagoflex.data.remote.RetrofitClient
import com.example.pagoflex.validation.ValidacionesCuenta
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FormularioUiState(
    val nombre: String = "",
    val rut: String = "",
    val correo: String = "",
    val contrasena: String = "",
    val repetirContrasena: String = "",
    val contrasenaVisible: Boolean = false,
    val repetirVisible: Boolean = false,
    val pregunta1: String = "",
    val respuesta1: String = "",
    val pregunta2: String = "",
    val respuesta2: String = "",
    val recibirNoticias: Boolean = false,
    val aceptaTerminos: Boolean = false,
    val errorNombre: String? = null,
    val errorRut: String? = null,
    val errorCorreo: String? = null,
    val errorContrasena: String? = null,
    val errorRepetir: String? = null,
    val errorPregunta1: String? = null,
    val errorRespuesta1: String? = null,
    val errorPregunta2: String? = null,
    val errorRespuesta2: String? = null,
    val errorTerminos: String? = null,
    val errorRegistro: String? = null,
    val cargando: Boolean = false,
    val error: String? = null,
    val registroExitoso: Boolean = false
)

class FormularioViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(FormularioUiState())
    val uiState: StateFlow<FormularioUiState> = _uiState.asStateFlow()

    fun onNombreCambia(v: String) = _uiState.update {
        it.copy(nombre = v, errorNombre = null, error = null, errorRegistro = null)
    }

    fun onRutCambia(v: String) = _uiState.update {
        it.copy(rut = v, errorRut = null, error = null, errorRegistro = null)
    }

    fun onCorreoCambia(v: String) = _uiState.update {
        it.copy(correo = v, errorCorreo = null, error = null, errorRegistro = null)
    }

    fun onContrasenaCambia(v: String) = _uiState.update {
        it.copy(contrasena = v, errorContrasena = null, error = null, errorRegistro = null)
    }

    fun onRepetirCambia(v: String) = _uiState.update {
        it.copy(repetirContrasena = v, errorRepetir = null, errorRegistro = null)
    }

    fun onAlternarVisibilidad() = _uiState.update {
        it.copy(contrasenaVisible = !it.contrasenaVisible)
    }

    fun onAlternarRepetirVisibilidad() = _uiState.update {
        it.copy(repetirVisible = !it.repetirVisible)
    }

    fun onPregunta1Cambia(v: String) = _uiState.update {
        it.copy(pregunta1 = v, errorPregunta1 = null, errorRegistro = null)
    }

    fun onRespuesta1Cambia(v: String) = _uiState.update {
        it.copy(respuesta1 = v, errorRespuesta1 = null, errorRegistro = null)
    }

    fun onPregunta2Cambia(v: String) = _uiState.update {
        it.copy(pregunta2 = v, errorPregunta2 = null, errorRegistro = null)
    }

    fun onRespuesta2Cambia(v: String) = _uiState.update {
        it.copy(respuesta2 = v, errorRespuesta2 = null, errorRegistro = null)
    }

    fun onNoticiasCambia(v: Boolean) = _uiState.update {
        it.copy(recibirNoticias = v)
    }

    fun onTerminosCambia(v: Boolean) = _uiState.update {
        it.copy(aceptaTerminos = v, errorTerminos = null)
    }

    fun onErrorRegistro(mensaje: String) = _uiState.update {
        it.copy(cargando = false, registroExitoso = false, errorRegistro = mensaje, error = mensaje)
    }

    fun onLimpiar() {
        _uiState.value = FormularioUiState()
    }

    fun onRegistrar() {
        val s = _uiState.value

        val errNombre = ValidacionesCuenta.nombre(s.nombre)
        val errRut = ValidacionesCuenta.rut(s.rut)
        val errCorreo = ValidacionesCuenta.correo(s.correo)
        val errClave = ValidacionesCuenta.contrasena(s.contrasena)
        val errRepetir = ValidacionesCuenta.repetir(s.contrasena, s.repetirContrasena)
        val errPreg1 = if (s.pregunta1.isBlank()) "Selecciona una pregunta" else null
        val errResp1 = ValidacionesCuenta.respuesta(s.respuesta1)
        val errPreg2 = when {
            s.pregunta2.isBlank() -> "Selecciona una pregunta"
            s.pregunta2 == s.pregunta1 -> "Elige una pregunta distinta"
            else -> null
        }
        val errResp2 = ValidacionesCuenta.respuesta(s.respuesta2)
        val errTerminos = if (!s.aceptaTerminos) "Debes aceptar los términos y condiciones" else null

        val hayErrores = listOf(
            errNombre, errRut, errCorreo, errClave, errRepetir,
            errPreg1, errResp1, errPreg2, errResp2, errTerminos
        ).any { it != null }

        if (hayErrores) {
            _uiState.update {
                it.copy(
                    errorNombre = errNombre,
                    errorRut = errRut,
                    errorCorreo = errCorreo,
                    errorContrasena = errClave,
                    errorRepetir = errRepetir,
                    errorPregunta1 = errPreg1,
                    errorRespuesta1 = errResp1,
                    errorPregunta2 = errPreg2,
                    errorRespuesta2 = errResp2,
                    errorTerminos = errTerminos
                )
            }
            return
        }

        _uiState.update {
            it.copy(errorRegistro = null, error = null, registroExitoso = true)
        }
    }

    fun registrar() {
        val s = _uiState.value
        viewModelScope.launch {
            _uiState.update { it.copy(cargando = true, error = null, errorRegistro = null) }
            try {
                RetrofitClient.usuarioApi.registrar(
                    RegistroRequest(
                        nombre = s.nombre.trim(),
                        rut = s.rut.trim(),
                        correo = s.correo.trim(),
                        contrasena = s.contrasena,
                        pregunta1 = s.pregunta1,
                        respuesta1 = s.respuesta1,
                        pregunta2 = s.pregunta2,
                        respuesta2 = s.respuesta2
                    )
                )
                _uiState.update { it.copy(cargando = false, registroExitoso = true) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        cargando = false,
                        error = e.message ?: "Error al registrar",
                        errorRegistro = e.message ?: "Error al registrar"
                    )
                }
            }
        }
    }
}