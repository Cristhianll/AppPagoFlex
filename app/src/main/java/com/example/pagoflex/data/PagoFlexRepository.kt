package com.example.pagoflex.data

import android.content.Context

class PagoFlexRepository(context: Context) {

    private val db = PagoFlexDbHelper(context.applicationContext)

    val maxTarjetas: Int = PagoFlexDbHelper.MAX_TARJETAS

    fun iniciarSesion(
        correo: String,
        clave: String
    ): Map<String, String>? {
        return db.iniciarSesion(correo, clave)
    }

    fun registrarUsuario(
        nombre: String,
        rut: String,
        correo: String,
        clave: String,
        pregunta1: String,
        respuesta1: String,
        pregunta2: String,
        respuesta2: String
    ): Boolean {
        return db.registrarUsuario(
            nombre, rut, correo, clave,
            pregunta1, respuesta1, pregunta2, respuesta2
        ) == null
    }

    fun obtenerPreguntas(correo: String): List<String>? {
        return db.obtenerPreguntas(correo)
    }

    fun restablecerClave(
        correo: String,
        respuesta1: String,
        respuesta2: String,
        nuevaClave: String
    ): Boolean {
        return db.restablecerClave(correo, respuesta1, respuesta2, nuevaClave)
    }

    fun actualizarPerfil(
        usuarioId: Int,
        nombre: String,
        rut: String,
        correo: String,
        pregunta1: String = "",
        respuesta1: String = "",
        pregunta2: String = "",
        respuesta2: String = ""
    ): String? {
        return db.actualizarPerfil(
            usuarioId, nombre, rut, correo,
            pregunta1, respuesta1, pregunta2, respuesta2
        )
    }

    fun guardarFoto(usuarioId: Int, fotoBytes: ByteArray): Boolean {
        return db.guardarFoto(usuarioId, fotoBytes)
    }

    fun obtenerFoto(usuarioId: Int): ByteArray? {
        return db.obtenerFoto(usuarioId)
    }

    fun listarTarjetasUsuario(usuarioId: Int): List<Map<String, String>> {
        return db.listarTarjetas(usuarioId)
    }

    fun agregarTarjeta(
        usuarioId: Int,
        ultimosDigitos: String,
        titular: String,
        vencimiento: String,
        marca: String
    ): Boolean {
        return db.agregarTarjeta(usuarioId, ultimosDigitos, titular, vencimiento, marca) == null
    }

    fun marcarTarjetaPrincipal(tarjetaId: Int, usuarioId: Int): Boolean {
        return db.marcarTarjetaPrincipal(tarjetaId, usuarioId)
    }

    fun eliminarTarjeta(tarjetaId: Int, usuarioId: Int): Boolean {
        return db.eliminarTarjeta(tarjetaId, usuarioId)
    }

    fun listarUsuarios(): List<Map<String, String>> {
        return db.listarUsuarios()
    }

    fun listarClientes(): List<Map<String, String>> {
        return db.listarUsuarios().filter { it["rol"] == "USUARIO" }
    }

    fun crearDeuda(
        usuarioId: Int,
        concepto: String,
        empresa: String,
        monto: Double,
        vencimiento: String
    ): Boolean {
        return db.crearDeuda(usuarioId, concepto, empresa, monto, vencimiento)
    }

    fun actualizarDeuda(
        deudaId: Int,
        usuarioId: Int,
        concepto: String,
        empresa: String,
        monto: Double,
        vencimiento: String
    ): Boolean {
        return db.actualizarDeuda(deudaId, usuarioId, concepto, empresa, monto, vencimiento)
    }

    fun eliminarDeuda(deudaId: Int): Boolean {
        return db.eliminarDeuda(deudaId)
    }

    fun listarDeudas(usuarioId: Int? = null): List<Map<String, String>> {
        return db.listarDeudas(usuarioId)
    }

    fun pagarDeuda(deudaId: Int, usuarioId: Int, tarjetaId: Int): Boolean {
        return db.pagarDeuda(deudaId, usuarioId, tarjetaId)
    }

    fun listarPagos(usuarioId: Int): List<Map<String, String>> {
        return db.listarPagos(usuarioId)
    }
    fun verificarRespuestas(correo: String, respuesta1: String, respuesta2: String): Boolean {
        return try {
            db.verificarRespuestas(correo, respuesta1, respuesta2)
        } catch (_: Exception) {
            false
        }
    }
}