package com.example.pagoflex.validation

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
