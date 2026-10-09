package com.example.pagoflex.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val ColorAzulOscuro = Color(0xFF001F3F)
val ColorVerdeClaro = Color(0xFFD4ED91)
val ColorTextoClaro = Color(0xFFE0E0E0)
val ColorFondoClaro = Color(0xFFF5F5F5)
val ColorGrisMedio = Color(0xFF9E9E9E)

@Composable
fun DashboardScreen(
    nombreUsuario: String,
    onVolverAlLogin: () -> Unit
) {
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
                    onClick = { }
                )
                NavigationBarItem(
                    icon = { Text("🕒", fontSize = 20.sp) },
                    label = { Text("Historial") },
                    selected = false,
                    onClick = { }
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(ColorAzulOscuro)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(44.dp))

            // Cabecera superior de la app
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(Color.Black, RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("P", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("PagoFlex", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Medium)
                }

                // Botón de salir o perfil rápido
                TextButton(onClick = onVolverAlLogin) {
                    Text("Salir", color = ColorVerdeClaro)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Saludo
            Text("BUENOS DÍAS", color = ColorVerdeClaro, fontSize = 12.sp, letterSpacing = 1.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text("¡Hola, ${if (nombreUsuario.isBlank()) "Cristhian" else nombreUsuario}!", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Tu tranquilidad financiera, al día.", color = ColorTextoClaro, fontSize = 16.sp)

            Spacer(modifier = Modifier.height(32.dp))

            // Tarjeta de Próximo Vencimiento
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
                        Text("PRÓXIMO VENCIMIENTO", color = ColorGrisMedio, fontSize = 12.sp, letterSpacing = 0.5.sp)
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
                                .background(ColorFondoClaro, RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🏢", fontSize = 24.sp)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text("Inmobiliaria del Parque", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Text("📅 Vence 15 Oct", color = ColorGrisMedio, fontSize = 14.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    HorizontalDivider(color = ColorFondoClaro)
                    Spacer(modifier = Modifier.height(16.dp))

                    Text("MONTO A PAGAR", color = ColorGrisMedio, fontSize = 12.sp, letterSpacing = 0.5.sp)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("$125,000", fontSize = 34.sp, fontWeight = FontWeight.Bold, color = ColorAzulOscuro)
                        Text("CLP", color = ColorGrisMedio, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = { /* Acción de pago */ },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ColorVerdeClaro, contentColor = ColorAzulOscuro)
                    ) {
                        Text("Pagar ahora", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.weight(1f))
                        Text("➔", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Tarjeta de Racha de Pagos en inicio
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = ColorVerdeClaro)
            ) {
                Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.Top) {
                    Text("📈", fontSize = 28.sp)
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text("TU RACHA DE PAGOS", color = ColorAzulOscuro.copy(alpha = 0.7f), fontSize = 12.sp, letterSpacing = 0.5.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("¡Llevas 4 meses en racha pagando!", color = ColorAzulOscuro, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Continúa pagando a tiempo para mantener tu buen hábito financiero.", color = ColorAzulOscuro.copy(alpha = 0.8f), fontSize = 14.sp)
                    }
                }
            }
        }
    }
}