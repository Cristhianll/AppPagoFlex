package com.example.pagoflex.ui.theme

import android.content.Context
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

private val EsquemaOscuro = darkColorScheme(
    primary = PrimarioClaro,
    onPrimary = PrimarioOscuro,
    primaryContainer = PrimarioOscuro,
    onPrimaryContainer = TextoClaro,

    secondary = Acento,
    onSecondary = AcentoOscuro,
    secondaryContainer = AcentoOscuro,
    onSecondaryContainer = Acento,

    tertiary = Secundario,
    onTertiary = PrimarioOscuro,

    background = FondoOscuro,
    onBackground = TextoClaro,

    surface = SuperficieOscura,
    onSurface = TextoClaro,
    surfaceVariant = Primario,
    onSurfaceVariant = PrimarioClaro,

    outline = GrisDeshabilitado,

    error = androidx.compose.ui.graphics.Color(0xFFFFB4AB),
    onError = androidx.compose.ui.graphics.Color(0xFF690005)
)

private val EsquemaClaro = lightColorScheme(
    primary = Primario,
    onPrimary = Blanco,
    primaryContainer = PrimarioClaro,
    onPrimaryContainer = PrimarioOscuro,

    secondary = Acento,
    onSecondary = AcentoOscuro,
    secondaryContainer = Acento,
    onSecondaryContainer = AcentoOscuro,

    tertiary = Secundario,
    onTertiary = Blanco,

    background = FondoClaro,
    onBackground = TextoOscuro,

    surface = SuperficieClara,
    onSurface = TextoOscuro,
    surfaceVariant = PrimarioClaro,
    onSurfaceVariant = Primario,

    outline = androidx.compose.ui.graphics.Color(0xFF74777F),

    error = ErrorPagoFlex,
    onError = Blanco
)

private val Formas = Shapes(
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(20.dp)
)

data class ControlTemaPagoFlex(
    val oscuro: Boolean,
    val sigueSistema: Boolean,
    val cambiarModoOscuro: (Boolean) -> Unit,
    val usarTemaDelSistema: () -> Unit
)

val LocalControlTemaPagoFlex = staticCompositionLocalOf<ControlTemaPagoFlex> {
    error("La pantalla debe estar dentro de PagoFlexTheme.")
}

@Composable
fun PagoFlexTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current.applicationContext

    val preferencias = remember(context) {
        context.getSharedPreferences(
            "pagoflex_apariencia",
            Context.MODE_PRIVATE
        )
    }

    // null significa que todavía se utiliza el tema del sistema.
    var eleccionManual by remember(preferencias) {
        mutableStateOf<Boolean?>(
            if (preferencias.contains("modo_oscuro")) {
                preferencias.getBoolean("modo_oscuro", false)
            } else {
                null
            }
        )
    }

    val oscuroActual = eleccionManual ?: darkTheme

    val controlTema = ControlTemaPagoFlex(
        oscuro = oscuroActual,
        sigueSistema = eleccionManual == null,

        cambiarModoOscuro = { activar ->
            preferencias.edit()
                .putBoolean("modo_oscuro", activar)
                .apply()

            eleccionManual = activar
        },

        usarTemaDelSistema = {
            preferencias.edit()
                .remove("modo_oscuro")
                .apply()

            eleccionManual = null
        }
    )

    // Se conserva dynamicColor en la firma por compatibilidad,
    // pero se usan siempre los colores de identidad de PagoFlex.
    CompositionLocalProvider(
        LocalControlTemaPagoFlex provides controlTema
    ) {
        MaterialTheme(
            colorScheme = if (oscuroActual) {
                EsquemaOscuro
            } else {
                EsquemaClaro
            },
            typography = Tipografia,
            shapes = Formas,
            content = content
        )
    }
}