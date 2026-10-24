package com.example.pagoflex.ui.screens

import androidx.compose.foundation.BorderStroke
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
import com.example.pagoflex.validation.ValidacionesTarjeta
import com.example.pagoflex.ui.components.BotonPrincipal
import com.example.pagoflex.ui.components.BotonSecundario
import com.example.pagoflex.ui.components.TarjetaSeccion
import java.text.NumberFormat
import java.util.Locale

@Suppress("DEPRECATION")
@Composable
fun DetallePagoScreen(
    onVolverAtras: () -> Unit,
    onConfirmarPago: () -> Unit,
    tarjetas: List<Map<String, String>>,
    tarjetaSeleccionadaId: String?,
    onSeleccionarTarjeta: (String) -> Unit,
    empresa: String = "",
    concepto: String = "",
    monto: Double = 0.0,
    onIrAVincularTarjeta: () -> Unit = {}
) {
    val colores = MaterialTheme.colorScheme
    val colorTarjetaBancaria = Color(0xFF001F3F)

    val montoFormateado = NumberFormat
        .getCurrencyInstance(Locale("es", "CL"))
        .format(monto)

    val tarjetaElegida = tarjetas.firstOrNull { it["id"] == tarjetaSeleccionadaId }
    val puedePagar = tarjetaElegida != null &&
            ValidacionesTarjeta.vencimientoVigente(tarjetaElegida["vencimiento"].orEmpty()) &&
            monto.isFinite() && monto > 0

    Scaffold(
        containerColor = colores.background,
        bottomBar = {
            Surface(
                color = colores.surface,
                contentColor = colores.onSurface,
                tonalElevation = 12.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.navigationBarsPadding().padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = if (tarjetaElegida != null) {
                            "Se pagará con ${tarjetaElegida["marca"]} •••• ${tarjetaElegida["ultimosDigitos"]}. " +
                                    "Pago de demostración: no se realiza un cargo bancario."
                        } else {
                            "Pago de demostración. No se realiza un cargo bancario."
                        },
                        fontSize = 12.sp,
                        color = colores.onSurfaceVariant
                    )

                    ProvideTextStyle(
                        value = MaterialTheme.typography.labelLarge.copy(fontSize = 16.sp)
                    ) {
                        BotonPrincipal(
                            texto = "Confirmar pago · $montoFormateado",
                            onClick = onConfirmarPago,
                            habilitado = puedePagar,
                            modifier = Modifier.fillMaxWidth(),
                            altura = 54.dp,
                            forma = RoundedCornerShape(16.dp),
                            colores = ButtonDefaults.buttonColors(
                                containerColor = colores.secondary,
                                contentColor = colores.onSecondary
                            ),
                            elevacion = ButtonDefaults.buttonElevation(),
                            relleno = ButtonDefaults.ContentPadding,
                            pesoTexto = FontWeight.Bold
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
                IconButton(onClick = onVolverAtras) {
                    Text("‹", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = colores.onBackground)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Detalle de pago",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = colores.onBackground
                )
            }

            TarjetaSeccion(
                forma = RoundedCornerShape(24.dp),
                colores = CardDefaults.cardColors(
                    containerColor = colores.surface,
                    contentColor = colores.onSurface
                ),
                elevacion = CardDefaults.cardElevation(defaultElevation = 4.dp),
                relleno = PaddingValues(24.dp),
                separacion = 16.dp,
                alineacionHorizontal = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .background(color = colores.primary, shape = RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🏢", fontSize = 24.sp)
                }

                Text(
                    text = "PAGAR A",
                    fontSize = 11.sp,
                    color = colores.onSurfaceVariant,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                Text(
                    text = empresa.ifBlank { "Empresa o servicio" },
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                HorizontalDivider(color = colores.outline.copy(alpha = 0.3f))

                DatoDetallePago("Emisor", empresa)
                DatoDetallePago("Concepto", concepto)

                HorizontalDivider(color = colores.outline.copy(alpha = 0.3f))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Monto total",
                        modifier = Modifier.weight(1f),
                        color = colores.onSurfaceVariant,
                        fontSize = 14.sp
                    )
                    Text(text = montoFormateado, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
            }

            Text(
                text = "Elige tu medio de pago",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = colores.onBackground
            )

            if (tarjetas.isEmpty()) {
                TarjetaSeccion(
                    forma = RoundedCornerShape(18.dp),
                    colores = CardDefaults.cardColors(
                        containerColor = colores.surface,
                        contentColor = colores.onSurface
                    ),
                    elevacion = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    relleno = PaddingValues(20.dp),
                    separacion = 12.dp
                ) {
                    Text("No tienes tarjetas vinculadas", fontWeight = FontWeight.Bold)
                    Text(
                        text = "Vincula una tarjeta de prueba para continuar con el pago simulado.",
                        color = colores.onSurfaceVariant,
                        fontSize = 14.sp
                    )
                    BotonPrincipal(
                        texto = "Vincular tarjeta de prueba",
                        onClick = onIrAVincularTarjeta,
                        modifier = Modifier.fillMaxWidth(),
                        altura = null,
                        forma = RoundedCornerShape(12.dp),
                        colores = ButtonDefaults.buttonColors(
                            containerColor = colores.secondary,
                            contentColor = colores.onSecondary
                        ),
                        elevacion = ButtonDefaults.buttonElevation(),
                        relleno = ButtonDefaults.ContentPadding
                    )
                }
            } else {
                tarjetas.forEach { tarjeta ->
                    val id = tarjeta["id"].orEmpty()
                    val seleccionada = id == tarjetaSeleccionadaId
                    val venc = tarjeta["vencimiento"].orEmpty()
                    val vigente = ValidacionesTarjeta.vencimientoVigente(venc)

                    Card(
                        onClick = { onSeleccionarTarjeta(id) },
                        enabled = vigente,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = colorTarjetaBancaria,
                            contentColor = Color.White
                        ),
                        border = if (seleccionada) BorderStroke(2.dp, colores.secondary) else null
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = seleccionada,
                                onClick = null,
                                enabled = vigente,
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = colores.secondary,
                                    unselectedColor = Color.White
                                )
                            )
                            Spacer(Modifier.width(12.dp))
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Text(
                                    text = "${tarjeta["marca"].orEmpty()} •••• ${tarjeta["ultimosDigitos"].orEmpty()}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                                Text(
                                    text = tarjeta["titular"].orEmpty() + " • " +
                                            if (vigente) "Vence $venc" else "Vencida $venc",
                                    fontSize = 12.sp,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                            }
                            if (tarjeta["principal"] == "1") {
                                Text("★ Principal", color = colores.secondary, fontSize = 11.sp)
                            }
                        }
                    }
                }

                BotonSecundario(
                    texto = "Gestionar mis tarjetas",
                    onClick = onIrAVincularTarjeta,
                    modifier = Modifier.fillMaxWidth(),
                    altura = null,
                    forma = RoundedCornerShape(12.dp),
                    colores = ButtonDefaults.outlinedButtonColors(contentColor = colores.primary),
                    borde = ButtonDefaults.outlinedButtonBorder,
                    relleno = ButtonDefaults.ContentPadding
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun DatoDetallePago(etiqueta: String, valor: String) {
    val colores = MaterialTheme.colorScheme

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(text = etiqueta, color = colores.onSurfaceVariant, fontSize = 13.sp)
        Text(
            text = valor,
            color = colores.onSurface,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium
        )
    }
}