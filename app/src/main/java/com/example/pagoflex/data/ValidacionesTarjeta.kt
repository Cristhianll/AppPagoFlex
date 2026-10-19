package com.example.pagoflex.data

import java.util.Calendar

object ValidacionesTarjeta {

    /**
     * Valida un vencimiento en formato MM/AA.
     *
     * La tarjeta sigue vigente durante todo el mes indicado.
     */
    fun vencimientoVigente(vencimiento: String): Boolean {
        if (
            !vencimiento.matches(
                Regex("[0-9]{2}/[0-9]{2}")
            )
        ) {
            return false
        }

        val mes = vencimiento
            .take(2)
            .toIntOrNull() ?: return false

        val anioCorto = vencimiento
            .takeLast(2)
            .toIntOrNull() ?: return false

        val anio = 2000 + anioCorto

        if (mes !in 1..12) {
            return false
        }

        val hoy = Calendar.getInstance()
        val anioActual = hoy.get(Calendar.YEAR)
        val mesActual = hoy.get(Calendar.MONTH) + 1

        return anio > anioActual ||
                (anio == anioActual && mes >= mesActual)
    }
}