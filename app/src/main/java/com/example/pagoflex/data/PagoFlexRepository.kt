
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
        if (nombre.isBlank() || correo.isBlank() || clave.length < 6) {
            return false
        }
        return db.registrarUsuario(nombre, correo, clave)
    }

    fun listarUsuarios(): List<Map<String, String>> {
        return db.listarUsuarios()
    }

    fun crearDeuda(
        usuarioId: Int,
        concepto: String,
        empresa: String,
        monto: Double,
        vencimiento: String
    ): Boolean {
        return db.crearDeuda(
            usuarioId, concepto, empresa, monto, vencimiento
        )
    }

    fun listarDeudas(
        usuarioId: Int? = null
    ): List<Map<String, String>> {
        return db.listarDeudas(usuarioId)
    }

    fun pagarDeuda(
        deudaId: Int,
        usuarioId: Int
    ): Boolean {
        return db.pagarDeuda(deudaId, usuarioId)
    }

    fun listarPagos(
        usuarioId: Int
    ): List<Map<String, String>> {
        return db.listarPagos(usuarioId)
    }
}
