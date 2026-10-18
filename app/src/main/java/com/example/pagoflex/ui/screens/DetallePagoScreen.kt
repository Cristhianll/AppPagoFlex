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
    empresa: String = "",
    concepto: String = "",
    monto: Double = 0.0,
    ultimosDigitosTarjeta: String = "",
    titularTarjeta: String = "",
    onIrAVincularTarjeta: () -> Unit = {}
) {
    val montoFormateado = NumberFormat
        .getCurrencyInstance(Locale("es", "CL"))
        .format(monto)

    val tieneTarjeta = ultimosDigitosTarjeta.matches(
        Regex("[0-9]{4}")
    )

    Scaffold(
        containerColor = FondoPantallaClaro,
        bottomBar = {
            Surface(
                color = Color.White,
                tonalElevation = 12.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .navigationBarsPadding()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Pago de demostración. No se realiza un cargo bancario.",
                        fontSize = 12.sp,
                        color = ColorGrisTexto
                    )

                    Button(
                        onClick = onConfirmarPago,
                        enabled = tieneTarjeta &&
                                monto.isFinite() &&
                                monto > 0,
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
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onVolverAtras
                ) {
                    Text(
                        text = "‹",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = ColorTextoOscuro
                    )
                }

                Spacer(Modifier.width(8.dp))

                Text(
                    text = "Detalle de pago",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = ColorTextoOscuro
                )
            }

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
                    verticalArrangement = Arrangement.spacedBy(16.dp),
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

                    Text(
                        text = "PAGAR A",
                        fontSize = 11.sp,
                        color = ColorGrisTexto,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )

                    Text(
                        text = empresa.ifBlank { "Empresa o servicio" },
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = ColorTextoOscuro
                    )

                    HorizontalDivider(
                        color = FondoPantallaClaro
                    )

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "Emisor",
                            color = ColorGrisTexto,
                            fontSize = 13.sp
                        )

                        Text(
                            text = empresa,
                            color = ColorTextoOscuro,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "Concepto",
                            color = ColorGrisTexto,
                            fontSize = 13.sp
                        )

                        Text(
                            text = concepto,
                            color = ColorTextoOscuro,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    HorizontalDivider(
                        color = FondoPantallaClaro
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Monto total",
                            modifier = Modifier.weight(1f),
                            color = ColorGrisTexto,
                            fontSize = 14.sp
                        )

                        Text(
                            text = montoFormateado,
                            color = ColorTextoOscuro,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Text(
                text = "Medio de pago",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = ColorTextoOscuro
            )

            if (tieneTarjeta) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = AzulOscuroTexto
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "💳",
                                fontSize = 24.sp
                            )

                            Spacer(Modifier.width(12.dp))

                            Text(
                                text = "Tarjeta de demostración",
                                modifier = Modifier.weight(1f),
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )

                            Text(
                                text = "Principal",
                                color = VerdeBotonPago,
                                fontSize = 11.sp
                            )
                        }

                        Text(
                            text = "•••• •••• •••• $ultimosDigitosTarjeta",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp
                        )

                        if (titularTarjeta.isNotBlank()) {
                            Text(
                                text = titularTarjeta,
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 13.sp
                            )
                        }
                    }
                }

                OutlinedButton(
                    onClick = onIrAVincularTarjeta,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Gestionar tarjeta de prueba")
                }
            } else {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "No tienes tarjetas vinculadas",
                            color = ColorTextoOscuro,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "Vincula una tarjeta de prueba para continuar con el pago simulado.",
                            color = ColorGrisTexto,
                            fontSize = 14.sp
                        )

                        Button(
                            onClick = onIrAVincularTarjeta,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = VerdeBotonPago,
                                contentColor = AzulOscuroTexto
                            )
                        ) {
                            Text("Vincular tarjeta de prueba")
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))
        }
    }
}