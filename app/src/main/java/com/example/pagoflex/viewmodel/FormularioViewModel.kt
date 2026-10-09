package com.example.pagoflex.viewmodel


import androidx.lifecycle.ViewModel
import com.example.pagoflex.model.FormularioUiState
import com.example.pagoflex.model.NivelExperiencia
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class FormularioViewModel: ViewModel(){
    // variable de estado " State privado y mutable"
    private val _uiState = MutableStateFlow(FormularioUiState())
    // Publica de solo lectura
    val uiState: StateFlow<FormularioUiState> = _uiState.asStateFlow()

    //eventos de los campos
    fun onNombreCambia(valor: String){
        _uiState.update { it.copy(nombre = valor,errorNombre = null, registroExitoso = false) }
    }
    fun onCorreoCambia(valor: String){
        _uiState.update { it.copy(correo = valor, errorCorreo = null, registroExitoso = false) }
    }
    fun onContrasenaCambia(valor: String){
        _uiState.update { it.copy(contrasena = valor, errorContrasena = null, registroExitoso = false) }
    }
    fun onAlternarVisibilidad(){
        _uiState.update { it.copy(contrasenaVisible = !it.contrasenaVisible) }
    }
    //Eventos de los controles de seleccion
    fun onNivelSeleccionado(nivel : NivelExperiencia){
        _uiState.update { it.copy(nivel = nivel) }
    }
    fun onNoticiasCambia(valor: Boolean){
        _uiState.update { it.copy(recibirNoticias = valor) }
    }
    fun onTerminosCambia(valor: Boolean){
        _uiState.update { it.copy(recibirNoticias = valor, errorTerminos = null) }
    }
    //botones
    //boton registrar
    fun onRegistrar(){
        val estado = _uiState.value
        //validaciones de campos
        val errorNombre = if (estado.nombre.isBlank()) "El nombre es obligatorio" else null
        val errorCorreo = if(!estado.correo.contains(("@")) || !estado.correo.contains((".")))
            "ingresa un correo valido" else null //usar Regex
        val errorContrasena = if(estado.contrasena.length < 6)"Minimo 6 caracteres" else null
        val errorTerminos = if(!estado.aceptaTerminos) "Debes aceptar los terminos" else null
        //debe tener minimo 5 validaciones
        //verificamos si genre algun error
        val hayErrores = listOf(errorNombre,errorCorreo,errorContrasena,errorTerminos).any { it != null }
        _uiState.update { it.copy(
            errorNombre = errorNombre,
            errorCorreo = errorCorreo,
            errorContrasena = errorContrasena,
            registroExitoso = !hayErrores
        ) }
    }
    //boton limpiar
    fun onLimpiar(){
        _uiState.value = FormularioUiState()
    }
}