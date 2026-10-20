package com.example.pagoflex.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.pagoflex.ui.styles.EstilosBoton
import com.example.pagoflex.ui.theme.Dimens
import com.example.pagoflex.ui.theme.PagoFlexTheme

@Composable
fun BotonSecundario(
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    habilitado: Boolean = true,
    colores: ButtonColors = EstilosBoton.coloresSecundario(),
    borde: BorderStroke? = EstilosBoton.bordeSecundario(),
    forma: Shape = EstilosBoton.forma,
    altura: Dp? = Dimens.alturaBoton,
    relleno: PaddingValues = EstilosBoton.relleno,
    pesoTexto: FontWeight? = null
) {
    val modificador = if (altura != null) {
        modifier.height(altura)
    } else {
        modifier
    }

    OutlinedButton(
        onClick = onClick,
        modifier = modificador,
        enabled = habilitado,
        shape = forma,
        colors = colores,
        border = borde,
        contentPadding = relleno
    ) {
        Text(
            text = texto,
            fontWeight = pesoTexto
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun BotonSecundarioPreview() {
    PagoFlexTheme {
        BotonSecundario(
            texto = "Limpiar",
            onClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}