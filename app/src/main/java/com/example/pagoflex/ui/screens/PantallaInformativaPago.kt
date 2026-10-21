package com.example.pagoflex.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pagoflex.ui.components.BotonPrincipal
import com.example.pagoflex.ui.components.BotonSecundario
import java.text.NumberFormat
import java.util.Locale

@Composable
fun ResultadoPagoScreen(
    pago: Map<String, String>?,
    onVolverInicio: () -> Unit,
    onVerHistorial: () -> Unit
) {
    val colores = MaterialTheme.colorScheme
    val formato = NumberFormat.getCurrencyInstance(Locale("es", "CL"))

    val empresa = pago?.get("empresa").orEmpty().ifBlank { "Servicio" }
    val concepto = pago?.get("concepto").orEmpty().ifBlank { "Pago de cuenta" }
    val montoPagado = pago?.get("monto")?.toDoubleOrNull() ?: 0.0
    val comprobante = pago?.get("comprobante").orEmpty()
    val tarjeta = pago?.get("tarjeta").orEmpty()

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
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // Tarjeta de Comprobante / Voucher
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = colores.surface,
                    contentColor = colores.onSurface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Ícono de éxito animado / destacado
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(colores.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "✓",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = colores.onPrimaryContainer
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "¡Pago Exitoso!",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = colores.onSurface
                    )

                    Text(
                        text = empresa,
                        fontSize = 16.sp,
                        color = colores.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Monto grande
                    Text(
                        text = formato.format(montoPagado),
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = colores.primary
                    )

                    Spacer(modifier = Modifier.height(20.dp))
                    HorizontalDivider(color = colores.outlineVariant)
                    Spacer(modifier = Modifier.height(16.dp))

                    // Detalles en filas organizadas
                    DetalleFilaVoucher(etiqueta = "Concepto", valor = concepto)

                    if (tarjeta.isNotBlank()) {
                        DetalleFilaVoucher(etiqueta = "Método de pago", valor = tarjeta)
                    }

                    if (comprobante.isNotBlank()) {
                        DetalleFilaVoucher(etiqueta = "N° Referencia", valor = comprobante)
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Botones de acción inferiores
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                BotonPrincipal(
                    texto = "Volver al inicio",
                    onClick = onVolverInicio,
                    modifier = Modifier.fillMaxWidth(),
                    altura = 50.dp,
                    forma = RoundedCornerShape(14.dp),
                    colores = ButtonDefaults.buttonColors(
                        containerColor = colores.primary,
                        contentColor = colores.onPrimary
                    ),
                    pesoTexto = FontWeight.Bold
                )

                BotonSecundario(
                    texto = "Ver en el historial",
                    onClick = onVerHistorial,
                    modifier = Modifier.fillMaxWidth(),
                    altura = 50.dp,
                    forma = RoundedCornerShape(14.dp),
                    colores = ButtonDefaults.outlinedButtonColors(),
                    borde = ButtonDefaults.outlinedButtonBorder
                )
            }
        }
    }
}

@Composable
private fun DetalleFilaVoucher(etiqueta: String, valor: String) {
    val colores = MaterialTheme.colorScheme
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = etiqueta,
            fontSize = 13.sp,
            color = colores.onSurfaceVariant
        )
        Text(
            text = valor,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = colores.onSurface,
            textAlign = TextAlign.End
        )
    }
}

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
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = titulo,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = texto,
                color = colores.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            BotonPrincipal(
                texto = textoBotonVolver,
                onClick = onVolver,
                modifier = Modifier.fillMaxWidth(),
                altura = 50.dp,
                forma = RoundedCornerShape(14.dp)
            )

            onVerHistorial?.let { accion ->
                Spacer(modifier = Modifier.height(12.dp))
                BotonSecundario(
                    texto = "Ver historial",
                    onClick = accion,
                    modifier = Modifier.fillMaxWidth(),
                    altura = 50.dp,
                    forma = RoundedCornerShape(14.dp)
                )
            }
        }
    }
}