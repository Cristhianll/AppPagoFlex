package com.example.pagoflex.data

import android.content.Context

class SesionPreferencias(context: Context) {
    private val prefs = context.getSharedPreferences("pagoflex_sesion", Context.MODE_PRIVATE)

    fun guardar(token: String, id: Long, nombre: String, correo: String, rol: String) {
        prefs.edit()
            .putString("token", token)
            .putLong("id", id)
            .putString("nombre", nombre)
            .putString("correo", correo)
            .putString("rol", rol)
            .apply()
    }

    fun token(): String? = prefs.getString("token", null)
    fun id(): Long = prefs.getLong("id", -1L)
    fun nombre(): String? = prefs.getString("nombre", null)
    fun correo(): String? = prefs.getString("correo", null)
    fun rol(): String? = prefs.getString("rol", null)
    fun haySesion(): Boolean = !token().isNullOrBlank()

    fun limpiar() {
        prefs.edit().clear().apply()
    }
}