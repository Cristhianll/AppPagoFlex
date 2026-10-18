
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
import java.text.NumberFormat
import java.util.Locale

// Colores de la pantalla Detalle de Pago
private val FondoPantallaClaro = Color(0xFFF1F5F9)
private val ColorTarjetaBlanca = Color.White
private val ColorTextoOscuro = Color(0xFF0F172A)
private val ColorGrisTexto = Color(0xFF64748B)
private val VerdeBotonPago = Color(0xFFD4ED91)
private val AzulOscuroTexto = Color(0xFF001F3F)

@Composable
fun DetallePagoScreen(
    onVolverAtras: () -> Unit,
    onConfirmarPago: () -> Unit,
    empresa: String = "Enel Distribución",
    concepto: String = "Cuenta de electricidad",
    monto: Double = 125000.0
) {
    val scrollState = rememberScrollState()

    // Formato de moneda chilena
    val montoFormateado = NumberFormat
        .getCurrencyInstance(Locale("es", "CL"))
        .format(monto)

    Scaffold(
        bottomBar = {
            Surface(
                color = Color.White,
                tonalElevation = 12.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .navigationBarsPadding()
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🔒 Pago protegido y encriptado",
                            fontSize = 12.sp,
                            color = ColorGrisTexto
                        )
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
                        Text(
                            text = "Confirmar pago · $montoFormateado",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = "➔",
                            fontSize = 20.sp,
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
                .background(FondoPantallaClaro)
                .verticalScroll(scrollState)
                .padding(20.dp)
        ) {
            // Encabezado
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onVolverAtras) {
                    Text(
                        text = "‹",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = ColorTextoOscuro
                    )
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

            // Tarjeta con los datos de la deuda
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = ColorTarjetaBlanca
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 4.dp
                )
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .background(
                                ColorTextoOscuro,
                                RoundedCornerShape(14.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "🏢",
                            fontSize = 24.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "PAGAR A",
                        fontSize = 11.sp,
                        color = ColorGrisTexto,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = empresa,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = ColorTextoOscuro
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    HorizontalDivider(color = FondoPantallaClaro)

                    Spacer(modifier = Modifier.height(16.dp))

                    // Empresa emisora
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Emisor",
                            color = ColorGrisTexto,
                            fontSize = 14.sp
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Text(
                            text = empresa,
                            color = ColorTextoOscuro,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Concepto de la deuda
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = "Concepto",
                            color = ColorGrisTexto,
                            fontSize = 14.sp
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Text(
                            text = concepto,
                            modifier = Modifier.weight(1f),
                            color = ColorTextoOscuro,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    HorizontalDivider(color = FondoPantallaClaro)

                    Spacer(modifier = Modifier.height(16.dp))

                    // Monto dinámico
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Monto total",
                            color = ColorGrisTexto,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Text(
                            text = montoFormateado,
                            color = ColorTextoOscuro,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Medio de pago
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
                    text = "Método registrado",
                    fontSize = 12.sp,
                    color = ColorGrisTexto
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Tarjeta de pago
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = ColorTextoOscuro
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                Color.White.copy(alpha = 0.15f),
                                RoundedCornerShape(10.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "💳",
                            fontSize = 20.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Tarjeta de Crédito",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color.White
                        )

                        Text(
                            text = "•••• 4582 • Visa",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "Principal",
                        fontSize = 11.sp,
                        color = VerdeBotonPago,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
