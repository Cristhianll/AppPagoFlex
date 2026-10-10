package com.example.pagoflex.ui.screens


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Colores específicos para Detalle de Pago
private val FondoPantallaClaro = Color(0xFFF1F5F9)
private val ColorTarjetaBlanca = Color.White
private val ColorTextoOscuro = Color(0xFF0F172A)
private val ColorGrisTexto = Color(0xFF64748B)
private val VerdeBotonPago = Color(0xFFD4ED91)
private val AzulOscuroTexto = Color(0xFF001F3F)

@Composable
fun DetallePagoScreen(
    onVolverAtras: () -> Unit,
    onConfirmarPago: () -> Unit
) {
    var metodoSeleccionado by remember { mutableStateOf(1) } // 1: Tarjeta de Crédito, 2: Cuenta Corriente
    val scrollState = rememberScrollState()

    Scaffold(
        bottomBar = {
            // Barra inferior fija con el botón de confirmación de pago
            Surface(
                color = Color.White,
                tonalElevation = 12.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🔒 Pago protegido y encriptado", fontSize = 12.sp, color = ColorGrisTexto)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = onConfirmarPago,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = VerdeBotonPago,
                            contentColor = AzulOscuroTexto
                        )
                    ) {
                        Text("Confirmar pago · $125,000", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.weight(1f))
                        Text("➔", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(FondoPantallaClaro)
                .verticalScroll(scrollState)
                .padding(20.dp)
        ) {
            // --- CABECERA CON FLECHA ATRÁS Y TÍTULO ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onVolverAtras) {
                    Text("‹", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = ColorTextoOscuro)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Detalle de pago",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = ColorTextoOscuro
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // --- TARJETA SUPERIOR DE INSTITUCIÓN Y MONTO ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = ColorTarjetaBlanca),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .background(ColorTextoOscuro, RoundedCornerShape(14.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🏢", fontSize = 24.sp)
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = "PAGAR A", fontSize = 11.sp, color = ColorGrisTexto, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(text = "Enel Distribución", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = ColorTextoOscuro)

                    Spacer(modifier = Modifier.height(20.dp))
                    HorizontalDivider(color = FondoPantallaClaro)
                    Spacer(modifier = Modifier.height(16.dp))

                    // Emisor
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Emisor", color = ColorGrisTexto, fontSize = 14.sp)
                        Text("Enel Distribución", color = ColorTextoOscuro, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    // Concepto
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Concepto", color = ColorGrisTexto, fontSize = 14.sp)
                        Text("Cuenta de Agua - Consumo Mensual", color = ColorTextoOscuro, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                    HorizontalDivider(color = FondoPantallaClaro)
                    Spacer(modifier = Modifier.height(16.dp))

                    // Monto Total
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Monto total", color = ColorGrisTexto, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text("$125,000", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = ColorTextoOscuro)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("CLP", fontSize = 12.sp, color = ColorGrisTexto, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- SECCIÓN MEDIO DE PAGO ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "2  Medio de pago",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = ColorTextoOscuro
                )
                Text(
                    text = "Selecciona uno",
                    fontSize = 12.sp,
                    color = ColorGrisTexto
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Opción 1: Tarjeta de Crédito
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { metodoSeleccionado = 1 },
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (metodoSeleccionado == 1) ColorTextoOscuro else ColorTarjetaBlanca
                ),
                border = if (metodoSeleccionado == 1) null else androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray)
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(
                                    if (metodoSeleccionado == 1) Color.White.copy(alpha = 0.15f) else FondoPantallaClaro,
                                    RoundedCornerShape(10.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("💳", fontSize = 20.sp)
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                "Tarjeta de Crédito",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = if (metodoSeleccionado == 1) Color.White else ColorTextoOscuro
                            )
                            Text(
                                "•••• 4582 • Visa",
                                fontSize = 12.sp,
                                color = if (metodoSeleccionado == 1) Color.White.copy(alpha = 0.7f) else ColorGrisTexto
                            )
                        }
                    }

                    RadioButton(
                        selected = metodoSeleccionado == 1,
                        onClick = { metodoSeleccionado = 1 },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = VerdeBotonPago,
                            unselectedColor = ColorGrisTexto
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Opción 2: Cuenta Corriente
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { metodoSeleccionado = 2 },
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (metodoSeleccionado == 2) ColorTextoOscuro else ColorTarjetaBlanca
                ),
                border = if (metodoSeleccionado == 2) null else androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray)
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(
                                    if (metodoSeleccionado == 2) Color.White.copy(alpha = 0.15f) else FondoPantallaClaro,
                                    RoundedCornerShape(10.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🏦", fontSize = 20.sp)
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                "Cuenta Corriente",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = if (metodoSeleccionado == 2) Color.White else ColorTextoOscuro
                            )
                            Text(
                                "Banco PagoFlex •• 9012",
                                fontSize = 12.sp,
                                color = if (metodoSeleccionado == 2) Color.White.copy(alpha = 0.7f) else ColorGrisTexto
                            )
                        }
                    }

                    RadioButton(
                        selected = metodoSeleccionado == 2,
                        onClick = { metodoSeleccionado = 2 },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = VerdeBotonPago,
                            unselectedColor = ColorGrisTexto
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}