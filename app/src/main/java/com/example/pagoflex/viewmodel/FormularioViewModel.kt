package com.example.pagoflex.viewmodel


import androidx.lifecycle.ViewModel
import com.example.pagoflex.model.FormularioUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class FormularioViewModel: ViewModel(){

    private val _uiState = MutableStateFlow(FormularioUiState())

    val uiState: StateFlow<FormularioUiState> = _uiState.asStateFlow()

    private val regexCorreo = Regex("^[A-Za-z0-9._]+@[A-Za-z]+\\.[A-Za-z]{2,3}$")


    fun onNombreCambia(valor: String){
        _uiState.update { it.copy(nombre = valor,errorNombre = null, registroExitoso = false) }
    }
    fun onCorreoCambia(valor: String){
        _uiState.update { it.copy(correo = valor, errorCorreo = null, registroExitoso = false) }
    }
    fun onContrasenaCambia(valor: String){
        _uiState.update { it.copy(contrasena = valor, errorContrasena = null, registroExitoso = false) }
    }

    fun onRepetirCambia(valor: String) {
        _uiState.update { it.copy(repetirContrasena = valor, errorRepetir = null, registroExitoso = false) }
    }
    fun onAlternarVisibilidad(){
        _uiState.update { it.copy(contrasenaVisible = !it.contrasenaVisible) }
    }

    fun onAlternarRepetirVisibilidad() {
        _uiState.update { it.copy(repetirVisible = !it.repetirVisible) }
    }

    fun onNoticiasCambia(valor: Boolean){
        _uiState.update { it.copy(recibirNoticias = valor) }
    }
    fun onTerminosCambia(valor: Boolean){
        _uiState.update { it.copy(aceptaTerminos = valor, errorTerminos = null) }
    }

    private fun validarNombre(nombre: String): String? = when {
        nombre.isBlank() -> "El nombre es obligatorio"
        nombre.trim().length < 3 -> "El nombre debe tener al menos 3 caracteres"
        !nombre.all { it.isLetter() || it == ' ' } -> "El nombre solo puede tener letras"
        else -> null
    }

    private fun validarCorreo(correo: String): String? = when {
        correo.isBlank() -> "El correo es obligatorio"
        !regexCorreo.matches(correo.trim()) -> "Ingresa un correo válido"
        else -> null
    }

    private fun validarContrasena(clave: String): String? = when {
        clave.length < 8 -> "Mínimo 8 caracteres"
        !clave.any { it.isUpperCase() } -> "Debe tener al menos una mayúscula"
        !clave.any { it.isLowerCase() } -> "Debe tener al menos una minúscula"
        !clave.any { it.isDigit() } -> "Debe tener al menos un número"
        clave.all { it.isLetterOrDigit() } -> "Debe tener al menos un símbolo (ej: @ # !)"
        else -> null
    }
    private fun validarRepetir(clave: String, repetir: String): String? = when {
        repetir.isEmpty() -> "Repite la contraseña"
        clave != repetir -> "Las contraseñas no coinciden"
        else -> null
    }

    //botones
    //boton registrar
    fun onRegistrar() {
        val estado = _uiState.value
        val errorNombre = validarNombre(estado.nombre)
        val errorCorreo = validarCorreo(estado.correo)
        val errorContrasena = validarContrasena(estado.contrasena)
        val errorRepetir = validarRepetir(estado.contrasena, estado.repetirContrasena)
        val errorTerminos = if (!estado.aceptaTerminos) "Debes aceptar los términos" else null

        val hayErrores = listOf(errorNombre, errorCorreo, errorContrasena, errorRepetir, errorTerminos)
            .any { it != null }

        _uiState.update {
            it.copy(
                errorNombre = errorNombre,
                errorCorreo = errorCorreo,
                errorContrasena = errorContrasena,
                errorRepetir = errorRepetir,
                errorTerminos = errorTerminos,
                registroExitoso = !hayErrores
            )
        }
    }
    fun onLimpiar() {
        _uiState.value = FormularioUiState()
    }
}