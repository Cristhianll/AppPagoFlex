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
        NumberFormat.getCurrencyInstance(Locale("es", "CL"))
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
            NavigationBar(
                containerColor = colores.surface,
                contentColor = colores.onSurface
            ) {
                NavigationBarItem(
                    selected = false,
                    onClick = onVolverInicio,
                    icon = {
                        Text("🏠")
                    },
                    label = {
                        Text("Inicio")
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = colores.onSecondaryContainer,
                        selectedTextColor = colores.onSurface,
                        indicatorColor = colores.secondaryContainer,
                        unselectedIconColor = colores.onSurfaceVariant,
                        unselectedTextColor = colores.onSurfaceVariant
                    )
                )

                NavigationBarItem(
                    selected = false,
                    onClick = onIrAPagos,
                    icon = {
                        Text("📄")
                    },
                    label = {
                        Text("Pagos")
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = colores.onSecondaryContainer,
                        selectedTextColor = colores.onSurface,
                        indicatorColor = colores.secondaryContainer,
                        unselectedIconColor = colores.onSurfaceVariant,
                        unselectedTextColor = colores.onSurfaceVariant
                    )
                )

                NavigationBarItem(
                    selected = true,
                    onClick = {},
                    icon = {
                        Text("🕒")
                    },
                    label = {
                        Text("Historial")
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = colores.onSecondaryContainer,
                        selectedTextColor = colores.onSurface,
                        indicatorColor = colores.secondaryContainer,
                        unselectedIconColor = colores.onSurfaceVariant,
                        unselectedTextColor = colores.onSurfaceVariant
                    )
                )
            }
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
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = colores.surface,
                            contentColor = colores.onSurface
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
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
            }

            items(
                items = pagos,
                key = { pago ->
                    pago["id"].orEmpty()
                }
            ) { pago ->
                val concepto = pago["concepto"].orEmpty()
                val empresa = pago["empresa"].orEmpty()
                val monto = pago["monto"]?.toDoubleOrNull() ?: 0.0
                val fecha = pago["fecha"].orEmpty()

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
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
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = concepto.ifBlank { "Deuda pagada" },
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
                    }
                }
            }
        }
    }
}