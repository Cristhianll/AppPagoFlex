package com.example.pagoflex.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardElevation
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.pagoflex.ui.styles.EstilosTarjeta
import com.example.pagoflex.ui.theme.Dimens
import com.example.pagoflex.ui.theme.PagoFlexTheme

@Composable
fun TarjetaSeccion(
    titulo: String = "",
    modifier: Modifier = Modifier,
    forma: Shape = EstilosTarjeta.forma,
    colores: CardColors = EstilosTarjeta.colores(),
    elevacion: CardElevation = EstilosTarjeta.elevacion(),
    relleno: PaddingValues = PaddingValues(Dimens.espacioMedio),
    separacion: Dp = Dimens.espacioChico,
    estiloTitulo: TextStyle = MaterialTheme.typography.titleMedium,
    colorTitulo: Color = MaterialTheme.colorScheme.primary,
    alineacionHorizontal: Alignment.Horizontal = Alignment.Start,
    contenido: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = forma,
        colors = colores,
        elevation = elevacion
    ) {
        Column(
            modifier = Modifier.padding(relleno),
            verticalArrangement = Arrangement.spacedBy(separacion),
            horizontalAlignment = alineacionHorizontal
        ) {
            if (titulo.isNotEmpty()) {
                Text(
                    text = titulo,
                    style = estiloTitulo,
                    color = colorTitulo
                )
            }

            contenido()
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TarjetaSeccionPreview() {
    PagoFlexTheme {
        TarjetaSeccion(
            titulo = "Sobre mí",
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Cualquier contenido puede ir aquí dentro.")
        }
    }
}