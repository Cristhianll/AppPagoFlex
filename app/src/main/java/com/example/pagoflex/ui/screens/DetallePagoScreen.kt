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
    empresa: String = "",
    concepto: String = "",
    monto: Double = 0.0,
    ultimosDigitosTarjeta: String = "",
    titularTarjeta: String = "",
    onIrAVincularTarjeta: () -> Unit = {}
) {
    val colores = MaterialTheme.colorScheme

    // =====================================================
    // COLORES DEL DETALLE SEGÚN EL TEMA ACTIVO
    // =====================================================

    val colorFondo = colores.background
    val colorTarjeta = colores.surface
    val colorTexto = colores.onSurface
    val colorTextoFondo = colores.onBackground
    val colorTextoSecundario = colores.onSurfaceVariant

    val colorBoton = colores.secondary
    val colorTextoBoton = colores.onSecondary

    val colorFondoIcono = colores.primary
    val colorSeparador = colores.outline.copy(alpha = 0.3f)

    // La tarjeta bancaria conserva una presentación azul
    // con texto claro en ambos temas.
    val colorTarjetaBancaria = Color(0xFF001F3F)
    val colorTextoTarjetaBancaria = Color.White
    val colorEtiquetaPrincipal = colores.secondary

    // =====================================================
    // DATOS DE PRESENTACIÓN
    // =====================================================

    val montoFormateado = NumberFormat
        .getCurrencyInstance(Locale("es", "CL"))
        .format(monto)

    val tieneTarjeta = ultimosDigitosTarjeta.matches(
        Regex("[0-9]{4}")
    )

    Scaffold(
        containerColor = colorFondo,

        // =================================================
        // ZONA FIJA INFERIOR
        // =================================================

        bottomBar = {
            Surface(
                color = colorTarjeta,
                contentColor = colorTexto,
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
                        text = "Pago de demostración. " +
                                "No se realiza un cargo bancario.",
                        fontSize = 12.sp,
                        color = colorTextoSecundario
                    )

                    ProvideTextStyle(
                        value = MaterialTheme.typography.labelLarge.copy(
                            fontSize = 16.sp
                        )
                    ) {
                        BotonPrincipal(
                            texto = "Confirmar pago · $montoFormateado",
                            onClick = onConfirmarPago,
                            habilitado = tieneTarjeta &&
                                    monto.isFinite() &&
                                    monto > 0,
                            modifier = Modifier.fillMaxWidth(),
                            altura = 54.dp,
                            forma = RoundedCornerShape(16.dp),
                            colores = ButtonDefaults.buttonColors(
                                containerColor = colorBoton,
                                contentColor = colorTextoBoton
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
            // =================================================
            // CABECERA
            // =================================================

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
                        color = colorTextoFondo
                    )
                }

                Spacer(
                    modifier = Modifier.width(8.dp)
                )

                Text(
                    text = "Detalle de pago",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorTextoFondo
                )
            }

            // =================================================
            // RESUMEN DE LA DEUDA
            // =================================================

            TarjetaSeccion(
                forma = RoundedCornerShape(24.dp),
                colores = CardDefaults.cardColors(
                    containerColor = colorTarjeta,
                    contentColor = colorTexto
                ),
                elevacion = CardDefaults.cardElevation(
                    defaultElevation = 4.dp
                ),
                relleno = PaddingValues(24.dp),
                separacion = 16.dp,
                alineacionHorizontal = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .background(
                            color = colorFondoIcono,
                            shape = RoundedCornerShape(14.dp)
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
                    color = colorTextoSecundario,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                Text(
                    text = empresa.ifBlank {
                        "Empresa o servicio"
                    },
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorTexto
                )

                HorizontalDivider(
                    color = colorSeparador
                )

                DatoDetallePago(
                    etiqueta = "Emisor",
                    valor = empresa
                )

                DatoDetallePago(
                    etiqueta = "Concepto",
                    valor = concepto
                )

                HorizontalDivider(
                    color = colorSeparador
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Monto total",
                        modifier = Modifier.weight(1f),
                        color = colorTextoSecundario,
                        fontSize = 14.sp
                    )

                    Text(
                        text = montoFormateado,
                        color = colorTexto,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Text(
                text = "Medio de pago",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = colorTextoFondo
            )

            // =================================================
            // TARJETA VINCULADA
            // =================================================

            if (tieneTarjeta) {
                TarjetaSeccion(
                    forma = RoundedCornerShape(18.dp),
                    colores = CardDefaults.cardColors(
                        containerColor = colorTarjetaBancaria,
                        contentColor = colorTextoTarjetaBancaria
                    ),
                    elevacion = CardDefaults.cardElevation(
                        defaultElevation = 0.dp
                    ),
                    relleno = PaddingValues(20.dp),
                    separacion = 12.dp
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "💳",
                            fontSize = 24.sp
                        )

                        Spacer(
                            modifier = Modifier.width(12.dp)
                        )

                        Text(
                            text = "Tarjeta de demostración",
                            modifier = Modifier.weight(1f),
                            color = colorTextoTarjetaBancaria,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )

                        Text(
                            text = "Principal",
                            color = colorEtiquetaPrincipal,
                            fontSize = 11.sp
                        )
                    }

                    Text(
                        text = "•••• •••• •••• $ultimosDigitosTarjeta",
                        color = colorTextoTarjetaBancaria,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )

                    if (titularTarjeta.isNotBlank()) {
                        Text(
                            text = titularTarjeta,
                            color = colorTextoTarjetaBancaria.copy(
                                alpha = 0.8f
                            ),
                            fontSize = 13.sp
                        )
                    }
                }

                BotonSecundario(
                    texto = "Gestionar tarjeta de prueba",
                    onClick = onIrAVincularTarjeta,
                    modifier = Modifier.fillMaxWidth(),
                    altura = null,
                    forma = RoundedCornerShape(12.dp),
                    colores = ButtonDefaults.outlinedButtonColors(
                        contentColor = colores.primary
                    ),
                    borde = ButtonDefaults.outlinedButtonBorder,
                    relleno = ButtonDefaults.ContentPadding
                )
            } else {
                // =============================================
                // ESTADO SIN TARJETA VINCULADA
                // =============================================

                TarjetaSeccion(
                    forma = RoundedCornerShape(18.dp),
                    colores = CardDefaults.cardColors(
                        containerColor = colorTarjeta,
                        contentColor = colorTexto
                    ),
                    elevacion = CardDefaults.cardElevation(
                        defaultElevation = 0.dp
                    ),
                    relleno = PaddingValues(20.dp),
                    separacion = 12.dp
                ) {
                    Text(
                        text = "No tienes tarjetas vinculadas",
                        color = colorTexto,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Vincula una tarjeta de prueba para " +
                                "continuar con el pago simulado.",
                        color = colorTextoSecundario,
                        fontSize = 14.sp
                    )

                    BotonPrincipal(
                        texto = "Vincular tarjeta de prueba",
                        onClick = onIrAVincularTarjeta,
                        modifier = Modifier.fillMaxWidth(),
                        altura = null,
                        forma = RoundedCornerShape(12.dp),
                        colores = ButtonDefaults.buttonColors(
                            containerColor = colorBoton,
                            contentColor = colorTextoBoton
                        ),
                        elevacion = ButtonDefaults.buttonElevation(),
                        relleno = ButtonDefaults.ContentPadding
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )
        }
    }
}

// =====================================================
// DATO DEL RESUMEN: EMISOR O CONCEPTO
// =====================================================

@Composable
private fun DatoDetallePago(
    etiqueta: String,
    valor: String
) {
    val colores = MaterialTheme.colorScheme

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = etiqueta,
            color = colores.onSurfaceVariant,
            fontSize = 13.sp
        )

        Text(
            text = valor,
            color = colores.onSurface,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium
        )
    }
}