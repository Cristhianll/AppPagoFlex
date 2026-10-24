package com.example.pagoflex.validation

import java.util.Locale

object ValidacionesRut {

    fun limpiar(texto: String): String =
        texto.filter { it.isDigit() || it == 'k' || it == 'K' }
            .uppercase(Locale.ROOT)

    fun esValido(texto: String): Boolean {
        val limpio = limpiar(texto)
        if (limpio.length !in 8..9) return false

        val cuerpo = limpio.dropLast(1)
        if (!cuerpo.all { it.isDigit() }) return false

        var suma = 0
        var multiplicador = 2
        for (c in cuerpo.reversed()) {
            suma += (c - '0') * multiplicador
            multiplicador = if (multiplicador == 7) 2 else multiplicador + 1
        }

        val resto = 11 - (suma % 11)
        val esperado = when (resto) {
            11 -> '0'
            10 -> 'K'
            else -> '0' + resto
        }
        return limpio.last() == esperado
    }

    fun normalizar(texto: String): String {
        val limpio = limpiar(texto)
        if (limpio.length < 2) return limpio
        return limpio.dropLast(1) + "-" + limpio.last()
    }

    fun formatear(texto: String): String {
        val limpio = limpiar(texto)
        if (limpio.length < 2) return texto
        val cuerpo = limpio.dropLast(1)
        val conPuntos = cuerpo.reversed().chunked(3).joinToString(".").reversed()
        return "$conPuntos-${limpio.last()}"
    }
}
