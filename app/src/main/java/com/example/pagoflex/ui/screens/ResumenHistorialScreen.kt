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
import com.example.pagoflex.ui.components.BarraNavegacionPagoFlex
import com.example.pagoflex.ui.components.SeccionPagoFlex
import com.example.pagoflex.ui.components.TarjetaSeccion
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResumenHistorialScreen(
    onVolverInicio: () -> Unit,
    onIrAPagos: () -> Unit,
    pagos: List<Map<String, String>> = emptyList()
) {
    val colores = MaterialTheme.colorScheme

    val formatoMoneda = remember {
        NumberFormat.getCurrencyInstance(
            Locale("es", "CL")
        )
    }

    Scaffold(
        containerColor = colores.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Historial de pagos",
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colores.primary,
                    titleContentColor = colores.onPrimary
                )
            )
        },
        bottomBar = {
            BarraNavegacionPagoFlex(
                seccionActual = SeccionPagoFlex.HISTORIAL,
                onIrAInicio = onVolverInicio,
                onIrAPagos = onIrAPagos,
                onIrAHistorial = {}
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            if (pagos.isEmpty()) {
                item {
                    TarjetaSeccion(
                        forma = RoundedCornerShape(16.dp),
                        colores = CardDefaults.cardColors(
                            containerColor = colores.surface,
                            contentColor = colores.onSurface
                        ),
                        elevacion = CardDefaults.cardElevation(
                            defaultElevation = 0.dp
                        ),
                        relleno = PaddingValues(20.dp),
                        separacion = 8.dp
                    ) {
                        Text(
                            text = "Sin pagos registrados",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "Cuando pagues una deuda aparecerá aquí.",
                            color = colores.onSurfaceVariant,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            items(
                items = pagos,
                key = { pago ->
                    pago["id"].orEmpty()
                }
            ) { pago ->
                val concepto = pago["concepto"].orEmpty()
                val empresa = pago["empresa"].orEmpty()

                val monto = pago["monto"]
                    ?.toDoubleOrNull() ?: 0.0

                val fecha = pago["fecha"].orEmpty()
                val tarjetaUsada = pago["tarjeta"].orEmpty()

                TarjetaSeccion(
                    forma = RoundedCornerShape(16.dp),
                    colores = CardDefaults.cardColors(
                        containerColor = colores.surface,
                        contentColor = colores.onSurface
                    ),
                    elevacion = CardDefaults.cardElevation(
                        defaultElevation = 2.dp
                    ),
                    relleno = PaddingValues(18.dp),
                    separacion = 8.dp
                ) {
                    Text(
                        text = concepto.ifBlank {
                            "Deuda pagada"
                        },
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    if (empresa.isNotBlank()) {
                        Text(
                            text = empresa,
                            color = colores.onSurfaceVariant,
                            fontSize = 14.sp
                        )
                    }

                    Text(
                        text = formatoMoneda.format(monto),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = if (fecha.isNotBlank()) {
                            "Pagado el ${fecha.take(10)}"
                        } else {
                            "Pagado — fecha no disponible"
                        },
                        color = colores.primary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )

                    if (tarjetaUsada.isNotBlank()) {
                        Text(
                            text = "Pagado con $tarjetaUsada",
                            color = colores.onSurfaceVariant,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}