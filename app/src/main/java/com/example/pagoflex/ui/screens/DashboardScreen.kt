package com.example.pagoflex.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pagoflex.validation.ValidacionesDeuda
import com.example.pagoflex.ui.components.BarraNavegacionPagoFlex
import com.example.pagoflex.ui.components.BotonPrincipal
import com.example.pagoflex.ui.components.MarcaHorizontalPagoFlex
import com.example.pagoflex.ui.components.SeccionPagoFlex
import com.example.pagoflex.ui.components.TarjetaSeccion
import java.text.NumberFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    nombreUsuario: String,
    deudasPendientes: List<Map<String, String>>,
    onPagarDeuda: (String) -> Unit,
    onIrAPagos: () -> Unit,
    onIrAHistorial: () -> Unit,
    onSeleccionarOpcionPerfil: (String) -> Unit,
    onActualizar: () -> Unit = {},
    cargando: Boolean = false
) {
    val colores = MaterialTheme.colorScheme

    var menuExpandido by remember {
        mutableStateOf(false)
    }

    val formatoMoneda = remember {
        NumberFormat.getCurrencyInstance(
            Locale("es", "CL")
        )
    }

    val deudasOrdenadas = deudasPendientes
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

    val totalPendiente = deudasOrdenadas.sumOf {
        it["monto"]?.toDoubleOrNull() ?: 0.0
    }

    val saludo = when (
        Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    ) {
        in 5..11 -> "BUENOS DÍAS"
        in 12..19 -> "BUENAS TARDES"
        else -> "BUENAS NOCHES"
    }

    Scaffold(
        containerColor = colores.background,
        topBar = {
            TopAppBar(
                title = {
                    MarcaHorizontalPagoFlex(
                        tamanoIcono = 34.dp,
                        colorPago = colores.onPrimary,
                        colorFlex = colores.tertiary
                    )
                },
                actions = {
                    Box {
                        IconButton(
                            onClick = {
                                menuExpandido = true
                            }
                        ) {
                            Surface(
                                modifier = Modifier.size(36.dp),
                                shape = CircleShape,
                                color = colores.secondary,
                                contentColor = colores.onSecondary
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = nombreUsuario
                                            .take(1)
                                            .uppercase()
                                            .ifBlank { "U" },
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        DropdownMenu(
                            expanded = menuExpandido,
                            onDismissRequest = {
                                menuExpandido = false
                            }
                        ) {
                            listOf(
                                "cuenta" to "Mi cuenta y datos",
                                "editar" to "Editar mis datos",
                                "tarjetas" to "Mis tarjetas",
                                "configuracion" to "Configuración",
                                "salir" to "Cerrar sesión"
                            ).forEach { (opcion, titulo) ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = titulo,
                                            color = if (opcion == "salir") {
                                                colores.error
                                            } else {
                                                colores.onSurface
                                            }
                                        )
                                    },
                                    onClick = {
                                        menuExpandido = false
                                        onSeleccionarOpcionPerfil(opcion)
                                    }
                                )
                            }
                        }
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
                seccionActual = SeccionPagoFlex.INICIO,
                onIrAInicio = {},
                onIrAPagos = onIrAPagos,
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
            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = saludo,
                        color = colores.primary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "¡Hola, ${
                            nombreUsuario.ifBlank { "Usuario" }
                        }!",
                        color = colores.onBackground,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Tus deudas y vencimientos, en un solo lugar.",
                        color = colores.onSurfaceVariant
                    )

                    TextButton(
                        onClick = onActualizar,
                        enabled = !cargando
                    ) {
                        Text(
                            text = if (cargando) {
                                "Actualizando..."
                            } else {
                                "Actualizar deudas"
                            }
                        )
                    }

                    if (cargando) {
                        LinearProgressIndicator(
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            item {
                TarjetaSeccion(
                    forma = RoundedCornerShape(22.dp),
                    colores = CardDefaults.cardColors(
                        containerColor = colores.surface,
                        contentColor = colores.onSurface
                    ),
                    elevacion = CardDefaults.cardElevation(
                        defaultElevation = 2.dp
                    ),
                    relleno = PaddingValues(20.dp),
                    separacion = 6.dp
                ) {
                    Text(
                        text = "TOTAL PENDIENTE",
                        color = colores.onSurfaceVariant,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )

                    Text(
                        text = formatoMoneda.format(totalPendiente),
                        fontWeight = FontWeight.Bold,
                        fontSize = 30.sp
                    )

                    Text(
                        text = "${deudasOrdenadas.size} deuda(s) pendiente(s)",
                        color = colores.onSurfaceVariant
                    )
                }
            }

            if (deudasOrdenadas.isEmpty() && !cargando) {
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
                        separacion = 8.dp
                    ) {
                        Text(
                            text = "¡Todo al día!",
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp
                        )

                        Text(
                            text = "No tienes deudas pendientes."
                        )
                    }
                }
            }

            itemsIndexed(
                items = deudasOrdenadas,
                key = { _, deuda ->
                    deuda["id"].orEmpty()
                }
            ) { indice, deuda ->
                val id = deuda["id"].orEmpty()
                val fecha = deuda["vencimiento"].orEmpty()

                val monto = deuda["monto"]
                    ?.toDoubleOrNull() ?: 0.0

                val dias = ValidacionesDeuda.diasHasta(fecha)

                TarjetaSeccion(
                    forma = RoundedCornerShape(24.dp),
                    colores = CardDefaults.cardColors(
                        containerColor = colores.surface,
                        contentColor = colores.onSurface
                    ),
                    elevacion = CardDefaults.cardElevation(
                        defaultElevation = 2.dp
                    ),
                    relleno = PaddingValues(20.dp),
                    separacion = 12.dp
                ) {
                    Text(
                        text = if (indice == 0) {
                            "PRIMERA CUENTA POR ATENDER"
                        } else {
                            "CUENTA PENDIENTE"
                        },
                        color = colores.onSurfaceVariant,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = deuda["empresa"]
                            .orEmpty()
                            .ifBlank { "Empresa o servicio" },
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = deuda["concepto"].orEmpty(),
                        color = colores.onSurfaceVariant
                    )

                    Text(
                        text = "Vencimiento: $fecha",
                        color = colores.onSurfaceVariant
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
                        fontSize = 28.sp,
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
                        forma = RoundedCornerShape(14.dp),
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