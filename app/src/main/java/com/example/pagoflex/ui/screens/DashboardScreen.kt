package com.example.pagoflex.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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

// Definimos los colores con un sufijo o de forma interna para evitar conflictos con FormularioScreen
private val DashAzulFondo = Color(0xFF001F3F)
private val DashVerdeAccento = Color(0xFFD4ED91)
private val DashTextoBlancoSuave = Color(0xFFE2E8F0)
private val DashGrisClaro = Color(0xFF64748B)
private val DashTextoPrincipal = Color(0xFF0F172A)
private val DashFondoBlanca = Color(0xFFF8FAFC)

@Composable
fun DashboardScreen(
    nombreUsuario: String,
    onVolverAlLogin: () -> Unit,
    onIrADetallePago: () -> Unit,
    onIrAPagos: () -> Unit,
    onIrHistorial:() -> Unit

) {
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
                    selected = true,
                    onClick = { }
                )
                NavigationBarItem(
                    icon = { Text("📄", fontSize = 20.sp) },
                    label = { Text("Pagos") },
                    selected = false,
                    onClick = {
                        onIrAPagos()
                    }
                )
                NavigationBarItem(
                    icon = { Text("🕒", fontSize = 20.sp) },
                    label = { Text("Historial") },
                    selected = false,
                    onClick = {
                        onIrHistorial()
                    }
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(DashAzulFondo)
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // --- CABECERA CON LOGO Y NOMBRE PAGOFLEX ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(DashVerdeAccento, RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "P",
                            color = DashAzulFondo,
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "PagoFlex",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }

                TextButton(onClick = onVolverAlLogin) {
                    Text("Salir", color = DashVerdeAccento, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- SALUDO ---
            Text(
                text = "BUENOS DÍAS",
                color = DashVerdeAccento,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "¡Hola, ${if (nombreUsuario.isBlank()) "Cristhian" else nombreUsuario}!",
                color = Color.White,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Tu tranquilidad financiera, al día.",
                color = DashTextoBlancoSuave,
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            // --- TARJETA DE PRÓXIMO VENCIMIENTO ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "PRÓXIMO VENCIMIENTO",
                            color = DashGrisClaro,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                        Box(
                            modifier = Modifier
                                .background(Color.Black, RoundedCornerShape(12.dp))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text("⏱ 5 días", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(DashFondoBlanca, RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🏢", fontSize = 24.sp)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text("Inmobiliaria del Parque", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = DashTextoPrincipal)
                            Text("📅 Vence 15 Oct", color = DashGrisClaro, fontSize = 14.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                    HorizontalDivider(color = DashFondoBlanca)
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "MONTO A PAGAR",
                        color = DashGrisClaro,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("$125,000", fontSize = 34.sp, fontWeight = FontWeight.Bold, color = DashTextoPrincipal)
                        Text("CLP", color = DashGrisClaro, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = onIrADetallePago,
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DashVerdeAccento,
                            contentColor = DashAzulFondo
                        )
                    ) {
                        Text("Pagar ahora", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.weight(1f))
                        Text("➔", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- TARJETA DE RACHA DE PAGOS ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = DashVerdeAccento)
            ) {
                Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.Top) {
                    Text("📈", fontSize = 28.sp)
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "TU RACHA DE PAGOS",
                            color = DashAzulFondo.copy(alpha = 0.7f),
                            fontSize = 12.sp,
                            letterSpacing = 0.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "¡Llevas 4 meses en racha pagando!",
                            color = DashAzulFondo,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Continúa pagando a tiempo para mantener tu buen hábito financiero.",
                            color = DashAzulFondo.copy(alpha = 0.85f),
                            fontSize = 13.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}