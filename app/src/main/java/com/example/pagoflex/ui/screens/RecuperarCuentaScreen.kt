package com.example.pagoflex.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*

import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pagoflex.data.PagoFlexRepository
import com.example.pagoflex.validation.ValidacionesCuenta
import com.example.pagoflex.ui.components.BotonPrincipal
import com.example.pagoflex.ui.components.CampoContrasena
import com.example.pagoflex.ui.components.CampoTexto
import com.example.pagoflex.ui.components.MarcaHorizontalPagoFlex
import com.example.pagoflex.ui.components.TarjetaSeccion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Suppress("DEPRECATION")
@Composable
fun RecuperarCuentaScreen(
    repository: PagoFlexRepository,
    correoInicial: String,
    onVolver: () -> Unit,
    onFinalizado: (String) -> Unit
) {
    val colores = MaterialTheme.colorScheme
    val scope = rememberCoroutineScope()

    var paso by remember { mutableStateOf(1) }
    var correo by remember { mutableStateOf(correoInicial) }
    var preguntas by remember { mutableStateOf<List<String>>(emptyList()) }
    var respuesta1 by remember { mutableStateOf("") }
    var respuesta2 by remember { mutableStateOf("") }
    var nueva by remember { mutableStateOf("") }
    var repetir by remember { mutableStateOf("") }
    var nuevaVisible by remember { mutableStateOf(false) }
    var repetirVisible by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf("") }
    var ocupado by remember { mutableStateOf(false) }

    BackHandler(enabled = !ocupado) { onVolver() }

    fun buscarPreguntas() {
        val problema = ValidacionesCuenta.correo(correo)
        if (problema != null) {
            error = problema
            return
        }
        scope.launch {
            ocupado = true
            error = ""
            try {
                val resultado = withContext(Dispatchers.IO) {
                    repository.obtenerPreguntas(correo)
                }
                when {
                    resultado == null -> error = "No encontramos una cuenta con ese correo."
                    resultado.size < 2 -> error =
                        "Esta cuenta no tiene preguntas secretas configuradas."
                    else -> {
                        preguntas = resultado
                        paso = 2
                    }
                }
            } catch (_: Exception) {
                error = "No se pudo consultar la cuenta."
            } finally {
                ocupado = false
            }
        }
    }

    fun verificar() {
        val problema = ValidacionesCuenta.respuesta(respuesta1)
            ?: ValidacionesCuenta.respuesta(respuesta2)
        if (problema != null) {
            error = problema
            return
        }
        scope.launch {
            ocupado = true
            error = ""
            try {
                val ok = withContext(Dispatchers.IO) {
                    repository.verificarRespuestas(correo, respuesta1, respuesta2)
                }
                if (ok) paso = 3 else error = "Las respuestas no coinciden."
            } catch (_: Exception) {
                error = "No se pudo verificar las respuestas."
            } finally {
                ocupado = false
            }
        }
    }

    fun cambiarClave() {
        val problema = ValidacionesCuenta.contrasena(nueva)
            ?: ValidacionesCuenta.repetir(nueva, repetir)
        if (problema != null) {
            error = problema
            return
        }
        scope.launch {
            ocupado = true
            error = ""
            try {
                val ok = withContext(Dispatchers.IO) {
                    repository.restablecerClave(correo, respuesta1, respuesta2, nueva)
                }
                if (ok) onFinalizado(correo.trim()) else error = "No se pudo cambiar la contraseña."
            } catch (_: Exception) {
                error = "Ocurrió un error al cambiar la contraseña."
            } finally {
                ocupado = false
            }
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = colores.background,
        contentColor = colores.onBackground
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TextButton(onClick = onVolver, enabled = !ocupado) {
                Text("← Volver al inicio de sesión")
            }

            MarcaHorizontalPagoFlex(
                tamanoIcono = 44.dp,
                colorPago = colores.onBackground,
                colorFlex = colores.tertiary
            )

            Text(
                text = "Recuperar cuenta",
                color = colores.primary,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Paso $paso de 3",
                color = colores.onSurfaceVariant,
                fontWeight = FontWeight.SemiBold
            )

            if (ocupado) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            TarjetaSeccion(
                forma = RoundedCornerShape(24.dp),
                colores = CardDefaults.cardColors(
                    containerColor = colores.surface,
                    contentColor = colores.onSurface
                ),
                elevacion = CardDefaults.cardElevation(defaultElevation = 2.dp),
                relleno = PaddingValues(20.dp),
                separacion = 14.dp
            ) {
                when (paso) {
                    1 -> {
                        Text("Ingresa tu correo", fontWeight = FontWeight.Bold, fontSize = 17.sp)
                        Text(
                            "Te mostraremos tus preguntas secretas.",
                            color = colores.onSurfaceVariant
                        )
                        CampoTexto(
                            valor = correo,
                            onValorCambia = { correo = it; error = "" },
                            etiqueta = "Correo electrónico",
                            tipoTeclado = KeyboardType.Email,
                            habilitado = !ocupado,
                            forma = OutlinedTextFieldDefaults.shape,
                            colores = OutlinedTextFieldDefaults.colors(),
                            accionTeclado = ImeAction.Default,
                            reservarEspacioAyuda = false
                        )
                        MensajeError(error)
                        BotonPrincipal(
                            texto = "Continuar",
                            onClick = { buscarPreguntas() },
                            habilitado = !ocupado,
                            modifier = Modifier.fillMaxWidth(),
                            colores = ButtonDefaults.buttonColors(
                                containerColor = colores.secondary,
                                contentColor = colores.onSecondary
                            ),
                            pesoTexto = FontWeight.Bold
                        )
                    }

                    2 -> {
                        Text("Responde tus preguntas", fontWeight = FontWeight.Bold, fontSize = 17.sp)

                        Text(preguntas.getOrElse(0) { "" }, fontWeight = FontWeight.SemiBold)
                        CampoTexto(
                            valor = respuesta1,
                            onValorCambia = { respuesta1 = it; error = "" },
                            etiqueta = "Respuesta 1",
                            habilitado = !ocupado,
                            forma = OutlinedTextFieldDefaults.shape,
                            colores = OutlinedTextFieldDefaults.colors(),
                            accionTeclado = ImeAction.Default,
                            reservarEspacioAyuda = false
                        )

                        Text(preguntas.getOrElse(1) { "" }, fontWeight = FontWeight.SemiBold)
                        CampoTexto(
                            valor = respuesta2,
                            onValorCambia = { respuesta2 = it; error = "" },
                            etiqueta = "Respuesta 2",
                            habilitado = !ocupado,
                            forma = OutlinedTextFieldDefaults.shape,
                            colores = OutlinedTextFieldDefaults.colors(),
                            accionTeclado = ImeAction.Default,
                            reservarEspacioAyuda = false
                        )

                        MensajeError(error)
                        BotonPrincipal(
                            texto = "Verificar respuestas",
                            onClick = { verificar() },
                            habilitado = !ocupado,
                            modifier = Modifier.fillMaxWidth(),
                            colores = ButtonDefaults.buttonColors(
                                containerColor = colores.secondary,
                                contentColor = colores.onSecondary
                            ),
                            pesoTexto = FontWeight.Bold
                        )
                    }

                    else -> {
                        Text("Crea tu nueva contraseña", fontWeight = FontWeight.Bold, fontSize = 17.sp)

                        CampoContrasena(
                            valor = nueva,
                            onValorCambia = { nueva = it; error = "" },
                            etiqueta = "Nueva contraseña",
                            visible = nuevaVisible,
                            onAlternarVisible = { nuevaVisible = !nuevaVisible },
                            habilitado = !ocupado,
                            mostrarCandado = false,
                            usarTextoVisibilidad = true,
                            forma = OutlinedTextFieldDefaults.shape,
                            colores = OutlinedTextFieldDefaults.colors(),
                            reservarEspacioError = false
                        )
                        CampoContrasena(
                            valor = repetir,
                            onValorCambia = { repetir = it; error = "" },
                            etiqueta = "Confirmar nueva contraseña",
                            visible = repetirVisible,
                            onAlternarVisible = { repetirVisible = !repetirVisible },
                            habilitado = !ocupado,
                            mostrarCandado = false,
                            usarTextoVisibilidad = true,
                            forma = OutlinedTextFieldDefaults.shape,
                            colores = OutlinedTextFieldDefaults.colors(),
                            reservarEspacioError = false
                        )

                        MensajeError(error)
                        BotonPrincipal(
                            texto = "Cambiar contraseña",
                            onClick = { cambiarClave() },
                            habilitado = !ocupado,
                            modifier = Modifier.fillMaxWidth(),
                            colores = ButtonDefaults.buttonColors(
                                containerColor = colores.secondary,
                                contentColor = colores.onSecondary
                            ),
                            pesoTexto = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MensajeError(texto: String) {
    if (texto.isNotBlank()) {
        Text(
            text = texto,
            color = MaterialTheme.colorScheme.error,
            fontWeight = FontWeight.Medium
        )
    }
}