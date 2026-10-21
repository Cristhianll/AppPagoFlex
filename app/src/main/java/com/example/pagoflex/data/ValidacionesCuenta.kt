package com.example.pagoflex.data

import android.util.Patterns
import java.text.Normalizer
import java.util.Locale

object PreguntasSecretas {
    val lista = listOf(
        "¿Cuál es el nombre de tu primera mascota?",
        "¿Cuál es el nombre de tu primer amor?",
        "¿En qué ciudad nació tu madre?",
        "¿Cuál es el nombre de tu mejor amigo de la infancia?",
        "¿Cuál fue el nombre de tu primer colegio?",
        "¿Cuál es tu comida favorita?"
    )


    fun normalizarRespuesta(texto: String): String {
        val sinTildes = Normalizer
            .normalize(texto.trim().lowercase(Locale.ROOT), Normalizer.Form.NFD)
            .replace(Regex("\\p{M}+"), "")
        return sinTildes.replace(Regex("\\s+"), " ")
    }
}

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

object ValidacionesCuenta {

    fun nombre(valor: String): String? = when {
        valor.isBlank() -> "El nombre es obligatorio"
        valor.trim().length < 3 -> "El nombre debe tener al menos 3 caracteres"
        !valor.all { it.isLetter() || it == ' ' } -> "El nombre solo puede tener letras"
        else -> null
    }

    fun rut(valor: String): String? = when {
        valor.isBlank() -> "El RUT es obligatorio"
        !ValidacionesRut.esValido(valor) -> "Ingresa un RUT válido (ej: 12.345.678-5)"
        else -> null
    }

    fun correo(valor: String): String? = when {
        valor.isBlank() -> "El correo es obligatorio"
        !Patterns.EMAIL_ADDRESS.matcher(valor.trim()).matches() -> "Ingresa un correo válido"
        else -> null
    }

    fun contrasena(clave: String): String? = when {
        clave.length < 8 -> "Mínimo 8 caracteres"
        !clave.any { it.isUpperCase() } -> "Debe tener al menos una mayúscula"
        !clave.any { it.isLowerCase() } -> "Debe tener al menos una minúscula"
        !clave.any { it.isDigit() } -> "Debe tener al menos un número"
        clave.all { it.isLetterOrDigit() } -> "Debe tener al menos un símbolo (ej: @ # !)"
        else -> null
    }

    fun repetir(clave: String, repetir: String): String? = when {
        repetir.isEmpty() -> "Repite la contraseña"
        clave != repetir -> "Las contraseñas no coinciden"
        else -> null
    }

    fun respuesta(valor: String): String? = when {
        valor.isBlank() -> "Ingresa tu respuesta"
        valor.trim().length < 2 -> "La respuesta es muy corta"
        else -> null
    }

    fun preguntas(p1: String, r1: String, p2: String, r2: String): String? = when {
        p1.isBlank() || p2.isBlank() -> "Selecciona las dos preguntas secretas."
        p1 == p2 -> "Elige dos preguntas distintas."
        respuesta(r1) != null || respuesta(r2) != null ->
            "Ingresa una respuesta válida para cada pregunta."
        else -> null
    }
}