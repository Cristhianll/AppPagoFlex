package com.example.pagoflex.ui.screens



import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
    seccionInicial: String = "cuenta", // "cuenta", "tarjetas", "configuracion"
    onVolver: () -> Unit,
    onCerrarSesion: () -> Unit
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
            // Cabecera con botón de retroceso
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onVolver) {
                    Text("←", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = AzulFondo)
                }
                Text(
                    text = "Gestión de Perfil",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = AzulFondo
                )
                Spacer(modifier = Modifier.width(40.dp)) // Para equilibrar el espacio
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Avatar central
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
                        text = if (nombreUsuario.isNotBlank()) nombreUsuario.take(1).uppercase() else "M",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = AzulFondo
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (nombreUsuario.isBlank()) "Martina" else nombreUsuario,
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

            // Pestañas de navegación interna
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                BotonPestanaPerfil("Cuenta", pestanaSeleccionada == "cuenta") { pestanaSeleccionada = "cuenta" }
                BotonPestanaPerfil("Tarjetas", pestanaSeleccionada == "tarjetas") { pestanaSeleccionada = "tarjetas" }
                BotonPestanaPerfil("Ajustes", pestanaSeleccionada == "configuracion") { pestanaSeleccionada = "configuracion" }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Contenido dinámico según la pestaña seleccionada
            when (pestanaSeleccionada) {
                "cuenta" -> ContenidoMiCuenta(nombre = nombreUsuario,correo = correoUsuario)
                "tarjetas" -> ContenidoMisTarjetas(
                    ultimosDigitosVisa = ultimosDigitosVisa,
                    tarjetaPrincipal = tarjetaPrincipal
                )
                "configuracion" -> ContenidoConfiguracion(onCerrarSesion = onCerrarSesion)
            }
        }
    }
}

@Composable
fun BotonPestanaPerfil(titulo: String, seleccionado: Boolean, onClick: () -> Unit) {
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
        Text("INFORMACIÓN PERSONAL", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GrisTexto)

        CardItemPerfil("Nombre completo", nombre.ifBlank { "sin Nombre" })
        CardItemPerfil("Correo electrónico", correo.ifBlank { "sin Correo" })
        CardItemPerfil("Estado de cuenta", "Activa / Verificada ✓")
    }
}

// Muestra los datos de las tarjetas
@Composable
fun ContenidoMisTarjetas(
    ultimosDigitosVisa: String,
    tarjetaPrincipal: Boolean
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            "MÉTODOS DE PAGO REGISTRADOS",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = GrisTexto
        )

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
                        Text(
                            "Principal",
                            color = Color.White,
                            fontSize = 10.sp
                        )
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

        Button(
            onClick = { /* Acción para añadir tarjeta */ },
            modifier = Modifier.fillMaxWidth().height(46.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = VerdeAccento,
                contentColor = AzulFondo
            )
        ) {
            Text("+ Vincular nueva tarjeta", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ContenidoConfiguracion(onCerrarSesion: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("PREFERENCIAS DE LA APLICACIÓN", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GrisTexto)

        OpcionConfigItem("🔔", "Notificaciones push de vencimiento", "Activado")
        OpcionConfigItem("🌙", "Modo oscuro automático", "Desactivado")
        OpcionConfigItem("🛡️", "Autenticación biométrica (Huella)", "Activado")
        OpcionConfigItem("📄", "Términos y condiciones", "Ver documento")

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = onCerrarSesion,
            modifier = Modifier.fillMaxWidth().height(48.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
        ) {
            Text("Cerrar sesión actual", color = Color.White, fontWeight = FontWeight.Bold)
        }
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
            Text(label, fontSize = 11.sp, color = GrisTexto, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(2.dp))
            Text(valor, fontSize = 14.sp, color = TextoOscuro, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun OpcionConfigItem(icono: String, titulo: String, estado: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Text(icono, fontSize = 18.sp)
                Spacer(modifier = Modifier.width(12.dp))
                Text(titulo, fontSize = 13.sp, color = TextoOscuro, fontWeight = FontWeight.Medium)
            }
            Text(estado, fontSize = 12.sp, color = GrisTexto, fontWeight = FontWeight.Bold)
        }
    }
}