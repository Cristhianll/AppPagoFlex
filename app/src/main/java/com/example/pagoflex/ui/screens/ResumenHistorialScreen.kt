package com.example.pagoflex.ui.screens

import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.max

private val FondoAzul = Color(0xFF001F3F)
private val VerdeAccento = Color(0xFFD4ED91)
private val FondoClaro = Color(0xFFF8FAFC)
private val TextoOscuro = Color(0xFF0F172A)
private val GrisTexto = Color(0xFF64748B)
private val VerdeOk = Color(0xFF247A3D)

@Composable
fun ResumenHistorialScreen(
    onVolverInicio: () -> Unit,
    onIrAPagos: () -> Unit,
    pagos: List<Map<String, String>> = emptyList()
) {
    val formato = NumberFormat.getCurrencyInstance(Locale("es", "CL"))
    val totalPagado = pagos.sumOf { it["monto"]?.toDoubleOrNull() ?: 0.0 }
    val promedio = if (pagos.isNotEmpty()) totalPagado / pagos.size else 0.0
    val maxMonto = pagos.maxOfOrNull { it["monto"]?.toDoubleOrNull() ?: 0.0 } ?: 0.0
    val montos = pagos.map { it["monto"]?.toDoubleOrNull() ?: 0.0 }.reversed()
    val scrollState = rememberScrollState()

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    icon = { Text("🏠", fontSize = 20.sp) },
                    label = { Text("Inicio") },
                    selected = false,
                    onClick = onVolverInicio
                )
                NavigationBarItem(
                    icon = { Text("📄", fontSize = 20.sp) },
                    label = { Text("Pagos") },
                    selected = false,
                    onClick = onIrAPagos
                )
                NavigationBarItem(
                    icon = { Text("🕒", fontSize = 20.sp) },
                    label = { Text("Historial") },
                    selected = true,
                    onClick = { }
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(FondoClaro)
                .verticalScroll(scrollState)
        ) {
            // Cabecera
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        FondoAzul,
                        RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp)
                    )
                    .padding(horizontal = 20.dp, vertical = 22.dp)
            ) {
                Text(
                    "ANÁLISIS",
                    color = VerdeAccento,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "Historial financiero",
                    color = Color.White,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "Resumen de tus pagos e inversiones de hábitos",
                    color = Color.White.copy(alpha = 0.75f),
                    fontSize = 13.sp
                )
            }

            Spacer(Modifier.height(16.dp))

            // Tarjetas de métricas
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricCard(
                    modifier = Modifier.weight(1f),
                    titulo = "Total pagado",
                    valor = formato.format(totalPagado),
                    emoji = "💰"
                )
                MetricCard(
                    modifier = Modifier.weight(1f),
                    titulo = "Pagos",
                    valor = "${pagos.size}",
                    emoji = "✅"
                )
            }

            Spacer(Modifier.height(10.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricCard(
                    modifier = Modifier.weight(1f),
                    titulo = "Promedio",
                    valor = formato.format(promedio),
                    emoji = "📊"
                )
                MetricCard(
                    modifier = Modifier.weight(1f),
                    titulo = "Mayor pago",
                    valor = formato.format(maxMonto),
                    emoji = "📈"
                )
            }

            Spacer(Modifier.height(18.dp))

            // Gráfico de barras
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Evolución de pagos",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = TextoOscuro
                    )
                    Text(
                        "Montos de tus últimos movimientos",
                        fontSize = 12.sp,
                        color = GrisTexto
                    )
                    Spacer(Modifier.height(16.dp))

                    if (montos.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(140.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "Sin datos para graficar aún",
                                color = GrisTexto,
                                fontSize = 13.sp
                            )
                        }
                    } else {
                        val maxBar = max(montos.maxOrNull() ?: 1.0, 1.0)
                        Canvas(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                        ) {
                            val barCount = montos.size.coerceAtMost(8)
                            val data = montos.takeLast(barCount)
                            val gap = 12.dp.toPx()
                            val barWidth = (size.width - gap * (data.size + 1)) / data.size
                            val maxH = size.height * 0.85f

                            data.forEachIndexed { index, value ->
                                val h = ((value / maxBar) * maxH).toFloat().coerceAtLeast(8f)
                                val x = gap + index * (barWidth + gap)
                                val y = size.height - h

                                drawRoundRect(
                                    color = if (index == data.lastIndex) {
                                        Color(0xFFD4ED91)
                                    } else {
                                        Color(0xFF001F3F)
                                    },
                                    topLeft = Offset(x, y),
                                    size = Size(barWidth, h),
                                    cornerRadius = CornerRadius(10f, 10f)
                                )
                            }
                        }

                        Spacer(Modifier.height(8.dp))
                        Text(
                            "Últimos ${montos.size.coerceAtMost(8)} pagos · el más reciente en verde",
                            fontSize = 11.sp,
                            color = GrisTexto
                        )
                    }
                }
            }

            Spacer(Modifier.height(18.dp))

            Text(
                "Detalle de operaciones",
                modifier = Modifier.padding(horizontal = 20.dp),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = TextoOscuro
            )

            Spacer(Modifier.height(10.dp))

            if (pagos.isEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("📭", fontSize = 32.sp)
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "Aún no hay pagos registrados",
                            fontWeight = FontWeight.Bold,
                            color = TextoOscuro
                        )
                        Text(
                            "Cuando pagues una deuda verás el análisis aquí.",
                            color = GrisTexto,
                            fontSize = 13.sp
                        )
                    }
                }
            } else {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    pagos.forEachIndexed { index, pago ->
                        val monto = pago["monto"]?.toDoubleOrNull() ?: 0.0
                        val concepto = pago["concepto"].orEmpty()
                        val fecha = pago["fecha"].orEmpty()
                        val comprobante = pago["comprobante"].orEmpty()
                        val porcentaje = if (totalPagado > 0) {
                            (monto / totalPagado * 100)
                        } else {
                            0.0
                        }

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(40.dp)
                                                .background(
                                                    VerdeAccento.copy(alpha = 0.4f),
                                                    CircleShape
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                "${index + 1}",
                                                fontWeight = FontWeight.Bold,
                                                color = FondoAzul
                                            )
                                        }
                                        Spacer(Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                concepto.ifBlank { "Pago" },
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp,
                                                color = TextoOscuro
                                            )
                                            Text(
                                                fecha,
                                                fontSize = 12.sp,
                                                color = GrisTexto
                                            )
                                        }
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            formato.format(monto),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = VerdeOk
                                        )
                                        Text(
                                            String.format(Locale.US, "%.1f%%", porcentaje),
                                            fontSize = 11.sp,
                                            color = GrisTexto
                                        )
                                    }
                                }

                                Spacer(Modifier.height(10.dp))

                                // Barra de participación
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                        .background(Color(0xFFE2E8F0), RoundedCornerShape(4.dp))
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth(
                                                fraction = (porcentaje / 100.0)
                                                    .toFloat()
                                                    .coerceIn(0.05f, 1f)
                                            )
                                            .height(6.dp)
                                            .background(FondoAzul, RoundedCornerShape(4.dp))
                                    )
                                }

                                if (comprobante.isNotBlank()) {
                                    Spacer(Modifier.height(6.dp))
                                    Text(
                                        "Comprobante: $comprobante",
                                        fontSize = 11.sp,
                                        color = GrisTexto
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(28.dp))
        }
    }
}

@Composable
private fun MetricCard(
    modifier: Modifier = Modifier,
    titulo: String,
    valor: String,
    emoji: String
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(emoji, fontSize = 18.sp)
            Spacer(Modifier.height(6.dp))
            Text(
                titulo,
                fontSize = 11.sp,
                color = GrisTexto,
                fontWeight = FontWeight.Medium
            )
            Text(
                valor,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextoOscuro,
                maxLines = 1
            )
        }
    }
}