package com.example.pagoflex.model

enum class NivelExperiencia(val etiqueta: String){
    PRINCIPIANTE("Principiante"),
    INTERMEDIO("Intermedio"),
    AVANZADO("Avanzado")
}

//Estructura de datos para manipular la informacion
data class FormularioUiState(
    //Variables para guardar la informacion de los campos de texto recibido por el usuario
    val nombre: String = "",
    val correo: String = "",
    val contrasena: String = "",
    val nivel: NivelExperiencia = NivelExperiencia.PRINCIPIANTE,
    val recibirNoticias: Boolean = false,
    val aceptaTerminos: Boolean = false,
    // para manipular el estado visual de la clave
    val contrasenaVisible: Boolean = false,
    // variable para manejar errores
    val errorNombre : String? = null,
    val errorCorreo: String? = null,
    val errorContrasena: String? = null,
    val errorTerminos: String? = null,
    //registro exitoso simulado
    val registroExitoso: Boolean = false
)