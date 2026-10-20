package com.example.pagoflex.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pagoflex.R
import com.example.pagoflex.ui.styles.EstilosCampo
import com.example.pagoflex.ui.theme.Dimens
import com.example.pagoflex.ui.theme.PagoFlexTheme

@Composable
fun CampoTexto(
    valor: String,
    onValorCambia: (String) -> Unit,
    etiqueta: String,
    @DrawableRes icono: Int? = null,
    modifier: Modifier = Modifier,
    error: String? = null,
    tipoTeclado: KeyboardType = KeyboardType.Text,
    habilitado: Boolean = true,
    ayuda: String? = null,
    placeholder: String? = null,
    forma: Shape = EstilosCampo.forma,
    colores: TextFieldColors = EstilosCampo.colores(),
    accionTeclado: ImeAction = ImeAction.Next,
    reservarEspacioAyuda: Boolean = true,
    mostrarMensajeError: Boolean = true
) {
    val textoApoyo = if (mostrarMensajeError) {
        error ?: ayuda
    } else {
        ayuda
    }

    OutlinedTextField(
        value = valor,
        onValueChange = onValorCambia,
        modifier = modifier.fillMaxWidth(),
        enabled = habilitado,

        label = {
            Text(etiqueta)
        },

        placeholder = if (placeholder != null) {
            {
                Text(placeholder)
            }
        } else {
            null
        },

        leadingIcon = if (icono != null) {
            {
                Icon(
                    painter = painterResource(id = icono),
                    contentDescription = null,
                    modifier = Modifier.size(Dimens.tamanoIcono)
                )
            }
        } else {
            null
        },

        isError = error != null,

        supportingText = if (
            textoApoyo != null || reservarEspacioAyuda
        ) {
            {
                if (textoApoyo != null) {
                    Text(textoApoyo)
                }
            }
        } else {
            null
        },

        singleLine = true,

        keyboardOptions = KeyboardOptions(
            keyboardType = tipoTeclado,
            imeAction = accionTeclado
        ),

        shape = forma,
        colors = colores
    )
}

@Preview(showBackground = true)
@Composable
private fun CampoTextoPreview() {
    PagoFlexTheme {
        CampoTexto(
            valor = "ana@",
            onValorCambia = {},
            etiqueta = "Correo electrónico",
            icono = R.drawable.ic_correo,
            error = "Ingresa un correo válido",
            tipoTeclado = KeyboardType.Email,
            modifier = Modifier.padding(16.dp)
        )
    }
}