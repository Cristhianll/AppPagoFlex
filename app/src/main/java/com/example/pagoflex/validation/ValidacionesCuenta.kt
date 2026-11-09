package com.example.pagoflex.validation

import android.util.Patterns

object ValidacionesCuenta {

    fun nombre(valor: String): String? {
        val texto = valor.trim()
        return when {
            texto.isEmpty() -> "Ingresa tu nombre completo"
            texto.length < 3 -> "El nombre es muy corto (mínimo 3 letras)"
            texto.any { it.isDigit() } -> "El nombre no puede contener números"
            !texto.all { it.isLetter() || it == ' ' } ->
                "El nombre solo puede tener letras y espacios (sin símbolos)"
            else -> null
        }
    }

    fun rut(valor: String): String? {
        val texto = valor.trim()
        if (texto.isEmpty()) return "Ingresa tu RUT"
        if (!texto.all { it.isDigit() || it in ".-kK" }) {
            return "El RUT solo puede tener números, puntos, guion y la letra K"
        }
        val limpio = texto.filter { it.isDigit() || it == 'k' || it == 'K' }
        return when {
            limpio.length < 8 -> "El RUT está incompleto: faltan dígitos (ej: 12.345.678-5)"
            limpio.length > 9 -> "El RUT tiene demasiados dígitos"
            !ValidacionesRut.esValido(texto) -> "El dígito verificador no corresponde a este RUT"
            else -> null
        }
    }

    fun correo(valor: String): String? {
        val texto = valor.trim()
        val dominio = texto.substringAfter('@', "")
        return when {
            texto.isEmpty() -> "Ingresa tu correo electrónico"
            texto.contains(' ') -> "El correo no puede tener espacios"
            !texto.contains('@') -> "Falta el símbolo @ en el correo"
            texto.count { it == '@' } > 1 -> "El correo solo puede tener un @"
            texto.substringBefore('@').isEmpty() ->
                "Falta el nombre antes del @ (ej: nombre@correo.cl)"
            dominio.isEmpty() -> "Falta el dominio después del @ (ej: @gmail.com)"
            !dominio.contains('.') -> "Falta el punto del dominio (ej: .com o .cl)"
            dominio.substringAfterLast('.').length < 2 ->
                "El final del dominio es muy corto (ej: .com, .cl)"
            !Patterns.EMAIL_ADDRESS.matcher(texto).matches() ->
                "El correo tiene caracteres no válidos"
            else -> null
        }
    }

    fun contrasena(clave: String): String? {
        if (clave.isEmpty()) return "Ingresa una contraseña"
        if (clave.contains(' ')) return "La contraseña no puede tener espacios"

        val faltan = mutableListOf<String>()
        if (!clave.any { it.isUpperCase() }) faltan += "una mayúscula"
        if (!clave.any { it.isLowerCase() }) faltan += "una minúscula"
        if (!clave.any { it.isDigit() }) faltan += "un número"
        if (clave.all { it.isLetterOrDigit() }) faltan += "un símbolo (ej: @ # !)"

        val problemas = mutableListOf<String>()
        if (clave.length < 8) problemas += "mínimo 8 caracteres (tiene ${clave.length})"
        if (faltan.isNotEmpty()) problemas += "le falta ${faltan.joinToString(", ")}"

        return if (problemas.isEmpty()) null
        else "Contraseña débil: ${problemas.joinToString(" y ")}"
    }

    fun repetir(clave: String, repetir: String): String? = when {
        repetir.isEmpty() -> "Confirma tu contraseña escribiéndola otra vez"
        clave != repetir -> "Las contraseñas no coinciden"
        else -> null
    }

    fun respuesta(valor: String): String? = when {
        valor.isBlank() -> "Escribe la respuesta a tu pregunta secreta"
        valor.trim().length < 2 -> "La respuesta es muy corta (mínimo 2 caracteres)"
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