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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val ColorFondoClaro = Color(0xFFF8FAFC)
private val VerdeAccento = Color(0xFFD4ED91)
private val AzulOscuro = Color(0xFF001F3F)
private val GrisTexto = Color(0xFF64748B)

@Composable
fun PagoExitosoScreen(
    onVolverInicio: () -> Unit,
    onVerHistorial: () -> Unit
) {
    val scrollState = rememberScrollState()

    Scaffold(
        bottomBar = {
            Surface(
                color = Color.White,
                tonalElevation = 12.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(
                        onClick = onVolverInicio,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = VerdeAccento,
                            contentColor = AzulOscuro
                        )
                    ) {
                        Text("Volver al inicio", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.weight(1f))
                        Text("➔", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    TextButton(onClick = onVerHistorial) {
                        Text(
                            text = "Ver historial de pagos",
                            color = AzulOscuro,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(ColorFondoClaro)
                .verticalScroll(scrollState)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // Icono de éxito con círculos concéntricos
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .background(VerdeAccento.copy(alpha = 0.3f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(VerdeAccento, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("✓", fontSize = 32.sp, color = AzulOscuro, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "TRANSACCIÓN COMPLETADA",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = GrisTexto,
                letterSpacing = 1.2.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "¡Pago Exitoso!",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = AzulOscuro
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Tu pago fue recibido y ya está siendo procesado.",
                fontSize = 13.sp,
                color = GrisTexto,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Tarjeta de detalles del pago
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(AzulOscuro, RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🏢", fontSize = 20.sp)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("PAGASTE A", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = GrisTexto)
                            Text("Cuenta de Agua - Consumo Mensual", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = AzulOscuro)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = Color(0xFFF1F5F9))
                    Spacer(modifier = Modifier.height(16.dp))

                    Text("TOTAL PAGADO", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = GrisTexto)
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text("$125,000", fontSize = 30.sp, fontWeight = FontWeight.Bold, color = AzulOscuro)
                        Text("CLP", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = GrisTexto)
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = Color(0xFFF1F5F9))
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("10 Oct, 2025 • 09:41", fontSize = 11.sp, color = GrisTexto)
                        Text("Operación #PF-847291", fontSize = 11.sp, color = GrisTexto, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Tarjeta verde de racha avanzada
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = VerdeAccento)
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("📈", fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text("RACHA DE PAGOS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = AzulOscuro.copy(alpha = 0.7f))
                        Text("¡Llevas 4 meses en racha pagando!", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = AzulOscuro)
                        Text("Sigue así y mantén tus pagos al día.", fontSize = 12.sp, color = AzulOscuro.copy(alpha = 0.8f))
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
