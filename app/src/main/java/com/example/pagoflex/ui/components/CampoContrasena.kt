package com.example.pagoflex.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pagoflex.R
import com.example.pagoflex.ui.styles.EstilosCampo
import com.example.pagoflex.ui.theme.Dimens
import com.example.pagoflex.ui.theme.PagoFlexTheme

@Composable
fun CampoContrasena(
    valor: String,
    onValorCambia: (String) -> Unit,
    etiqueta: String,
    visible: Boolean,
    onAlternarVisible: () -> Unit,
    modifier: Modifier = Modifier,
    error: String? = null,
    habilitado: Boolean = true,
    mostrarCandado: Boolean = true,
    usarTextoVisibilidad: Boolean = false,
    forma: Shape = EstilosCampo.forma,
    colores: TextFieldColors = EstilosCampo.colores(),
    reservarEspacioError: Boolean = true,
    mostrarMensajeError: Boolean = true
) {
    val mostrarApoyo = reservarEspacioError ||
            (mostrarMensajeError && error != null)

    OutlinedTextField(
        value = valor,
        onValueChange = onValorCambia,
        modifier = modifier.fillMaxWidth(),
        enabled = habilitado,

        label = {
            Text(etiqueta)
        },

        leadingIcon = if (mostrarCandado) {
            {
                Icon(
                    painter = painterResource(
                        id = R.drawable.ic_candado
                    ),
                    contentDescription = null,
                    modifier = Modifier.size(Dimens.tamanoIcono)
                )
            }
        } else {
            null
        },

        trailingIcon = {
            if (usarTextoVisibilidad) {
                TextButton(
                    onClick = onAlternarVisible,
                    enabled = habilitado
                ) {
                    Text(
                        text = if (visible) {
                            "Ocultar"
                        } else {
                            "Ver"
                        }
                    )
                }
            } else {
                IconButton(
                    onClick = onAlternarVisible,
                    enabled = habilitado
                ) {
                    Icon(
                        painter = painterResource(
                            id = if (visible) {
                                R.drawable.ic_ojo_tachado
                            } else {
                                R.drawable.ic_ojo
                            }
                        ),
                        contentDescription = stringResource(
                            id = if (visible) {
                                R.string.cd_ocultar_contrasena
                            } else {
                                R.string.cd_mostrar_contrasena
                            }
                        ),
                        modifier = Modifier.size(Dimens.tamanoIcono)
                    )
                }
            }
        },

        visualTransformation = if (visible) {
            VisualTransformation.None
        } else {
            PasswordVisualTransformation()
        },

        isError = error != null,

        supportingText = if (mostrarApoyo) {
            {
                if (mostrarMensajeError && error != null) {
                    Text(error)
                }
            }
        } else {
            null
        },

        singleLine = true,

        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password
        ),

        shape = forma,
        colors = colores
    )
}

@Preview(showBackground = true)
@Composable
private fun CampoContrasenaPreview() {
    PagoFlexTheme {
        CampoContrasena(
            valor = "secreto",
            onValorCambia = {},
            etiqueta = "Contraseña",
            visible = false,
            onAlternarVisible = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}