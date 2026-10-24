package com.example.pagoflex.validation

import android.util.Patterns

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
