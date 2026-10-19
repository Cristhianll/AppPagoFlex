package com.example.pagoflex.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.pagoflex.R
import com.example.pagoflex.ui.styles.EstilosBoton
import com.example.pagoflex.ui.theme.Dimens
import com.example.pagoflex.ui.theme.PagoFlexTheme

@Composable
fun BotonPrincipal(
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    habilitado: Boolean = true,
    @DrawableRes icono: Int? = null,
    colores: ButtonColors = EstilosBoton.coloresPrincipal(),
    forma: Shape = EstilosBoton.forma,
    altura: Dp? = Dimens.alturaBoton,
    elevacion: ButtonElevation? = EstilosBoton.elevacion(),
    relleno: PaddingValues = EstilosBoton.relleno,
    pesoTexto: FontWeight? = null
) {
    val modificador = if (altura != null) {
        modifier.height(altura)
    } else {
        modifier
    }

    Button(
        onClick = onClick,
        modifier = modificador,
        enabled = habilitado,
        shape = forma,
        colors = colores,
        elevation = elevacion,
        contentPadding = relleno
    ) {
        if (icono != null) {
            Icon(
                painter = painterResource(id = icono),
                contentDescription = null,
                modifier = Modifier.size(ButtonDefaults.IconSize)
            )

            Spacer(
                modifier = Modifier.size(
                    ButtonDefaults.IconSpacing
                )
            )
        }

        Text(
            text = texto,
            fontWeight = pesoTexto
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun BotonPrincipalPreview() {
    PagoFlexTheme {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            BotonPrincipal(
                texto = "Registrarme",
                onClick = {},
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.size(8.dp))

            BotonPrincipal(
                texto = "Con ícono",
                onClick = {},
                icono = R.drawable.ic_check,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.size(8.dp))

            BotonPrincipal(
                texto = "Deshabilitado",
                onClick = {},
                habilitado = false,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}