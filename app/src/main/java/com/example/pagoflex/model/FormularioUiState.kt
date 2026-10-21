package com.example.pagoflex.model

data class FormularioUiState(
    val nombre: String = "",
    val rut: String = "",
    val correo: String = "",
    val contrasena: String = "",
    val repetirContrasena: String = "",
    val pregunta1: String = "",
    val respuesta1: String = "",
    val pregunta2: String = "",
    val respuesta2: String = "",
    val recibirNoticias: Boolean = false,
    val aceptaTerminos: Boolean = false,
    val contrasenaVisible: Boolean = false,
    val repetirVisible: Boolean = false,
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
    val registroExitoso: Boolean = false
)