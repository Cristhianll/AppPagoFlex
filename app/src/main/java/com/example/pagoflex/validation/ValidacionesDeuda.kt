package com.example.pagoflex.validation

import java.text.ParsePosition
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object ValidacionesDeuda {

    private fun formatoFecha(): SimpleDateFormat {
        return SimpleDateFormat("yyyy-MM-dd", Locale.US).apply {
            isLenient = false
            timeZone = TimeZone.getTimeZone("UTC")
        }
    }

    private fun convertirFecha(texto: String): Date? {
        if (!texto.matches(Regex("""[0-9]{4}-[0-9]{2}-[0-9]{2}"""))) {
            return null
        }

        val anio = texto.take(4).toIntOrNull() ?: return null

        if (anio !in 1..9999) {
            return null
        }

        val formato = formatoFecha()
        val posicion = ParsePosition(0)
        val fecha = formato.parse(texto, posicion) ?: return null

        return if (
            posicion.index == texto.length &&
            formato.format(fecha) == texto
        ) {
            fecha
        } else {
            null
        }
    }

    fun fechaValida(texto: String): Boolean {
        return convertirFecha(texto) != null
    }

    fun obtenerError(
        usuarioId: Int?,
        concepto: String,
        empresa: String,
        monto: Double?,
        vencimiento: String
    ): String? {
        return when {
            usuarioId == null || usuarioId <= 0 ->
                "Selecciona un usuario válido."

            concepto.isBlank() ->
                "Ingresa el concepto de la deuda."

            empresa.isBlank() ->
                "Ingresa la empresa o servicio."

            monto == null || !monto.isFinite() || monto <= 0 ->
                "Ingresa un monto válido mayor que cero."

            !fechaValida(vencimiento) ->
                "La fecha no existe. Usa AAAA-MM-DD."

            else -> null
        }
    }

    fun diasHasta(vencimiento: String): Long? {
        val fecha = convertirFecha(vencimiento) ?: return null

        val ahora = Calendar.getInstance()

        val hoy = Calendar.getInstance(
            TimeZone.getTimeZone("UTC")
        ).apply {
            clear()
            set(
                ahora.get(Calendar.YEAR),
                ahora.get(Calendar.MONTH),
                ahora.get(Calendar.DAY_OF_MONTH)
            )
        }

        return (fecha.time - hoy.timeInMillis) / 86_400_000L
    }

    fun textoVencimiento(vencimiento: String): String {
        val dias = diasHasta(vencimiento)
            ?: return "Fecha inválida"

        return when {
            dias < 0 -> {
                val cantidad = -dias
                if (cantidad == 1L) {
                    "Vencida hace 1 día"
                } else {
                    "Vencida hace $cantidad días"
                }
            }

            dias == 0L -> "Vence hoy"
            dias == 1L -> "Vence mañana"
            else -> "Vence en $dias días"
        }
    }
}