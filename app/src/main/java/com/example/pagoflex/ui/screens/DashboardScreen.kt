package com.example.pagoflex.ui.screens

import androidx.compose.foundation.background
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
import java.text.NumberFormat
import java.util.Locale

private val DashAzulFondo = Color(0xFF001F3F)
private val DashVerdeAccento = Color(0xFFD4ED91)
private val DashTextoBlancoSuave = Color(0xFFE2E8F0)
private val DashGrisClaro = Color(0xFF64748B)
private val DashTextoPrincipal = Color(0xFF0F172A)
private val DashFondoBlanca = Color(0xFFF8FAFC)

@Composable
fun DashboardScreen(
    nombreUsuario: String,
    deudasPendientes: List<Map<String, String>> = emptyList(),
    onPagarDeuda: (String) -> Unit,
    onIrAPagos: () -> Unit,
    onIrAHistorial: () -> Unit,
    onSeleccionarOpcionPerfil: (String) -> Unit
) {
    val scrollState = rememberScrollState()
    var menuExpandido by remember { mutableStateOf(false) }
    val formato = NumberFormat.getCurrencyInstance(Locale("es", "CL"))
    val totalPendiente = deudasPendientes.sumOf {
        it["monto"]?.toDoubleOrNull() ?: 0.0
    }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    icon = { Text("🏠", fontSize = 20.sp) },
                    label = { Text("Inicio") },
                    selected = true,
                    onClick = { }
                )
                NavigationBarItem(
                    icon = { Text("📄", fontSize = 20.sp) },
                    label = { Text("Pagos") },
                    selected = false,
                    onClick = onIrAPagos
                )
                NavigationBarItem(
                    icon = { Text("🕒", fontSize = 20.sp) },
                    label = { Text("Historial") },
                    selected = false,
                    onClick = onIrAHistorial
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(DashAzulFondo)
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(DashVerdeAccento, RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "P",
                            color = DashAzulFondo,
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "PagoFlex",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }

                Box {
                    IconButton(onClick = { menuExpandido = true }) {
                        Surface(
                            shape = androidx.compose.foundation.shape.CircleShape,
                            color = DashVerdeAccento,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = if (nombreUsuario.isNotBlank()) {
                                        nombreUsuario.take(1).uppercase()
                                    } else {
                                        "U"
                                    },
                                    color = DashAzulFondo,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                )
                            }
                        }
                    }

                    DropdownMenu(
                        expanded = menuExpandido,
                        onDismissRequest = { menuExpandido = false },
                        modifier = Modifier.background(Color.White)
                    ) {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    "👤 Mi Cuenta y Datos",
                                    fontWeight = FontWeight.Bold,
                                    color = DashTextoPrincipal
                                )
                            },
                            onClick = {
                                menuExpandido = false
                                onSeleccionarOpcionPerfil("cuenta")
                            }
                        )
                        DropdownMenuItem(
                            text = {
                                Text("💳 Mis Tarjetas", color = DashTextoPrincipal)
                            },
                            onClick = {
                                menuExpandido = false
                                onSeleccionarOpcionPerfil("tarjetas")
                            }
                        )
                        DropdownMenuItem(
                            text = {
                                Text("⚙️ Configuración", color = DashTextoPrincipal)
                            },
                            onClick = {
                                menuExpandido = false
                                onSeleccionarOpcionPerfil("configuracion")
                            }
                        )
                        HorizontalDivider()
                        DropdownMenuItem(
                            text = {
                                Text(
                                    "🚪 Cerrar Sesión",
                                    color = Color(0xFFEF4444),
                                    fontWeight = FontWeight.Bold
                                )
                            },
                            onClick = {
                                menuExpandido = false
                                onSeleccionarOpcionPerfil("salir")
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "BUENOS DÍAS",
                color = DashVerdeAccento,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "¡Hola, ${nombreUsuario.ifBlank { "Usuario" }}!",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Tu tranquilidad financiera, al día.",
                color = DashTextoBlancoSuave,
                fontSize = 15.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            if (deudasPendientes.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                "TOTAL PENDIENTE",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = DashGrisClaro
                            )
                            Text(
                                formato.format(totalPendiente),
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = DashTextoPrincipal
                            )
                        }
                        Text(
                            "${deudasPendientes.size} deuda(s)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = DashAzulFondo
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    "Cuentas por pagar",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(10.dp))

                deudasPendientes.forEach { deuda ->
                    val monto = deuda["monto"]?.toDoubleOrNull() ?: 0.0
                    val id = deuda["id"].orEmpty()

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "PENDIENTE",
                                    color = DashGrisClaro,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp
                                )
                                Box(
                                    modifier = Modifier
                                        .background(Color.Black, RoundedCornerShape(10.dp))
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        "Vence ${deuda["vencimiento"].orEmpty()}",
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .background(DashFondoBlanca, RoundedCornerShape(12.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("🏢", fontSize = 20.sp)
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        deuda["empresa"].orEmpty()
                                            .ifBlank { deuda["concepto"].orEmpty() },
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = DashTextoPrincipal
                                    )
                                    if (deuda["concepto"].orEmpty() != deuda["empresa"].orEmpty()) {
                                        Text(
                                            deuda["concepto"].orEmpty(),
                                            color = DashGrisClaro,
                                            fontSize = 13.sp
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))
                            HorizontalDivider(color = DashFondoBlanca)
                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    formato.format(monto),
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DashTextoPrincipal
                                )
                                Button(
                                    onClick = { onPagarDeuda(id) },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = DashVerdeAccento,
                                        contentColor = DashAzulFondo
                                    ),
                                    contentPadding = PaddingValues(
                                        horizontal = 16.dp,
                                        vertical = 8.dp
                                    )
                                ) {
                                    Text(
                                        "Pagar",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("✅", fontSize = 36.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            "¡Todo al día!",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = DashTextoPrincipal
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            "No tienes deudas pendientes por pagar.",
                            color = DashGrisClaro,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = onIrAHistorial,
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = DashVerdeAccento,
                                contentColor = DashAzulFondo
                            )
                        ) {
                            Text(
                                "Ver movimientos de pago",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DashVerdeAccento)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("📈", fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "¡4 meses en racha pagando!",
                            color = DashAzulFondo,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "Sigue pagando a tiempo.",
                            color = DashAzulFondo.copy(alpha = 0.8f),
                            fontSize = 11.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}