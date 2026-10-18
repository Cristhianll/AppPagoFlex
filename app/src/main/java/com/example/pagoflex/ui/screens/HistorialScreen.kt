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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.NumberFormat
import java.util.Locale

private val FondoAzul = Color(0xFF001F3F)
private val VerdeAccento = Color(0xFFD4ED91)
private val FondoClaro = Color(0xFFF8FAFC)
private val TextoOscuro = Color(0xFF0F172A)
private val GrisTexto = Color(0xFF64748B)

@Composable
fun HistorialScreen(
    onVolverInicio: () -> Unit,
    onIrAPagos: () -> Unit,
    onIrAHistorial: () -> Unit = {},
    pagos: List<Map<String, String>> = emptyList(),
    metodoPago: String = "Visa •••• 4582"
) {
    val formato = NumberFormat.getCurrencyInstance(Locale("es", "CL"))
    val totalPagado = pagos.sumOf { it["monto"]?.toDoubleOrNull() ?: 0.0 }
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
                    selected = true,
                    onClick = onIrAPagos
                )
                NavigationBarItem(
                    icon = { Text("🕒", fontSize = 20.sp) },
                    label = { Text("Historial") },
                    selected = false,
                    onClick = onIrAHistorial
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
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        FondoAzul,
                        RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp)
                    )
                    .padding(horizontal = 20.dp, vertical = 22.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "PagoFlex",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text("🔔", fontSize = 20.sp)
                }

                Spacer(Modifier.height(18.dp))

                Text(
                    "Total pagado",
                    color = VerdeAccento,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    formato.format(totalPagado),
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    if (pagos.isEmpty()) {
                        "Aún no registras pagos"
                    } else {
                        "${pagos.size} movimiento(s) · $metodoPago"
                    },
                    color = Color.White.copy(alpha = 0.75f),
                    fontSize = 13.sp
                )
            }

            Spacer(Modifier.height(18.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Últimos movimientos",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextoOscuro
                )
                Text(
                    "Ver todos",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = FondoAzul
                )
            }

            Spacer(Modifier.height(12.dp))

            if (pagos.isEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("📭", fontSize = 36.sp)
                        Spacer(Modifier.height(10.dp))
                        Text(
                            "Sin movimientos aún",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = TextoOscuro
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "Cuando pagues una deuda aparecerá aquí.",
                            color = GrisTexto,
                            fontSize = 13.sp
                        )
                    }
                }
            } else {
                Column(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    pagos.forEach { pago ->
                        val monto = pago["monto"]?.toDoubleOrNull() ?: 0.0
                        val concepto = pago["concepto"].orEmpty()
                        val fecha = pago["fecha"].orEmpty()
                        val comprobante = pago["comprobante"].orEmpty()

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .background(
                                            VerdeAccento.copy(alpha = 0.35f),
                                            CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("↗", fontSize = 18.sp, color = FondoAzul)
                                }

                                Spacer(Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        "Pago realizado",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = TextoOscuro
                                    )
                                    Text(
                                        concepto.ifBlank { "Servicio" },
                                        fontSize = 13.sp,
                                        color = GrisTexto
                                    )
                                    Spacer(Modifier.height(2.dp))
                                    Text(
                                        "$fecha · $metodoPago",
                                        fontSize = 11.sp,
                                        color = GrisTexto
                                    )
                                    if (comprobante.isNotBlank()) {
                                        Text(
                                            "Op. $comprobante",
                                            fontSize = 10.sp,
                                            color = GrisTexto
                                        )
                                    }
                                }

                                Text(
                                    "-${formato.format(monto)}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = Color(0xFFB45309)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = VerdeAccento)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("💳", fontSize = 20.sp)
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text(
                            "Método de pago principal",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = FondoAzul
                        )
                        Text(
                            metodoPago,
                            fontSize = 13.sp,
                            color = FondoAzul.copy(alpha = 0.85f)
                        )
                    }
                }
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}