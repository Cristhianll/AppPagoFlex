
package com.example.pagoflex.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
private val TextoOscuro = Color(0xFF0F172A)
private val GrisClaroTexto = Color(0xFF64748B)

private fun formatoPesos(monto: Double): String {
    return NumberFormat.getCurrencyInstance(Locale("es", "CL"))
        .format(monto)
}

@Composable
fun HistorialScreen(
    onVolverInicio: () -> Unit,
    onIrAPagos: () -> Unit,
    pagos: List<Map<String, String>> = emptyList()
) {
    // Calcula el total y la cantidad usando los pagos reales.
    val totalPagado = remember(pagos) {
        pagos.sumOf { it["monto"]?.toDoubleOrNull() ?: 0.0 }
    }

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
                    onClick = {}
                )
            }
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF8FAFC))
        ) {
            // Cabecera
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        FondoAzul,
                        shape = RoundedCornerShape(
                            bottomStart = 32.dp,
                            bottomEnd = 32.dp
                        )
                    )
                    .padding(
                        horizontal = 20.dp,
                        vertical = 24.dp
                    )
            ) {
                Column {
                    Text(
                        text = "TUS MOVIMIENTOS",
                        color = VerdeAccento,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Historial de pagos",
                        color = Color.White,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "Consulta todos tus pagos realizados.",
                        color = Color.White.copy(alpha = 0.75f),
                        fontSize = 13.sp
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Resumen calculado desde la base de datos
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = VerdeAccento
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .background(
                                            FondoAzul,
                                            RoundedCornerShape(10.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("📄", fontSize = 18.sp)
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column {
                                    Text(
                                        text = "TOTAL PAGADO",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = FondoAzul.copy(alpha = 0.7f)
                                    )

                                    Text(
                                        text = formatoPesos(totalPagado),
                                        fontSize = 19.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = FondoAzul
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Box(
                                modifier = Modifier
                                    .background(
                                        FondoAzul,
                                        RoundedCornerShape(14.dp)
                                    )
                                    .padding(
                                        horizontal = 10.dp,
                                        vertical = 7.dp
                                    )
                            ) {
                                Text(
                                    text = "${pagos.size} pagos",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // Lista de pagos reales
            if (pagos.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("📭", fontSize = 42.sp)

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Aún no tienes pagos registrados",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextoOscuro
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Cuando pagues una deuda, aparecerá aquí.",
                        fontSize = 13.sp,
                        color = GrisClaroTexto
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(
                        horizontal = 20.dp,
                        vertical = 16.dp
                    ),
                    modifier = Modifier.fillMaxSize()
                ) {
                    item {
                        Text(
                            text = "Pagos realizados",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = TextoOscuro
                        )
                    }

                    items(
                        items = pagos,
                        key = { pago ->
                            pago["id"]
                                ?: "${pago["concepto"]}_${pago["fecha"]}_${pago["monto"]}"
                        }
                    ) { pago ->
                        val empresa = pago["empresa"]
                            ?: pago["titulo"]
                            ?: "Servicio"

                        val concepto = pago["concepto"]
                            ?: "Pago de servicio"

                        val monto = pago["monto"]
                            ?.toDoubleOrNull()
                            ?.let { formatoPesos(it) }
                            ?: "$0"

                        val fecha = pago["fecha"] ?: "Fecha no disponible"

                        ItemHistorialPrototipo(
                            titulo = empresa,
                            concepto = concepto,
                            monto = monto,
                            fecha = fecha
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ItemHistorialPrototipo(
    titulo: String,
    concepto: String,
    monto: String,
    fecha: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .background(
                            FondoAzul,
                            RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🏢", fontSize = 18.sp)
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = titulo,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = TextoOscuro,
                        maxLines = 1
                    )

                    Text(
                        text = concepto,
                        fontSize = 11.sp,
                        color = GrisClaroTexto,
                        maxLines = 2
                    )

                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text = fecha,
                        fontSize = 10.sp,
                        color = GrisClaroTexto.copy(alpha = 0.8f)
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = monto,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = TextoOscuro,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(
                            Color(0xFF10B981).copy(alpha = 0.15f),
                            RoundedCornerShape(6.dp)
                        )
                        .padding(
                            horizontal = 6.dp,
                            vertical = 3.dp
                        )
                ) {
                    Text(
                        text = "✓",
                        fontSize = 10.sp,
                        color = Color(0xFF10B981),
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.width(3.dp))

                    Text(
                        text = "Pagado",
                        fontSize = 10.sp,
                        color = Color(0xFF10B981),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
