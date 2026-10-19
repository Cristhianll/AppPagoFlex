package com.example.pagoflex.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.pagoflex.ui.components.BotonPrincipal
import com.example.pagoflex.ui.components.BotonSecundario
import java.text.NumberFormat
import java.util.Locale

// =====================================================
// PRESENTACIÓN INFORMATIVA REUTILIZABLE
// =====================================================

@Suppress("DEPRECATION")
@Composable
fun PantallaInformativaPago(
    titulo: String,
    texto: String,
    onVolver: () -> Unit,
    textoBotonVolver: String = "Volver al inicio",
    onVerHistorial: (() -> Unit)? = null
) {
    val colores = MaterialTheme.colorScheme

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = colores.background,
        contentColor = colores.onBackground
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = titulo,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            Text(
                text = texto,
                color = colores.onSurfaceVariant
            )

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            BotonPrincipal(
                texto = textoBotonVolver,
                onClick = onVolver,
                modifier = Modifier.fillMaxWidth(),
                altura = null,
                forma = ButtonDefaults.shape,
                colores = ButtonDefaults.buttonColors(
                    containerColor = colores.secondary,
                    contentColor = colores.onSecondary
                ),
                elevacion = ButtonDefaults.buttonElevation(),
                relleno = ButtonDefaults.ContentPadding
            )

            onVerHistorial?.let { accion ->
                BotonSecundario(
                    texto = "Ver historial",
                    onClick = accion,
                    modifier = Modifier.fillMaxWidth(),
                    altura = null,
                    forma = ButtonDefaults.outlinedShape,
                    colores = ButtonDefaults.outlinedButtonColors(),
                    borde = ButtonDefaults.outlinedButtonBorder,
                    relleno = ButtonDefaults.ContentPadding
                )
            }
        }
    }
}

// =====================================================
// RESULTADO DEL PAGO REGISTRADO
// Prepara los textos que antes construía PagoFlexApp.
// =====================================================

@Composable
fun ResultadoPagoScreen(
    pago: Map<String, String>?,
    onVolverInicio: () -> Unit,
    onVerHistorial: () -> Unit
) {
    val formato = NumberFormat.getCurrencyInstance(
        Locale("es", "CL")
    )

    val montoPagado = pago
        ?.get("monto")
        ?.toDoubleOrNull() ?: 0.0

    val textoResultado = buildString {
        append(
            pago?.get("empresa").orEmpty()
        )

        append("\n")

        append(
            pago?.get("concepto").orEmpty()
        )

        append("\n")

        append(
            formato.format(montoPagado)
        )

        val comprobante = pago?.get("comprobante")

        if (!comprobante.isNullOrBlank()) {
            append("\nReferencia: ")
            append(comprobante)
        }

        append(
            "\n\nRegistro de demostración. " +
                    "No se realiza un cargo bancario."
        )
    }

    PantallaInformativaPago(
        titulo = "Pago registrado",
        texto = textoResultado,
        onVolver = onVolverInicio,
        onVerHistorial = onVerHistorial
    )
}