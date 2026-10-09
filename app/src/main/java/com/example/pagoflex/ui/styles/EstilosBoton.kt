package com.example.pagoflex.ui.styles

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.pagoflex.ui.theme.Acento
import com.example.pagoflex.ui.theme.AcentoOscuro
import com.example.pagoflex.ui.theme.Dimens
import com.example.pagoflex.ui.theme.GrisDeshabilitado
import com.example.pagoflex.ui.theme.Blanco

object EstilosBoton {
    // Forma redondeada compartida por los botones.
    val forma = RoundedCornerShape(Dimens.radioBoton)

    // Espacio interno del texto y el icono.
    val relleno = PaddingValues(horizontal = 20.dp, vertical = 12.dp)

    // Botón principal: fondo verde lima y contenido verde oscuro.
    @Composable
    fun coloresPrincipal(): ButtonColors = ButtonDefaults.buttonColors(
        containerColor = Acento,
        contentColor = AcentoOscuro,
        disabledContainerColor = GrisDeshabilitado,
        disabledContentColor = Blanco
    )

    // Botón secundario: fondo transparente y contenido azul del tema.
    @Composable
    fun coloresSecundario() = ButtonDefaults.outlinedButtonColors(
        contentColor = MaterialTheme.colorScheme.primary
    )

    // Borde del botón secundario con el azul del tema.
    @Composable
    fun bordeSecundario(): BorderStroke = BorderStroke(
        Dimens.bordeBoton,
        MaterialTheme.colorScheme.primary
    )

    // Elevación y respuesta al presionar el botón principal.
    @Composable
    fun elevacion(): ButtonElevation = ButtonDefaults.buttonElevation(
        defaultElevation = Dimens.elevacionBoton,
        pressedElevation = 1.dp
    )
}

// Altura común para los botones.
fun Modifier.estiloAltoBoton(): Modifier =
    this.height(Dimens.alturaBoton)