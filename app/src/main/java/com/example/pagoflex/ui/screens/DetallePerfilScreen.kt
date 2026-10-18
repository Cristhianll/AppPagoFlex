package com.example.pagoflex.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val AzulFondo = Color(0xFF001F3F)
private val VerdeAccento = Color(0xFFD4ED91)
private val TextoOscuro = Color(0xFF0F172A)
private val GrisClaro = Color(0xFFF1F5F9)
private val GrisTexto = Color(0xFF64748B)

@Composable
fun DetallePerfilScreen(
    nombreUsuario: String,
    correoUsuario: String,
    ultimosDigitosVisa: String,
    tarjetaPrincipal: Boolean,
    notificacionesActivas: Boolean = true,
    modoOscuroAutomatico: Boolean = false,
    biometriaActiva: Boolean = true,
    seccionInicial: String = "cuenta",
    onVolver: () -> Unit,
    onCerrarSesion: () -> Unit,
    onVincularTarjeta: (String) -> Unit = {},
    onAlternarNotificaciones: () -> Unit = {},
    onAlternarModoOscuro: () -> Unit = {},
    onAlternarBiometria: () -> Unit = {}
) {
    var pestanaSeleccionada by remember { mutableStateOf(seccionInicial) }
    val scrollState = rememberScrollState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF8FAFC)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onVolver) {
                    Text(
                        "←",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = AzulFondo
                    )
                }
                Text(
                    text = "Gestión de Perfil",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = AzulFondo
                )
                Spacer(modifier = Modifier.width(40.dp))
            }

            Spacer(modifier = Modifier.height(10.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(VerdeAccento, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (nombreUsuario.isNotBlank()) {
                            nombreUsuario.take(1).uppercase()
                        } else {
                            "U"
                        },
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = AzulFondo
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = nombreUsuario.ifBlank { "Usuario" },
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextoOscuro
                )
                Text(
                    text = "Cliente Registrado • PagoFlex",
                    fontSize = 12.sp,
                    color = GrisTexto
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                BotonPestanaPerfil(
                    "Cuenta",
                    pestanaSeleccionada == "cuenta"
                ) { pestanaSeleccionada = "cuenta" }
                BotonPestanaPerfil(
                    "Tarjetas",
                    pestanaSeleccionada == "tarjetas"
                ) { pestanaSeleccionada = "tarjetas" }
                BotonPestanaPerfil(
                    "Ajustes",
                    pestanaSeleccionada == "configuracion"
                ) { pestanaSeleccionada = "configuracion" }
            }

            Spacer(modifier = Modifier.height(20.dp))

            when (pestanaSeleccionada) {
                "cuenta" -> ContenidoMiCuenta(
                    nombre = nombreUsuario,
                    correo = correoUsuario
                )
                "tarjetas" -> ContenidoMisTarjetas(
                    ultimosDigitosVisa = ultimosDigitosVisa,
                    tarjetaPrincipal = tarjetaPrincipal,
                    onVincularTarjeta = onVincularTarjeta
                )
                "configuracion" -> ContenidoConfiguracion(
                    notificacionesActivas = notificacionesActivas,
                    modoOscuroAutomatico = modoOscuroAutomatico,
                    biometriaActiva = biometriaActiva,
                    onAlternarNotificaciones = onAlternarNotificaciones,
                    onAlternarModoOscuro = onAlternarModoOscuro,
                    onAlternarBiometria = onAlternarBiometria,
                    onCerrarSesion = onCerrarSesion
                )
            }
        }
    }
}

@Composable
fun BotonPestanaPerfil(
    titulo: String,
    seleccionado: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (seleccionado) AzulFondo else GrisClaro,
            contentColor = if (seleccionado) VerdeAccento else TextoOscuro
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.height(40.dp)
    ) {
        Text(titulo, fontSize = 13.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun ContenidoMiCuenta(nombre: String, correo: String) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            "INFORMACIÓN PERSONAL",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = GrisTexto
        )
        CardItemPerfil("Nombre completo", nombre.ifBlank { "Sin nombre" })
        CardItemPerfil("Correo electrónico", correo.ifBlank { "Sin correo" })
        CardItemPerfil("Estado de cuenta", "Activa / Verificada ✓")
    }
}

