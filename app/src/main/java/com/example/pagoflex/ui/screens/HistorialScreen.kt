package com.example.pagoflex.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val FondoAzul = Color(0xFF001F3F)
private val VerdeAccento = Color(0xFFD4ED91)
private val TextoOscuro = Color(0xFF0F172A)
private val GrisClaroTexto = Color(0xFF64748B)

@Composable
fun HistorialScreen(
    onVolverInicio: () -> Unit,
    onIrAPagos: () -> Unit
) {
    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = Color.White, tonalElevation = 8.dp) {
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
                .background(Color(0xFFF8FAFC))
        ) {
            // --- CABECERA AZUL OSCURO ESTILO PROTOTIPO ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        FondoAzul,
                        shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
                    )
                    .padding(horizontal = 20.dp, vertical = 24.dp)
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

                    // Tarjeta flotante dentro de la cabecera
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = VerdeAccento)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .background(FondoAzul, RoundedCornerShape(10.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("📄", fontSize = 18.sp)
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text("TOTAL PAGADO EN 2025", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = FondoAzul.copy(alpha = 0.7f))
                                    Text("$233,330", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = FondoAzul)
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .background(FondoAzul, RoundedCornerShape(14.dp))
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Text("4 pagos", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // --- CONTENIDO DE LA LISTA ---
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("📅", fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Octubre 2025", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextoOscuro)
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color.White,
                            shadowElevation = 1.dp
                        ) {
                            Text(
                                text = "Todos",
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextoOscuro
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }

                item {
                    ItemHistorialPrototipo(
                        titulo = "Enel Distribución",
                        concepto = "Cuenta de Agua - Consumo Mensual",
                        monto = "$125,000",
                        fecha = "10 Oct, 2025"
                    )
                }

                item {
                    ItemHistorialPrototipo(
                        titulo = "Aguas Andinas",
                        concepto = "Servicio de agua potable",
                        monto = "$38,450",
                        fecha = "12 Sep, 2025"
                    )
                }

                item {
                    ItemHistorialPrototipo(
                        titulo = "Mundo Pacífico",
                        concepto = "Internet hogar",
                        monto = "$26,990",
                        fecha = "28 Ago, 2025"
                    )
                }

                item {
                    ItemHistorialPrototipo(
                        titulo = "Enel Distribución",
                        concepto = "Electricidad residencial",
                        monto = "$42,890",
                        fecha = "05 Ago, 2025"
                    )
                }

                // Tarjeta inferior de racha en historial
                item {
                    Spacer(modifier = Modifier.height(4.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = VerdeAccento)
                    ) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text("📈", fontSize = 22.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("4 meses pagando a tiempo", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = FondoAzul)
                                Text("Tu historial confirma que mantienes una buena racha.", fontSize = 12.sp, color = FondoAzul.copy(alpha = 0.8f))
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
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
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f) // Ocupa el espacio disponible para evitar cortes
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .background(FondoAzul, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🏢", fontSize = 18.sp)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = titulo, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextoOscuro, maxLines = 1)
                    Text(text = concepto, fontSize = 11.sp, color = GrisClaroTexto, maxLines = 1)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(text = fecha, fontSize = 10.sp, color = GrisClaroTexto.copy(alpha = 0.8f))
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Columna derecha con ancho adecuado para que no se baje el texto
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = monto,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = TextoOscuro,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(Color(0xFF10B981).copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text("✓", fontSize = 10.sp, color = Color(0xFF10B981), fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(text = "Pagado", fontSize = 10.sp, color = Color(0xFF10B981), fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
