package com.example.pagoflex.ui.screens

import android.util.Patterns
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.pagoflex.ui.components.BotonPrincipal
import com.example.pagoflex.ui.components.BotonSecundario
import com.example.pagoflex.ui.components.CampoContrasena
import com.example.pagoflex.ui.components.CampoTexto
import com.example.pagoflex.ui.components.TarjetaSeccion
import com.example.pagoflex.ui.theme.Acento
import com.example.pagoflex.ui.theme.Blanco
import com.example.pagoflex.ui.theme.LocalControlTemaPagoFlex
import com.example.pagoflex.ui.theme.PrimarioOscuro

/**
 * Pantalla de inicio de sesión.
 *
 * Recibe los datos y las acciones desde el coordinador.
 * No consulta la base de datos ni decide la navegación.
 */
@Suppress("DEPRECATION")
@Composable
fun LoginScreen(
    correo: String,
    clave: String,
    mensaje: String,
    ocupado: Boolean,
    onCorreo: (String) -> Unit,
    onClave: (String) -> Unit,
    onEntrar: () -> Unit,
    onRegistro: () -> Unit
) {
    val colores = MaterialTheme.colorScheme
    val tema = LocalControlTemaPagoFlex.current

    // =====================================================
    // COLORES DEL LOGIN
    // =====================================================

    val colorFondo = if (tema.oscuro) {
        colores.background
    } else {
        PrimarioOscuro
    }

    val colorFondoLogo = Acento
    val colorLetraLogo = PrimarioOscuro

    val colorTitulo = Blanco
    val colorSubtitulo = Blanco.copy(alpha = 0.85f)

    val colorTarjeta = colores.surface
    val colorTextoTarjeta = colores.onSurface

    val colorMensaje = colores.onSurfaceVariant
    val colorError = colores.error

    val colorBotonIngresar = colores.secondary
    val colorTextoBotonIngresar = colores.onSecondary

    val colorTituloDemo = Acento
    val colorTextoDemo = Blanco.copy(alpha = 0.85f)

    // =====================================================
    // ESTADO LOCAL DE LA INTERFAZ
    // =====================================================

    var mostrarClave by remember {
        mutableStateOf(false)
    }

    var errorLocal by remember {
        mutableStateOf("")
    }

    // =====================================================
    // CONTENIDO DE LA PANTALLA
    // =====================================================

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = colorFondo,
        contentColor = Blanco
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // LOGO

            Surface(
                modifier = Modifier.size(64.dp),
                shape = RoundedCornerShape(16.dp),
                color = colorFondoLogo,
                contentColor = colorLetraLogo
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "P",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Text(
                text = "PagoFlex",
                color = colorTitulo,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Tus pagos, más simples",
                color = colorSubtitulo
            )

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            // TARJETA DEL FORMULARIO

            TarjetaSeccion(
                forma = RoundedCornerShape(24.dp),
                colores = CardDefaults.cardColors(
                    containerColor = colorTarjeta,
                    contentColor = colorTextoTarjeta
                ),
                elevacion = CardDefaults.cardElevation(
                    defaultElevation = 0.dp
                ),
                relleno = PaddingValues(20.dp),
                separacion = 14.dp
            ) {
                Text(
                    text = "Iniciar sesión",
                    color = colorTextoTarjeta,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                // CORREO ELECTRÓNICO

                CampoTexto(
                    valor = correo,
                    onValorCambia = { nuevoCorreo ->
                        errorLocal = ""
                        onCorreo(nuevoCorreo)
                    },
                    etiqueta = "Correo electrónico",
                    icono = null,
                    modifier = Modifier.fillMaxWidth(),
                    tipoTeclado = KeyboardType.Email,
                    habilitado = !ocupado,
                    forma = OutlinedTextFieldDefaults.shape,
                    colores = OutlinedTextFieldDefaults.colors(),
                    accionTeclado = ImeAction.Default,
                    reservarEspacioAyuda = false
                )

                // CONTRASEÑA

                CampoContrasena(
                    valor = clave,
                    onValorCambia = { nuevaClave ->
                        errorLocal = ""
                        onClave(nuevaClave)
                    },
                    etiqueta = "Contraseña",
                    visible = mostrarClave,
                    onAlternarVisible = {
                        mostrarClave = !mostrarClave
                    },
                    modifier = Modifier.fillMaxWidth(),
                    habilitado = !ocupado,
                    mostrarCandado = false,
                    usarTextoVisibilidad = true,
                    forma = OutlinedTextFieldDefaults.shape,
                    colores = OutlinedTextFieldDefaults.colors(),
                    reservarEspacioError = false
                )

                // ERROR DE VALIDACIÓN LOCAL

                if (errorLocal.isNotBlank()) {
                    Text(
                        text = errorLocal,
                        color = colorError
                    )
                }

                // MENSAJE DEL COORDINADOR

                if (mensaje.isNotBlank()) {
                    Text(
                        text = mensaje,
                        color = colorMensaje
                    )
                }

                // INGRESAR

                BotonPrincipal(
                    texto = if (ocupado) {
                        "Ingresando..."
                    } else {
                        "Ingresar"
                    },
                    onClick = {
                        errorLocal = when {
                            !Patterns.EMAIL_ADDRESS
                                .matcher(correo.trim())
                                .matches() -> {
                                "Ingresa un correo válido."
                            }

                            clave.isBlank() -> {
                                "Ingresa tu contraseña."
                            }

                            else -> ""
                        }

                        if (errorLocal.isBlank()) {
                            onEntrar()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    habilitado = !ocupado,
                    colores = ButtonDefaults.buttonColors(
                        containerColor = colorBotonIngresar,
                        contentColor = colorTextoBotonIngresar
                    ),
                    forma = RoundedCornerShape(12.dp),
                    altura = 50.dp,
                    elevacion = ButtonDefaults.buttonElevation(),
                    relleno = ButtonDefaults.ContentPadding,
                    pesoTexto = FontWeight.Bold
                )

                // CREAR CUENTA

                BotonSecundario(
                    texto = "Crear cuenta",
                    onClick = onRegistro,
                    modifier = Modifier.fillMaxWidth(),
                    habilitado = !ocupado,
                    colores = ButtonDefaults.outlinedButtonColors(),
                    borde = ButtonDefaults.outlinedButtonBorder,
                    forma = RoundedCornerShape(12.dp),
                    altura = 50.dp,
                    relleno = ButtonDefaults.ContentPadding
                )
            }

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            // ACCESOS DE DEMOSTRACIÓN

            Text(
                text = "Accesos de demostración",
                color = colorTituloDemo,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = "Admin: admin@pagoflex.cl",
                color = colorTextoDemo
            )

            Text(
                text = "Usuario: emilia@pagoflex.cl",
                color = colorTextoDemo
            )
        }
    }
}