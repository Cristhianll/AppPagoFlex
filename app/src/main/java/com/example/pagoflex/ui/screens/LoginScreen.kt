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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pagoflex.ui.components.BotonPrincipal
import com.example.pagoflex.ui.components.BotonSecundario
import com.example.pagoflex.ui.components.CampoContrasena
import com.example.pagoflex.ui.components.CampoTexto
import com.example.pagoflex.ui.components.IconoPagoFlex
import com.example.pagoflex.ui.components.NombrePagoFlex
import com.example.pagoflex.ui.components.TarjetaSeccion
import com.example.pagoflex.ui.theme.Acento
import com.example.pagoflex.ui.theme.Blanco
import com.example.pagoflex.ui.theme.LocalControlTemaPagoFlex
import com.example.pagoflex.ui.theme.PrimarioOscuro

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
    onRegistro: () -> Unit,
    onRecuperar: () -> Unit
) {
    val colores = MaterialTheme.colorScheme
    val tema = LocalControlTemaPagoFlex.current

    val colorFondo = if (tema.oscuro) colores.background else PrimarioOscuro
    val colorSubtitulo = Blanco.copy(alpha = 0.85f)
    val colorTextoDemo = Blanco.copy(alpha = 0.85f)

    var mostrarClave by remember { mutableStateOf(false) }
    var errorLocal by remember { mutableStateOf("") }

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

            IconoPagoFlex(tamano = 88.dp)

            Spacer(modifier = Modifier.height(12.dp))

            NombrePagoFlex(tamanoFuente = 34.sp)

            Text(
                text = "Tus pagos, más simples",
                color = colorSubtitulo
            )

            Spacer(modifier = Modifier.height(24.dp))

            TarjetaSeccion(
                forma = RoundedCornerShape(24.dp),
                colores = CardDefaults.cardColors(
                    containerColor = colores.surface,
                    contentColor = colores.onSurface
                ),
                elevacion = CardDefaults.cardElevation(defaultElevation = 0.dp),
                relleno = PaddingValues(20.dp),
                separacion = 14.dp
            ) {
                Text(
                    text = "Iniciar sesión",
                    color = colores.onSurface,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                CampoTexto(
                    valor = correo,
                    onValorCambia = {
                        errorLocal = ""
                        onCorreo(it)
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

                CampoContrasena(
                    valor = clave,
                    onValorCambia = {
                        errorLocal = ""
                        onClave(it)
                    },
                    etiqueta = "Contraseña",
                    visible = mostrarClave,
                    onAlternarVisible = { mostrarClave = !mostrarClave },
                    modifier = Modifier.fillMaxWidth(),
                    habilitado = !ocupado,
                    mostrarCandado = false,
                    usarTextoVisibilidad = true,
                    forma = OutlinedTextFieldDefaults.shape,
                    colores = OutlinedTextFieldDefaults.colors(),
                    reservarEspacioError = false
                )

                if (errorLocal.isNotBlank()) {
                    Text(text = errorLocal, color = colores.error)
                }

                if (mensaje.isNotBlank()) {
                    Text(text = mensaje, color = colores.onSurfaceVariant)
                }

                BotonPrincipal(
                    texto = if (ocupado) "Ingresando..." else "Ingresar",
                    onClick = {
                        errorLocal = when {
                            !Patterns.EMAIL_ADDRESS.matcher(correo.trim()).matches() ->
                                "Ingresa un correo válido."
                            clave.isBlank() -> "Ingresa tu contraseña."
                            else -> ""
                        }
                        if (errorLocal.isBlank()) onEntrar()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    habilitado = !ocupado,
                    colores = ButtonDefaults.buttonColors(
                        containerColor = colores.secondary,
                        contentColor = colores.onSecondary
                    ),
                    forma = RoundedCornerShape(12.dp),
                    altura = 50.dp,
                    elevacion = ButtonDefaults.buttonElevation(),
                    relleno = ButtonDefaults.ContentPadding,
                    pesoTexto = FontWeight.Bold
                )

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

            Spacer(modifier = Modifier.height(12.dp))

            TextButton(
                onClick = onRecuperar,
                enabled = !ocupado
            ) {
                Text(
                    text = "Recuperar cuenta",
                    color = Acento,
                    fontWeight = FontWeight.SemiBold,
                    textDecoration = TextDecoration.Underline
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

        }
    }
}