@Composable
fun ContenidoMisTarjetas(
    ultimosDigitosVisa: String,
    tarjetaPrincipal: Boolean,
    onVincularTarjeta: (String) -> Unit
) {
    var mostrarDialogo by remember { mutableStateOf(false) }
    var numeroTarjeta by remember { mutableStateOf("") }
    var mensajeError by remember { mutableStateOf("") }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            "MÉTODOS DE PAGO REGISTRADOS",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = GrisTexto
        )

        if (ultimosDigitosVisa.isNotBlank()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = AzulFondo)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "VISA Débito/Crédito",
                            color = VerdeAccento,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        if (tarjetaPrincipal) {
                            Text("Principal", color = Color.White, fontSize = 10.sp)
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "•••• •••• •••• $ultimosDigitosVisa",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )
                }
            }
        } else {
            Text(
                "Aún no tienes tarjetas vinculadas.",
                color = GrisTexto,
                fontSize = 13.sp
            )
        }

        Button(
            onClick = {
                numeroTarjeta = ""
                mensajeError = ""
                mostrarDialogo = true
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(46.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = VerdeAccento,
                contentColor = AzulFondo
            )
        ) {
            Text("+ Vincular nueva tarjeta", fontWeight = FontWeight.Bold)
        }
    }

    if (mostrarDialogo) {
        AlertDialog(
            onDismissRequest = { mostrarDialogo = false },
            title = {
                Text("Vincular tarjeta", fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        "Simulación: ingresa 16 dígitos (solo se guardan los últimos 4).",
                        fontSize = 13.sp,
                        color = GrisTexto
                    )
                    OutlinedTextField(
                        value = numeroTarjeta,
                        onValueChange = {
                            if (it.length <= 16 && it.all { c -> c.isDigit() }) {
                                numeroTarjeta = it
                                mensajeError = ""
                            }
                        },
                        label = { Text("Número de tarjeta") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (mensajeError.isNotBlank()) {
                        Text(
                            mensajeError,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val digitos = numeroTarjeta.filter { it.isDigit() }
                        if (digitos.length < 4) {
                            mensajeError = "Ingresa al menos 4 dígitos."
                        } else {
                            onVincularTarjeta(digitos)
                            mostrarDialogo = false
                        }
                    }
                ) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogo = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun ContenidoConfiguracion(
    notificacionesActivas: Boolean,
    modoOscuroAutomatico: Boolean,
    biometriaActiva: Boolean,
    onAlternarNotificaciones: () -> Unit,
    onAlternarModoOscuro: () -> Unit,
    onAlternarBiometria: () -> Unit,
    onCerrarSesion: () -> Unit
) {
    var mostrarTerminos by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            "PREFERENCIAS DE LA APLICACIÓN",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = GrisTexto
        )

        OpcionSwitchItem(
            icono = "🔔",
            titulo = "Notificaciones push de vencimiento",
            activado = notificacionesActivas,
            onCambiar = onAlternarNotificaciones
        )
        OpcionSwitchItem(
            icono = "🌙",
            titulo = "Modo oscuro automático",
            activado = modoOscuroAutomatico,
            onCambiar = onAlternarModoOscuro
        )
        OpcionSwitchItem(
            icono = "🛡️",
            titulo = "Autenticación biométrica (Huella)",
            activado = biometriaActiva,
            onCambiar = onAlternarBiometria
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            onClick = { mostrarTerminos = true }
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("📄", fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        "Términos y condiciones",
                        fontSize = 13.sp,
                        color = TextoOscuro,
                        fontWeight = FontWeight.Medium
                    )
                }
                Text(
                    "Ver documento",
                    fontSize = 12.sp,
                    color = GrisTexto,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = onCerrarSesion,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFEF4444)
            )
        ) {
            Text(
                "Cerrar sesión actual",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }

    if (mostrarTerminos) {
        AlertDialog(
            onDismissRequest = { mostrarTerminos = false },
            title = { Text("Términos y condiciones", fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "PagoFlex es una aplicación de demostración académica. " +
                            "Los pagos, tarjetas y datos son simulados. " +
                            "No se procesan transacciones reales ni se almacenan " +
                            "datos bancarios fuera del dispositivo.",
                    fontSize = 14.sp
                )
            },
            confirmButton = {
                TextButton(onClick = { mostrarTerminos = false }) {
                    Text("Cerrar")
                }
            }
        )
    }
}

@Composable
fun CardItemPerfil(label: String, valor: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                label,
                fontSize = 11.sp,
                color = GrisTexto,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                valor,
                fontSize = 14.sp,
                color = TextoOscuro,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun OpcionSwitchItem(
    icono: String,
    titulo: String,
    activado: Boolean,
    onCambiar: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Text(icono, fontSize = 18.sp)
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    titulo,
                    fontSize = 13.sp,
                    color = TextoOscuro,
                    fontWeight = FontWeight.Medium
                )
            }
            Switch(
                checked = activado,
                onCheckedChange = { onCambiar() },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = VerdeAccento,
                    checkedTrackColor = AzulFondo
                )
            )
        }
    }
}