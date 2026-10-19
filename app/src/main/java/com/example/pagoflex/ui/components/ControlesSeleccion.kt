package com.example.pagoflex.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.pagoflex.ui.theme.Dimens
import com.example.pagoflex.ui.theme.PagoFlexTheme

@Composable
fun CasillaConTexto(
    texto: String,
    marcado: Boolean,
    onCambio: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    habilitado: Boolean = true,
    filaPulsable: Boolean = true,
    rellenoVertical: Dp = Dimens.espacioMini,
    separacionTexto: Dp = Dimens.espacioChico,
    estiloTexto: TextStyle = MaterialTheme.typography.bodyMedium,
    colorTexto: Color = Color.Unspecified,
    textoOcupaEspacio: Boolean = false
) {
    val modificadorFila = if (filaPulsable) {
        modifier
            .fillMaxWidth()
            .toggleable(
                value = marcado,
                enabled = habilitado,
                role = Role.Checkbox,
                onValueChange = onCambio
            )
    } else {
        modifier.fillMaxWidth()
    }

    Row(
        modifier = modificadorFila.padding(
            vertical = rellenoVertical
        ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = marcado,
            enabled = habilitado,
            onCheckedChange = if (filaPulsable) {
                null
            } else {
                onCambio
            }
        )

        val modificadorTexto = if (textoOcupaEspacio) {
            Modifier
                .weight(1f)
                .padding(start = separacionTexto)
        } else {
            Modifier.padding(start = separacionTexto)
        }

        Text(
            text = texto,
            style = estiloTexto,
            color = colorTexto,
            modifier = modificadorTexto
        )
    }
}

@Composable
fun InterruptorConTexto(
    texto: String,
    activado: Boolean,
    onCambio: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    habilitado: Boolean = true,
    filaPulsable: Boolean = true,
    rellenoVertical: Dp = Dimens.espacioMini,
    estiloTexto: TextStyle = MaterialTheme.typography.bodyMedium,
    colorTexto: Color = Color.Unspecified
) {
    val modificadorFila = if (filaPulsable) {
        modifier
            .fillMaxWidth()
            .toggleable(
                value = activado,
                enabled = habilitado,
                role = Role.Switch,
                onValueChange = onCambio
            )
    } else {
        modifier.fillMaxWidth()
    }

    Row(
        modifier = modificadorFila.padding(
            vertical = rellenoVertical
        ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = texto,
            style = estiloTexto,
            color = colorTexto,
            modifier = Modifier.weight(1f)
        )

        Switch(
            checked = activado,
            enabled = habilitado,
            onCheckedChange = if (filaPulsable) {
                null
            } else {
                onCambio
            }
        )
    }
}

@Composable
fun <T> GrupoRadio(
    opciones: List<T>,
    seleccionada: T,
    onSeleccionar: (T) -> Unit,
    textoDe: (T) -> String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.selectableGroup()
    ) {
        opciones.forEach { opcion ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = opcion == seleccionada,
                        onClick = {
                            onSeleccionar(opcion)
                        },
                        role = Role.RadioButton
                    )
                    .padding(vertical = Dimens.espacioMini),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = opcion == seleccionada,
                    onClick = null
                )

                Text(
                    text = textoDe(opcion),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(
                        start = Dimens.espacioChico
                    )
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ControlesSeleccionPreview() {
    PagoFlexTheme {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            CasillaConTexto(
                texto = "Acepto los términos",
                marcado = true,
                onCambio = {}
            )

            InterruptorConTexto(
                texto = "Recibir novedades",
                activado = false,
                onCambio = {}
            )

            GrupoRadio(
                opciones = listOf("Uno", "Dos", "Tres"),
                seleccionada = "Dos",
                onSeleccionar = {},
                textoDe = { it }
            )
        }
    }
}