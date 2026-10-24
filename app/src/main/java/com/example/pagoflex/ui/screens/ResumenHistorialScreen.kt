package com.example.pagoflex.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pagoflex.ui.components.BarraNavegacionPagoFlex
import com.example.pagoflex.ui.components.SeccionPagoFlex
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

    val azulOscuro = Color(0xFF0C2E48)
    val azulMedio = Color(0xFF164E65)
    val verdeSuave = Color(0xFFCFFF9E)
    val verdeTexto = Color(0xFF254B2C)
    val celesteSuave = Color(0xFFDCEBF5)

    val formatoMoneda = remember {
        NumberFormat.getCurrencyInstance(Locale("es", "CL")).apply {
            maximumFractionDigits = 0
            minimumFractionDigits = 0
        }
    }

    val totalPagado = pagos.sumOf {
        it["monto"]?.toDoubleOrNull() ?: 0.0
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
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            if (pagos.isNotEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(24.dp))
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        azulOscuro,
                                        azulMedio
                                    )
                                )
                            )
                            .padding(22.dp)
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "TOTAL PAGADO",
                                    color = Color.White.copy(alpha = 0.85f),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    letterSpacing = 1.sp
                                )

                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .background(
                                            Color.White.copy(alpha = 0.12f),
                                            CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.ReceiptLong,
                                        contentDescription = null,
                                        tint = verdeSuave,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }

                            Text(
                                text = formatoMoneda.format(totalPagado),
                                color = Color.White,
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Surface(
                                color = verdeSuave,
                                contentColor = verdeTexto,
                                shape = RoundedCornerShape(50.dp)
                            ) {
                                Text(
                                    text = if (pagos.size == 1) {
                                        "1 pago registrado"
                                    } else {
                                        "${pagos.size} pagos registrados"
                                    },
                                    modifier = Modifier.padding(
                                        horizontal = 12.dp,
                                        vertical = 7.dp
                                    ),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }

                item {
                    Text(
                        text = "Tus pagos realizados",
                        modifier = Modifier.padding(
                            top = 6.dp,
                            bottom = 2.dp
                        ),
                        color = colores.onBackground,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = celesteSuave,
                            contentColor = azulOscuro
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(28.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .background(verdeSuave, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.ReceiptLong,
                                    contentDescription = null,
                                    tint = verdeTexto,
                                    modifier = Modifier.size(32.dp)
                                )
                            }

                            Text(
                                text = "Sin pagos registrados",
                                fontSize = 19.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = "Cuando pagues una deuda aparecerá aquí.",
                                fontSize = 14.sp,
                                textAlign =
                                    androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            }

            items(items = pagos) { pago ->
                val concepto = pago["concepto"].orEmpty()
                val empresa = pago["empresa"].orEmpty()
                val monto = pago["monto"]?.toDoubleOrNull() ?: 0.0
                val fecha = pago["fecha"].orEmpty()
                val tarjetaUsada = pago["tarjeta"].orEmpty()

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = colores.surface,
                        contentColor = colores.onSurface
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 2.dp
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .background(celesteSuave, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.CheckCircle,
                                    contentDescription = null,
                                    tint = azulOscuro,
                                    modifier = Modifier.size(26.dp)
                                )
                            }

                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Text(
                                    text = concepto.ifBlank { "Deuda pagada" },
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                if (empresa.isNotBlank()) {
                                    Text(
                                        text = empresa,
                                        color = colores.onSurfaceVariant,
                                        fontSize = 13.sp
                                    )
                                }
                            }

                            Surface(
                                color = verdeSuave,
                                contentColor = verdeTexto,
                                shape = RoundedCornerShape(50.dp)
                            ) {
                                Text(
                                    text = "Pagado",
                                    modifier = Modifier.padding(
                                        horizontal = 10.dp,
                                        vertical = 6.dp
                                    ),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = celesteSuave.copy(alpha = 0.55f),
                            contentColor = azulOscuro,
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(
                                    horizontal = 14.dp,
                                    vertical = 12.dp
                                ),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "Monto pagado",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )

                                Text(
                                    text = formatoMoneda.format(monto),
                                    fontSize = 25.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.DateRange,
                                contentDescription = null,
                                tint = colores.onSurfaceVariant,
                                modifier = Modifier.size(17.dp)
                            )

                            Text(
                                text = if (fecha.isNotBlank()) {
                                    "Pagado el ${fecha.take(10)}"
                                } else {
                                    "Fecha no disponible"
                                },
                                color = colores.onSurfaceVariant,
                                fontSize = 13.sp
                            )
                        }

                        if (tarjetaUsada.isNotBlank()) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.CreditCard,
                                    contentDescription = null,
                                    tint = colores.onSurfaceVariant,
                                    modifier = Modifier.size(17.dp)
                                )

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
    }
}