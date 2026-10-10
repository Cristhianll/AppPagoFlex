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
            // Barra de navegación inferior funcional
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
                .background(FondoAzul)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Título de la pantalla
            Text(
                text = "Historial y Estado",
                color = VerdeAccento,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Revisa tus cuentas pendientes y pagadas",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                // --- SECCIÓN: PENDIENTES ---
                item {
                    Text(
                        text = "⏳ POR PAGAR (PENDIENTES)",
                        color = VerdeAccento,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }

                item {
                    ItemHistorialCard(
                        titulo = "Inmobiliaria del Parque",
                        concepto = "Arriendo / Gasto Común",
                        monto = "$125,000 CLP",
                        fecha = "Vence 15 Oct",
                        estado = "Pendiente",
                        colorEstado = Color(0xFFF59E0B) // Naranja advertencia
                    )
                }

                item {
                    ItemHistorialCard(
                        titulo = "Enel Distribución",
                        concepto = "Cuenta de Luz - Mensual",
                        monto = "$34,500 CLP",
                        fecha = "Vence 22 Oct",
                        estado = "Pendiente",
                        colorEstado = Color(0xFFF59E0B)
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "✅ PAGADOS",
                        color = VerdeAccento,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }

                // --- SECCIÓN: HISTORIAL DE PAGADOS IMPLEMENTADA---
                item {
                    ItemHistorialCard(
                        titulo = "Aguas Andinas",
                        concepto = "Consumo de Agua",
                        monto = "$18,900 CLP",
                        fecha = "Pagado el 10 Sep",
                        estado = "Pagado",
                        colorEstado = Color(0xFF10B981) // Verde éxito
                    )
                }

                item {
                    ItemHistorialCard(
                        titulo = "Inmobiliaria del Parque",
                        concepto = "Arriendo Mes Anterior",
                        monto = "$125,000 CLP",
                        fecha = "Pagado el 15 Sep",
                        estado = "Pagado",
                        colorEstado = Color(0xFF10B981)
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(30.dp))
                }
            }
        }
    }
}

@Composable
fun ItemHistorialCard(
    titulo: String,
    concepto: String,
    monto: String,
    fecha: String,
    estado: String,
    colorEstado: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = titulo, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextoOscuro)

                // Etiqueta de estado de pago
                Box(
                    modifier = Modifier
                        .background(colorEstado.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(text = estado, color = colorEstado, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(text = concepto, fontSize = 13.sp, color = GrisClaroTexto)

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = Color(0xFFF1F5F9))
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = fecha, fontSize = 12.sp, color = GrisClaroTexto)
                Text(text = monto, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextoOscuro)
            }
        }
    }
}