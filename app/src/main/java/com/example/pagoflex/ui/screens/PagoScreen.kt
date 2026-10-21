package com.example.pagoflex.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pagoflex.data.ValidacionesDeuda
import com.example.pagoflex.ui.components.BarraNavegacionPagoFlex
import com.example.pagoflex.ui.components.BotonPrincipal
import com.example.pagoflex.ui.components.BotonSecundario
import com.example.pagoflex.ui.components.SeccionPagoFlex
import com.example.pagoflex.ui.components.TarjetaSeccion
import java.text.NumberFormat
import java.util.Locale

@Suppress("DEPRECATION")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PagosScreen(
    deudasPendientes: List<Map<String, String>>,
    cargando: Boolean,
    onPagarDeuda: (String) -> Unit,
    onIrAInicio: () -> Unit,
    onIrAHistorial: () -> Unit,
    onActualizar: () -> Unit
) {
    val colores = MaterialTheme.colorScheme

    val formatoMoneda = remember {
        NumberFormat.getCurrencyInstance(
            Locale("es", "CL")
        )
    }

    val deudas = deudasPendientes
        .filter {
            it["estado"] == "PENDIENTE"
        }
        .sortedWith(
            compareBy<Map<String, String>> {
                if (
                    ValidacionesDeuda.fechaValida(
                        it["vencimiento"].orEmpty()
                    )
                ) {
                    0
                } else {
                    1
                }
            }.thenBy {
                it["vencimiento"].orEmpty()
            }
        )

    val totalPendiente = deudas.sumOf {
        it["monto"]?.toDoubleOrNull() ?: 0.0
    }


    Scaffold(
        containerColor = colores.background,



        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Cuentas por pagar",
                        fontWeight = FontWeight.Bold
                    )
                },

                actions = {
                    TextButton(
                        onClick = onActualizar,
                        enabled = !cargando,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = colores.onPrimary,
                            disabledContentColor = colores.onPrimary.copy(
                                alpha = 0.5f
                            )
                        )
                    ) {
                        Text(
                            text = if (cargando) {
                                "Cargando..."
                            } else {
                                "Actualizar"
                            }
                        )
                    }
                },

                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colores.primary,
                    titleContentColor = colores.onPrimary,
                    actionIconContentColor = colores.onPrimary
                )
            )
        },



        bottomBar = {
            BarraNavegacionPagoFlex(
                seccionActual = SeccionPagoFlex.PAGOS,
                onIrAInicio = onIrAInicio,
                onIrAPagos = {},
                onIrAHistorial = onIrAHistorial
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // CARGA

            if (cargando) {
                item {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth(),
                        color = colores.primary,
                        trackColor = colores.surfaceVariant
                    )
                }
            }



            item {
                TarjetaSeccion(
                    forma = RoundedCornerShape(22.dp),
                    colores = CardDefaults.cardColors(
                        containerColor = colores.primaryContainer,
                        contentColor = colores.onPrimaryContainer
                    ),
                    elevacion = CardDefaults.cardElevation(
                        defaultElevation = 0.dp
                    ),
                    relleno = PaddingValues(22.dp),
                    separacion = 8.dp
                ) {
                    Text(
                        text = "TOTAL POR PAGAR",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = formatoMoneda.format(totalPendiente),
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "${deudas.size} deuda(s) pendiente(s)",
                        color = colores.onPrimaryContainer.copy(
                            alpha = 0.8f
                        )
                    )
                }
            }



            if (deudas.isEmpty() && !cargando) {
                item {
                    TarjetaSeccion(
                        forma = RoundedCornerShape(22.dp),
                        colores = CardDefaults.cardColors(
                            containerColor = colores.secondaryContainer,
                            contentColor = colores.onSecondaryContainer
                        ),
                        elevacion = CardDefaults.cardElevation(
                            defaultElevation = 0.dp
                        ),
                        relleno = PaddingValues(24.dp),
                        separacion = 10.dp
                    ) {
                        Text(
                            text = "¡Todo al día!",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "No tienes deudas pendientes por pagar."
                        )

                        BotonSecundario(
                            texto = "Ver historial",
                            onClick = onIrAHistorial,
                            altura = null,
                            forma = ButtonDefaults.outlinedShape,
                            colores = ButtonDefaults.outlinedButtonColors(
                                contentColor = colores.onSecondaryContainer
                            ),
                            borde = ButtonDefaults.outlinedButtonBorder,
                            relleno = ButtonDefaults.ContentPadding
                        )
                    }
                }
            }


            items(
                items = deudas,
                key = { deuda ->
                    deuda["id"].orEmpty()
                }
            ) { deuda ->
                val id = deuda["id"].orEmpty()
                val fecha = deuda["vencimiento"].orEmpty()
                val dias = ValidacionesDeuda.diasHasta(fecha)

                val monto = deuda["monto"]
                    ?.toDoubleOrNull() ?: 0.0

                TarjetaSeccion(
                    forma = RoundedCornerShape(20.dp),
                    colores = CardDefaults.cardColors(
                        containerColor = colores.surface,
                        contentColor = colores.onSurface
                    ),
                    elevacion = CardDefaults.cardElevation(
                        defaultElevation = 2.dp
                    ),
                    relleno = PaddingValues(20.dp),
                    separacion = 10.dp
                ) {
                    Text(
                        text = deuda["empresa"]
                            .orEmpty()
                            .ifBlank { "Empresa o servicio" },
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = deuda["concepto"].orEmpty(),
                        color = colores.onSurfaceVariant
                    )

                    Text(
                        text = "Vencimiento: $fecha",
                        color = colores.onSurfaceVariant,
                        fontSize = 13.sp
                    )

                    Text(
                        text = ValidacionesDeuda.textoVencimiento(fecha),
                        color = if (dias == null || dias < 0) {
                            colores.error
                        } else {
                            colores.primary
                        },
                        fontWeight = FontWeight.SemiBold
                    )

                    if (dias == null) {
                        Text(
                            text = "El administrador debe corregir esta fecha.",
                            color = colores.error,
                            fontSize = 12.sp
                        )
                    }

                    HorizontalDivider(
                        color = colores.outline.copy(
                            alpha = 0.3f
                        )
                    )

                    Text(
                        text = formatoMoneda.format(monto),
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold
                    )

                    BotonPrincipal(
                        texto = "Pagar esta deuda",
                        onClick = {
                            onPagarDeuda(id)
                        },
                        habilitado = !cargando &&
                                id.isNotBlank() &&
                                monto.isFinite() &&
                                monto > 0,
                        modifier = Modifier.fillMaxWidth(),
                        altura = 50.dp,
                        forma = RoundedCornerShape(12.dp),
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
}