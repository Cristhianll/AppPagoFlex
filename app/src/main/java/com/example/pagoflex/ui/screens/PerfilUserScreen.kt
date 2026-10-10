package com.example.pagoflex.ui.screens


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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

private val AzulOscuro = Color(0xFF001F3F)
private val VerdeAccento = Color(0xFFD4ED91)
private val GrisClaro = Color(0xFFF1F5F9)
private val TextoOscuro = Color(0xFF0F172A)
private val GrisTexto = Color(0xFF64748B)

@Composable
fun PerfilUserScreen(
    nombreUsuario: String,
    onCerrar: () -> Unit,
    onCerrarSesionTotal: () -> Unit
) {
    val scrollState = rememberScrollState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(20.dp)
        ) {
            // Cabecera con botón de cerrar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Mi Cuenta y Ajustes",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = AzulOscuro
                )
                IconButton(onClick = onCerrar) {
                    Text("✕", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextoOscuro)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Avatar y nombre grande
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .background(VerdeAccento, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (nombreUsuario.isNotBlank()) nombreUsuario.take(1).uppercase() else "M",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = AzulOscuro
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = if (nombreUsuario.isBlank()) "Martina" else nombreUsuario,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextoOscuro
                )
                Text(
                    text = "martina.cliente@pagoflex.cl",
                    fontSize = 13.sp,
                    color = GrisTexto
                )
            }

            Spacer(modifier = Modifier.height(30.dp))

            // Sección: Tarjetas asociadas
            Text("💳 TARJETAS ASOCIADAS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GrisTexto, letterSpacing = 1.sp)
            Spacer(modifier = Modifier.height(10.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = GrisClaro)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("💳", fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Tarjeta de Crédito Visa", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextoOscuro)
                        Text("•••• 4582 • Expira 08/28", fontSize = 12.sp, color = GrisTexto)
                    }
                    Box(
                        modifier = Modifier
                            .background(AzulOscuro, RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text("Principal", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Sección: Configuración y Datos
            Text("⚙️ CONFIGURACIÓN", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GrisTexto, letterSpacing = 1.sp)
            Spacer(modifier = Modifier.height(10.dp))

            OpcionAjusteItem("👤", "Datos personales y seguridad")
            OpcionAjusteItem("🔔", "Notificaciones de vencimiento")
            OpcionAjusteItem("🛡️", "Privacidad y biometría")
            OpcionAjusteItem("❓", "Ayuda y soporte técnico")

            Spacer(modifier = Modifier.height(30.dp))

            // Botón Salir / Cerrar sesión
            Button(
                onClick = onCerrarSesionTotal,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
            ) {
                Text("Cerrar sesión", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun OpcionAjusteItem(icono: String, titulo: String) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        color = GrisClaro
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(icono, fontSize = 18.sp)
            Spacer(modifier = Modifier.width(14.dp))
            Text(titulo, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextoOscuro, modifier = Modifier.weight(1f))
            Text("›", fontSize = 18.sp, color = GrisTexto)
        }
    }
}