package com.example.pagoflex.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


private val DashAzulFondo = Color(0xFF001F3F)
private val DashVerdeAccento = Color(0xFFD4ED91)
private val DashTextoBlancoSuave = Color(0xFFE2E8F0)
private val DashGrisClaro = Color(0xFF64748B)
private val DashTextoPrincipal = Color(0xFF0F172A)
private val DashFondoBlanca = Color(0xFFF8FAFC)

/**
 * PANTALLA: DashboardScreen adaptada con los componentes institucionales del profesor.
 */
@Composable
fun DashboardScreen(
    nombreUsuario: String,
    deudaPendiente: Boolean,
    onIrADetallePago: () -> Unit,
    onIrAPagos: () -> Unit,
    onIrAHistorial: () -> Unit,
    onSeleccionarOpcionPerfil: (String) -> Unit
) {
    val scrollState = rememberScrollState()

    // Estado para controlar el mini menú desplegable del perfil
    var menuExpandido by remember { mutableStateOf(false) }

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

            // --- CABECERA CON COMPONENTES DE PERFIL Y LOGO ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(DashVerdeAccento, androidx.compose.foundation.shape.RoundedCornerShape(10.dp)),
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

                // Utilizando el componente AvatarCircular provisto en los componentes del profesor
                Box {
                    // Si el profesor estructuró AvatarCircular para recibir texto o imagen:
                    Box(
                        modifier = Modifier.size(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(onClick = { menuExpandido = true }) {
                            Surface(
                                shape = androidx.compose.foundation.shape.CircleShape,
                                color = DashVerdeAccento,
                                modifier = Modifier.size(40.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = if (nombreUsuario.isNotBlank()) nombreUsuario.take(1).uppercase() else "M",
                                        color = DashAzulFondo,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp
                                    )
                                }
                            }
                        }
                    }

                    // Mini Menú Desplegable con las opciones del sistema
                    DropdownMenu(
                        expanded = menuExpandido,
                        onDismissRequest = { menuExpandido = false },
                        modifier = Modifier.background(Color.White)
                    ) {
                        DropdownMenuItem(
                            text = { Text("👤 Mi Cuenta y Datos", fontWeight = FontWeight.Bold, color = DashTextoPrincipal) },
                            onClick = {
                                menuExpandido = false
                                onSeleccionarOpcionPerfil("cuenta")
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("💳 Mis Tarjetas", color = DashTextoPrincipal) },
                            onClick = {
                                menuExpandido = false
                                onSeleccionarOpcionPerfil("tarjetas")
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("⚙️ Configuración", color = DashTextoPrincipal) },
                            onClick = {
                                menuExpandido = false
                                onSeleccionarOpcionPerfil("configuracion")
                            }
                        )
                        HorizontalDivider()
                        DropdownMenuItem(
                            text = { Text("🚪 Cerrar Sesión", color = Color(0xFFEF4444), fontWeight = FontWeight.Bold) },
                            onClick = {
                                menuExpandido = false
                                onSeleccionarOpcionPerfil("salir")
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- SALUDO ---
            Text(
                text = "BUENOS DÍAS",
                color = DashVerdeAccento,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "¡Hola, ${if (nombreUsuario.isBlank()) "Martina" else nombreUsuario}!",
                color = Color.White,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Tu tranquilidad financiera, al día.",
                color = DashTextoBlancoSuave,
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            // --- TARJETA DE PRÓXIMO VENCIMIENTO (Utilizando estructura de TarjetaSeccion) ---
            if(deudaPendiente) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "PRÓXIMO VENCIMIENTO",
                                color = DashGrisClaro,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                            Box(
                                modifier = Modifier
                                    .background(
                                        Color.Black,
                                        androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
                                    )
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    " 5 días",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(
                                        DashFondoBlanca,
                                        androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("🏢", fontSize = 24.sp)
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    "Inmobiliaria del Parque",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = DashTextoPrincipal
                                )
                                Text("📅 Vence 15 Oct", color = DashGrisClaro, fontSize = 14.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))
                        HorizontalDivider(color = DashFondoBlanca)
                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "MONTO A PAGAR",
                            color = DashGrisClaro,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "$125,000",
                                fontSize = 34.sp,
                                fontWeight = FontWeight.Bold,
                                color = DashTextoPrincipal
                            )
                            Text(
                                "CLP",
                                color = DashGrisClaro,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Reemplazando el botón estándar por BotonPrincipal si está disponible en tus componentes
                        Button(
                            onClick = onIrADetallePago,
                            modifier = Modifier.fillMaxWidth().height(52.dp),
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = DashVerdeAccento,
                                contentColor = DashAzulFondo
                            )
                        ) {
                            Text("Pagar ahora", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.weight(1f))
                            Text("➔", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- TARJETA DE RACHA DE PAGOS (Compatible con TarjetaEstadistica) ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = DashVerdeAccento)
            ) {
                Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.Top) {
                    Text("📈", fontSize = 28.sp)
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "TU RACHA DE PAGOS",
                            color = DashAzulFondo.copy(alpha = 0.7f),
                            fontSize = 12.sp,
                            letterSpacing = 0.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "¡Llevas 4 meses en racha pagando!",
                            color = DashAzulFondo,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Continúa pagando a tiempo para mantener tu buen hábito financiero.",
                            color = DashAzulFondo.copy(alpha = 0.85f),
                            fontSize = 13.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}