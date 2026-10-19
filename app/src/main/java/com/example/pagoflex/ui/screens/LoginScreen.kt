package com.example.pagoflex.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.pagoflex.ui.theme.*

@Composable
fun LoginScreen(
    mensaje: String = "",
    onIngresar: (String, String) -> Unit,
    onRegistro: () -> Unit
) {
    var correo by remember { mutableStateOf("") }
    var clave by remember { mutableStateOf("") }
    var mostrarClave by remember { mutableStateOf(false) }
    var errorLocal by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(Modifier.height(28.dp))

        Box(
            modifier = Modifier
                .size(88.dp)
                .background(Primario, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.AccountBalanceWallet,
                contentDescription = "Logo PagoFlex",
                tint = Acento,
                modifier = Modifier.size(48.dp)
            )
        }

        Spacer(Modifier.height(16.dp))

        Text(
            text = "PagoFlex",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = Primario
        )

        Text(
            text = "Tus pagos, más simples.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(Modifier.height(28.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 3.dp
            )
        ) {
            Column(
                modifier = Modifier.padding(22.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Bienvenido",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = "Ingresa a tu cuenta para continuar.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                OutlinedTextField(
                    value = correo,
                    onValueChange = {
                        correo = it
                        errorLocal = ""
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Correo electrónico") },
                    placeholder = { Text("nombre@correo.cl") },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    isError = errorLocal.isNotEmpty() &&
                            !android.util.Patterns.EMAIL_ADDRESS
                                .matcher(correo.trim()).matches()
                )

                OutlinedTextField(
                    value = clave,
                    onValueChange = {
                        clave = it
                        errorLocal = ""
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Contraseña") },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    visualTransformation = if (mostrarClave) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                    trailingIcon = {
                        IconButton(
                            onClick = { mostrarClave = !mostrarClave }
                        ) {
                            Icon(
                                imageVector = if (mostrarClave) {
                                    Icons.Default.VisibilityOff
                                } else {
                                    Icons.Default.Visibility
                                },
                                contentDescription = if (mostrarClave) {
                                    "Ocultar contraseña"
                                } else {
                                    "Mostrar contraseña"
                                }
                            )
                        }
                    }
                )

                AnimatedVisibility(
                    visible = errorLocal.isNotEmpty() ||
                            mensaje.isNotEmpty()
                ) {
                    Text(
                        text = if (errorLocal.isNotEmpty()) {
                            errorLocal
                        } else {
                            mensaje
                        },
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Button(
                    onClick = {
                        when {
                            !android.util.Patterns.EMAIL_ADDRESS
                                .matcher(correo.trim()).matches() ->
                                errorLocal = "Ingresa un correo válido."

                            clave.isBlank() ->
                                errorLocal = "Ingresa tu contraseña."

                            else -> {
                                errorLocal = ""
                                onIngresar(correo.trim(), clave)
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Primario
                    )
                ) {
                    Text(
                        "Iniciar sesión",
                        fontWeight = FontWeight.SemiBold
                    )
                }

                OutlinedButton(
                    onClick = onRegistro,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Crear una cuenta")
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = PrimarioClaro
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    "Cuenta de demostración",
                    fontWeight = FontWeight.Bold,
                    color = Primario
                )
                Text(
                    "Administrador: admin@pagoflex.cl",
                    color = PrimarioOscuro,
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    "Contraseña: admin123",
                    color = PrimarioOscuro,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        Spacer(Modifier.height(28.dp))
    }
}