package com.example.pagoflex.data.remote

// ============ USUARIOS / JWT ============
data class LoginRequest(val correo: String, val contrasena: String)

/** Respuesta de login con JWT */
data class LoginResponse(
    val token: String,
    val tipo: String,          // "Bearer"
    val id: Long,
    val nombre: String,
    val rut: String,
    val correo: String,
    val rol: String,
    val tieneFoto: Boolean = false
)

data class RegistroRequest(
    val nombre: String,
    val rut: String,
    val correo: String,
    val contrasena: String,
    val pregunta1: String? = null,
    val respuesta1: String? = null,
    val pregunta2: String? = null,
    val respuesta2: String? = null
)

data class UsuarioResponse(
    val id: Long,
    val nombre: String,
    val rut: String,
    val correo: String,
    val rol: String,
    val pregunta1: String? = null,
    val pregunta2: String? = null,
    val tieneFoto: Boolean = false
)

data class ActualizarUsuarioRequest(
    val nombre: String? = null,
    val rut: String? = null,
    val pregunta1: String? = null,
    val respuesta1: String? = null,
    val pregunta2: String? = null,
    val respuesta2: String? = null
)

data class CambiarContrasenaRequest(
    val contrasenaActual: String,
    val contrasenaNueva: String
)

data class RecuperarRequest(
    val correo: String,
    val respuesta1: String,
    val respuesta2: String,
    val nuevaContrasena: String
)

// ============ DEUDAS ============
data class DeudaRequest(
    val usuarioId: Long,
    val concepto: String,
    val empresa: String,
    val monto: Double,
    val vencimiento: String
)

data class DeudaResponse(
    val id: Long,
    val usuarioId: Long,
    val concepto: String,
    val empresa: String,
    val monto: Double,
    val vencimiento: String,
    val estado: String
)

// ============ PAGOS ============
data class PagoRequest(
    val deudaId: Long,
    val usuarioId: Long,
    val monto: Double,
    val metodo: String? = "TARJETA",
    val tarjetaId: Long? = null
)

data class PagoResponse(
    val id: Long,
    val deudaId: Long,
    val usuarioId: Long,
    val monto: Double,
    val fecha: String?,
    val metodo: String?,
    val tarjetaId: Long?
)

// ============ TARJETAS ============
data class TarjetaRequest(
    val usuarioId: Long,
    val ultimosDigitos: String,
    val titular: String,
    val vencimiento: String,
    val marca: String? = "Tarjeta",
    val principal: Boolean = false
)

data class TarjetaResponse(
    val id: Long,
    val usuarioId: Long,
    val ultimosDigitos: String,
    val titular: String,
    val vencimiento: String,
    val marca: String?,
    val principal: Boolean
)