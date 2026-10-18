package com.example.pagoflex.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pagoflex.viewmodel.FormularioViewModel

@Composable
fun FormularioScreen(
    viewModel: FormularioViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val colores = MaterialTheme.colorScheme

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = colores.background,
        contentColor = colores.onBackground
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(40.dp),
                    shape = RoundedCornerShape(10.dp),
                    color = colores.secondary,
                    contentColor = colores.onSecondary
                ) {
                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "P",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(Modifier.width(10.dp))

                Text(
                    text = "PagoFlex",
                    color = colores.onBackground,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = "Crear cuenta",
                color = colores.primary,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Completa tus datos para registrarte.",
                color = colores.onSurfaceVariant,
                fontSize = 15.sp
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = colores.surface,
                    contentColor = colores.onSurface
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 2.dp
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "Datos personales",
                        color = colores.primary,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )

                    OutlinedTextField(
                        value = uiState.nombre,
                        onValueChange = {
                            viewModel.onNombreCambia(it)
                        },
                        label = {
                            Text("Nombre completo")
                        },
                        singleLine = true,
                        isError = uiState.errorNombre != null,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    uiState.errorNombre?.let {
                        ErrorFormulario(texto = it)
                    }

                    OutlinedTextField(
                        value = uiState.correo,
                        onValueChange = {
                            viewModel.onCorreoCambia(it)
                        },
                        label = {
                            Text("Correo electrónico")
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email
                        ),
                        singleLine = true,
                        isError = uiState.errorCorreo != null,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    uiState.errorCorreo?.let {
                        ErrorFormulario(texto = it)
                    }

                    OutlinedTextField(
                        value = uiState.contrasena,
                        onValueChange = {
                            viewModel.onContrasenaCambia(it)
                        },
                        label = {
                            Text("Contraseña")
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password
                        ),
                        visualTransformation = if (
                            uiState.contrasenaVisible
                        ) {
                            VisualTransformation.None
                        } else {
                            PasswordVisualTransformation()
                        },
                        isError = uiState.errorContrasena != null,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        trailingIcon = {
                            TextButton(
                                onClick = {
                                    viewModel.onAlternarVisibilidad()
                                }
                            ) {
                                Text(
                                    text = if (uiState.contrasenaVisible) {
                                        "Ocultar"
                                    } else {
                                        "Ver"
                                    }
                                )
                            }
                        }
                    )

                    uiState.errorContrasena?.let {
                        ErrorFormulario(texto = it)
                    }

                    OutlinedTextField(
                        value = uiState.repetirContrasena,
                        onValueChange = {
                            viewModel.onRepetirCambia(it)
                        },
                        label = {
                            Text("Repetir contraseña")
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password
                        ),
                        visualTransformation = if (
                            uiState.repetirVisible
                        ) {
                            VisualTransformation.None
                        } else {
                            PasswordVisualTransformation()
                        },
                        isError = uiState.errorRepetir != null,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        trailingIcon = {
                            TextButton(
                                onClick = {
                                    viewModel.onAlternarRepetirVisibilidad()
                                }
                            ) {
                                Text(
                                    text = if (uiState.repetirVisible) {
                                        "Ocultar"
                                    } else {
                                        "Ver"
                                    }
                                )
                            }
                        }
                    )

                    uiState.errorRepetir?.let {
                        ErrorFormulario(texto = it)
                    }
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = colores.surface,
                    contentColor = colores.onSurface
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Preferencias",
                        color = colores.primary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Recibir novedades por correo",
                            modifier = Modifier.weight(1f),
                            color = colores.onSurface,
                            fontSize = 14.sp
                        )

                        Switch(
                            checked = uiState.recibirNoticias,
                            onCheckedChange = {
                                viewModel.onNoticiasCambia(it)
                            }
                        )
                    }
                }
            }

            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = uiState.aceptaTerminos,
                        onCheckedChange = {
                            viewModel.onTerminosCambia(it)
                        }
                    )

                    Text(
                        text = "Acepto los términos y condiciones",
                        modifier = Modifier.weight(1f),
                        color = colores.onBackground,
                        fontSize = 14.sp
                    )
                }

                uiState.errorTerminos?.let {
                    ErrorFormulario(texto = it)
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        viewModel.onLimpiar()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = "Limpiar",
                        fontWeight = FontWeight.Bold
                    )
                }

                Button(
                    onClick = {
                        viewModel.onRegistrar()
                    },
                    modifier = Modifier
                        .weight(1.5f)
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colores.secondary,
                        contentColor = colores.onSecondary
                    )
                ) {
                    Text(
                        text = "Crear cuenta",
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ErrorFormulario(
    texto: String
) {
    Text(
        text = texto,
        color = MaterialTheme.colorScheme.error,
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium
    )
}