package com.example.pagoflex.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pagoflex.viewmodel.FormularioViewModel

val AzulOscuroFondo = Color(0xFF001F3F)
val VerdeClaroAccento = Color(0xFFD4ED91)
val BlancoTarjeta = Color.White
val TextoOscuroPrincipal = Color(0xFF0F172A)
val ColorBordeCampo = Color(0xFF64748B)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioScreen(
    viewModel: FormularioViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AzulOscuroFondo)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // --- CABECERA CON LOGO Y NOMBRE PAGOFLEX ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .background(VerdeClaroAccento, RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "P",
                        color = AzulOscuroFondo,
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

            Spacer(modifier = Modifier.height(8.dp))

            // Título de la pantalla
            Text(
                text = "Crear cuenta",
                color = VerdeClaroAccento,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Completa los datos en menos de un minuto",
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 15.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Tarjeta contenedora de Datos Personales
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = BlancoTarjeta),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "Datos personales",
                        color = AzulOscuroFondo,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )

                    // Campo Nombre
                    OutlinedTextField(
                        value = uiState.nombre,
                        onValueChange = { viewModel.onNombreCambia(it) },
                        label = { Text("Nombre completo") },
                        singleLine = true, // Evita saltos de línea accidentales
                        isError = uiState.errorNombre != null,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AzulOscuroFondo,
                            unfocusedBorderColor = ColorBordeCampo,
                            focusedTextColor = TextoOscuroPrincipal,
                            unfocusedTextColor = TextoOscuroPrincipal,
                            focusedLabelColor = AzulOscuroFondo,
                            unfocusedLabelColor = ColorBordeCampo
                        )
                    )
                    if (uiState.errorNombre != null) {
                        Text(text = uiState.errorNombre!!, color = MaterialTheme.colorScheme.error, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    // Campo Correo
                    OutlinedTextField(
                        value = uiState.correo,
                        onValueChange = { viewModel.onCorreoCambia(it) },
                        label = { Text("Correo electrónico") },
                        singleLine = true,
                        isError = uiState.errorCorreo != null,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AzulOscuroFondo,
                            unfocusedBorderColor = ColorBordeCampo,
                            focusedTextColor = TextoOscuroPrincipal,
                            unfocusedTextColor = TextoOscuroPrincipal,
                            focusedLabelColor = AzulOscuroFondo,
                            unfocusedLabelColor = ColorBordeCampo
                        )
                    )
                    if (uiState.errorCorreo != null) {
                        Text(text = uiState.errorCorreo!!, color = MaterialTheme.colorScheme.error, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    // Campo Contraseña
                    OutlinedTextField(
                        value = uiState.contrasena,
                        onValueChange = { viewModel.onContrasenaCambia(it) },
                        label = { Text("Contraseña") },
                        singleLine = true,
                        visualTransformation = if (uiState.contrasenaVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        isError = uiState.errorContrasena != null,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        trailingIcon = {
                            IconButton(onClick = { viewModel.onAlternarVisibilidad() }) {
                                Text(if (uiState.contrasenaVisible) "👁️" else "🫣", fontSize = 16.sp)
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AzulOscuroFondo,
                            unfocusedBorderColor = ColorBordeCampo,
                            focusedTextColor = TextoOscuroPrincipal,
                            unfocusedTextColor = TextoOscuroPrincipal,
                            focusedLabelColor = AzulOscuroFondo,
                            unfocusedLabelColor = ColorBordeCampo
                        )
                    )
                    if (uiState.errorContrasena != null) {
                        Text(text = uiState.errorContrasena!!, color = MaterialTheme.colorScheme.error, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    // Campo Repetir Contraseña
                    OutlinedTextField(
                        value = uiState.repetirContrasena,
                        onValueChange = { viewModel.onRepetirCambia(it) },
                        label = { Text("Repetir contraseña") },
                        singleLine = true,
                        visualTransformation = if (uiState.repetirVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        isError = uiState.errorRepetir != null,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        trailingIcon = {
                            IconButton(onClick = { viewModel.onAlternarRepetirVisibilidad() }) {
                                Text(if (uiState.repetirVisible) "👁️‍🗨️" else "🫣", fontSize = 16.sp)
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AzulOscuroFondo,
                            unfocusedBorderColor = ColorBordeCampo,
                            focusedTextColor = TextoOscuroPrincipal,
                            unfocusedTextColor = TextoOscuroPrincipal,
                            focusedLabelColor = AzulOscuroFondo,
                            unfocusedLabelColor = ColorBordeCampo
                        )
                    )
                    if (uiState.errorRepetir != null) {
                        Text(text = uiState.errorRepetir!!, color = MaterialTheme.colorScheme.error, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Tarjeta de Preferencias
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = BlancoTarjeta),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Preferencias",
                        color = AzulOscuroFondo,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Recibir novedades por correo", color = TextoOscuroPrincipal, fontSize = 14.sp)
                        Switch(
                            checked = uiState.recibirNoticias,
                            onCheckedChange = { viewModel.onNoticiasCambia(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = VerdeClaroAccento,
                                checkedTrackColor = AzulOscuroFondo
                            )
                        )
                    }
                }
            }

            // Términos y condiciones
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 4.dp)
                ) {
                    Checkbox(
                        checked = uiState.aceptaTerminos,
                        onCheckedChange = { viewModel.onTerminosCambia(it) },
                        colors = CheckboxDefaults.colors(
                            checkedColor = VerdeClaroAccento,
                            uncheckedColor = Color.White
                        )
                    )
                    Text("Acepto los términos y condiciones", color = Color.White, fontSize = 14.sp)
                }

                if (uiState.errorTerminos != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "⚠️ ${uiState.errorTerminos!!}",
                        color = VerdeClaroAccento,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Botones de acción inferiores
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = { viewModel.onLimpiar() },
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                ) {
                    Text("Limpiar", fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = { viewModel.onRegistrar() },
                    modifier = Modifier
                        .weight(1.5f)
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = VerdeClaroAccento,
                        contentColor = AzulOscuroFondo
                    )
                ) {
                    Text("✓ Crear cuenta", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}