package com.example.pagoflex.data

import android.content.Context

class PagoFlexRepository(context: Context) {

    private val db = PagoFlexDbHelper(context.applicationContext)

    fun iniciarSesion(
        correo: String,
        clave: String
    ): Map<String, String>? {
        return db.iniciarSesion(correo, clave)
    }

    fun registrarUsuario(
        nombre: String,
        correo: String,
        clave: String
    ): Boolean {
        return db.registrarUsuario(
            nombre,
            correo,
            clave
        )
    }

    fun listarUsuarios(): List<Map<String, String>> {
        return db.listarUsuarios()
    }

    fun listarClientes(): List<Map<String, String>> {
        return db.listarUsuarios().filter {
            it["rol"] == "USUARIO"
        }
    }

    fun crearDeuda(
        usuarioId: Int,
        concepto: String,
        empresa: String,
        monto: Double,
        vencimiento: String
    ): Boolean {
        return db.crearDeuda(
            usuarioId,
            concepto,
            empresa,
            monto,
            vencimiento
        )
    }

    fun actualizarDeuda(
        deudaId: Int,
        usuarioId: Int,
        concepto: String,
        empresa: String,
        monto: Double,
        vencimiento: String
    ): Boolean {
        return db.actualizarDeuda(
            deudaId,
            usuarioId,
            concepto,
            empresa,
            monto,
            vencimiento
        )
    }

    fun eliminarDeuda(deudaId: Int): Boolean {
        return db.eliminarDeuda(deudaId)
    }

    fun listarDeudas(
        usuarioId: Int? = null
    ): List<Map<String, String>> {
        return db.listarDeudas(usuarioId)
    }

    fun listarDeudasPendientes(
        usuarioId: Int
    ): List<Map<String, String>> {
        return db.listarDeudas(usuarioId).filter {
            it["estado"] == "PENDIENTE"
        }
    }

    fun pagarDeuda(
        deudaId: Int,
        usuarioId: Int
    ): Boolean {
        return db.pagarDeuda(
            deudaId,
            usuarioId
        )
    }

    fun listarPagos(
        usuarioId: Int
    ): List<Map<String, String>> {
        return db.listarPagos(usuarioId)
    }
}