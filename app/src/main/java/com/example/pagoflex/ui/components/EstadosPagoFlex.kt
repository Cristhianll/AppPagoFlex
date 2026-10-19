package com.example.pagoflex.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MensajePagoFlex(
    texto: String,
    onCerrar: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colores = MaterialTheme.colorScheme

    Surface(
        modifier = modifier,
        color = colores.errorContainer,
        contentColor = colores.onErrorContainer
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = texto,
                modifier = Modifier.weight(1f)
            )

            TextButton(
                onClick = onCerrar,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = colores.onErrorContainer
                )
            ) {
                Text("Cerrar")
            }
        }
    }
}

@Composable
fun CargaPagoFlex(
    texto: String,
    modifier: Modifier = Modifier
) {
    val colores = MaterialTheme.colorScheme

    Surface(
        modifier = modifier.fillMaxSize(),
        color = colores.background,
        contentColor = colores.onBackground
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()

                Spacer(
                    modifier = Modifier.height(16.dp)
                )

                Text(text = texto)
            }
        }
    }
}