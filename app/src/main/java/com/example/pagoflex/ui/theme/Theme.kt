package com.example.pagoflex.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

// Paleta oscura: fondo oscuro y textos claros para mejorar la lectura.
private val EsquemaOscuro = darkColorScheme(
    primary = PrimarioClaro,
    onPrimary = PrimarioOscuro,
    primaryContainer = PrimarioOscuro,
    onPrimaryContainer = Blanco,

    // Verde lima para las acciones, con texto oscuro encima.
    secondary = Acento,
    onSecondary = AcentoOscuro,

    // Turquesa como color de apoyo.
    tertiary = Secundario,
    onTertiary = Blanco,

    background = FondoOscuro,
    onBackground = TextoClaro,
    surface = SuperficieOscura,
    onSurface = TextoClaro
)

// Paleta clara: colores tomados del diseño de Figma.
private val EsquemaClaro = lightColorScheme(
    primary = Primario,
    onPrimary = Blanco,
    primaryContainer = PrimarioClaro,
    onPrimaryContainer = PrimarioOscuro,

    // Verde lima para el botón principal.
    secondary = Acento,
    onSecondary = AcentoOscuro,

    // Turquesa como color de apoyo.
    tertiary = Secundario,
    onTertiary = Blanco,

    background = FondoClaro,
    onBackground = TextoOscuro,
    surface = SuperficieClara,
    onSurface = TextoOscuro
)

// Formas redondeadas de los componentes Material 3.
private val Formas = Shapes(
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(20.dp)
)

@Composable
fun PagoFlexTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,

    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> EsquemaOscuro
        else -> EsquemaClaro
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Tipografia,
        shapes = Formas,
        content = content
    )
}