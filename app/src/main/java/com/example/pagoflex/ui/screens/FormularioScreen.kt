package com.example.pagoflex.ui.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pagoflex.data.PreguntasSecretas
import com.example.pagoflex.ui.components.BotonPrincipal
import com.example.pagoflex.ui.components.BotonSecundario
import com.example.pagoflex.ui.components.CampoContrasena
import com.example.pagoflex.ui.components.CampoTexto
import com.example.pagoflex.ui.components.CasillaConTexto
import com.example.pagoflex.ui.components.InterruptorConTexto
import com.example.pagoflex.ui.components.MarcaHorizontalPagoFlex
import com.example.pagoflex.ui.components.SelectorDesplegable
import com.example.pagoflex.ui.components.TarjetaSeccion
import com.example.pagoflex.viewmodel.FormularioViewModel

@Composable
fun FormularioScreen(
    viewModel: FormularioViewModel,
    ocupado: Boolean,
    onVolverAlLogin: () -> Unit
) {
    val colores = MaterialTheme.colorScheme

    BackHandler(enabled = !ocupado) { onVolverAlLogin() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colores.background)
            .statusBarsPadding()
    ) {
        TextButton(onClick = onVolverAlLogin, enabled = !ocupado) {
            Text("← Volver al inicio de sesión")
        }

        if (ocupado) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }

        Box(modifier = Modifier.weight(1f)) {
            ContenidoFormulario(viewModel = viewModel)

            if (ocupado) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = colores.background.copy(alpha = 0.95f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("Creando cuenta...")
                        }
                    }
                }
            }
        }
    }
}

@Suppress("DEPRECATION")
@Composable
private fun ContenidoFormulario(viewModel: FormularioViewModel) {
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

            MarcaHorizontalPagoFlex(
                tamanoIcono = 44.dp,
                colorPago = colores.onBackground,
                colorFlex = colores.tertiary
            )

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
                Text(
                    text = "Datos personales",
                    color = colores.primary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )

                CampoFormulario(
                    valor = uiState.nombre,
                    onCambia = viewModel::onNombreCambia,
                    etiqueta = "Nombre completo",
                    error = uiState.errorNombre
                )

                CampoFormulario(
                    valor = uiState.rut,
                    onCambia = viewModel::onRutCambia,
                    etiqueta = "RUT",
                    error = uiState.errorRut,
                    ayuda = "Ejemplo: 12.345.678-5"
                )

                CampoFormulario(
                    valor = uiState.correo,
                    onCambia = viewModel::onCorreoCambia,
                    etiqueta = "Correo electrónico",
                    error = uiState.errorCorreo,
                    teclado = KeyboardType.Email
                )

                CampoContrasena(
                    valor = uiState.contrasena,
                    onValorCambia = viewModel::onContrasenaCambia,
                    etiqueta = "Contraseña",
                    visible = uiState.contrasenaVisible,
                    onAlternarVisible = viewModel::onAlternarVisibilidad,
                    error = uiState.errorContrasena,
                    mostrarCandado = false,
                    usarTextoVisibilidad = true,
                    forma = RoundedCornerShape(12.dp),
                    colores = OutlinedTextFieldDefaults.colors(),
                    reservarEspacioError = false,
                    mostrarMensajeError = false
                )
                uiState.errorContrasena?.let { ErrorFormulario(it) }

                CampoContrasena(
                    valor = uiState.repetirContrasena,
                    onValorCambia = viewModel::onRepetirCambia,
                    etiqueta = "Confirmar contraseña",
                    visible = uiState.repetirVisible,
                    onAlternarVisible = viewModel::onAlternarRepetirVisibilidad,
                    error = uiState.errorRepetir,
                    mostrarCandado = false,
                    usarTextoVisibilidad = true,
                    forma = RoundedCornerShape(12.dp),
                    colores = OutlinedTextFieldDefaults.colors(),
                    reservarEspacioError = false,
                    mostrarMensajeError = false
                )
                uiState.errorRepetir?.let { ErrorFormulario(it) }
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
                Text(
                    text = "Preguntas secretas",
                    color = colores.primary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Te servirán para recuperar tu cuenta si olvidas la contraseña.",
                    color = colores.onSurfaceVariant,
                    fontSize = 13.sp
                )

                SelectorDesplegable(
                    etiqueta = "Pregunta secreta 1",
                    textoSeleccionado = uiState.pregunta1,
                    opciones = PreguntasSecretas.lista,
                    onSeleccion = { viewModel.onPregunta1Cambia(PreguntasSecretas.lista[it]) },
                    error = uiState.errorPregunta1
                )

                CampoFormulario(
                    valor = uiState.respuesta1,
                    onCambia = viewModel::onRespuesta1Cambia,
                    etiqueta = "Respuesta 1",
                    error = uiState.errorRespuesta1
                )

                SelectorDesplegable(
                    etiqueta = "Pregunta secreta 2",
                    textoSeleccionado = uiState.pregunta2,
                    opciones = PreguntasSecretas.lista,
                    onSeleccion = { viewModel.onPregunta2Cambia(PreguntasSecretas.lista[it]) },
                    error = uiState.errorPregunta2
                )

                CampoFormulario(
                    valor = uiState.respuesta2,
                    onCambia = viewModel::onRespuesta2Cambia,
                    etiqueta = "Respuesta 2",
                    error = uiState.errorRespuesta2
                )
            }


            TarjetaSeccion(
                forma = RoundedCornerShape(24.dp),
                colores = CardDefaults.cardColors(
                    containerColor = colores.surface,
                    contentColor = colores.onSurface
                ),
                elevacion = CardDefaults.cardElevation(defaultElevation = 0.dp),
                relleno = PaddingValues(20.dp),
                separacion = 12.dp
            ) {
                Text(
                    text = "Preferencias",
                    color = colores.primary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )

                InterruptorConTexto(
                    texto = "Recibir novedades por correo",
                    activado = uiState.recibirNoticias,
                    onCambio = viewModel::onNoticiasCambia,
                    filaPulsable = false,
                    rellenoVertical = 0.dp,
                    estiloTexto = LocalTextStyle.current.copy(fontSize = 14.sp),
                    colorTexto = colores.onSurface
                )
            }

            Column(modifier = Modifier.fillMaxWidth()) {
                CasillaConTexto(
                    texto = "Acepto los términos y condiciones",
                    marcado = uiState.aceptaTerminos,
                    onCambio = viewModel::onTerminosCambia,
                    filaPulsable = false,
                    rellenoVertical = 0.dp,
                    separacionTexto = 0.dp,
                    estiloTexto = LocalTextStyle.current.copy(fontSize = 14.sp),
                    colorTexto = colores.onBackground,
                    textoOcupaEspacio = true
                )
                uiState.errorTerminos?.let { ErrorFormulario(it) }
            }

            uiState.errorRegistro?.let {
                Text(
                    text = it,
                    color = colores.error,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                BotonSecundario(
                    texto = "Limpiar",
                    onClick = viewModel::onLimpiar,
                    modifier = Modifier.weight(1f),
                    colores = ButtonDefaults.outlinedButtonColors(),
                    borde = ButtonDefaults.outlinedButtonBorder,
                    forma = RoundedCornerShape(16.dp),
                    altura = 52.dp,
                    relleno = ButtonDefaults.ContentPadding,
                    pesoTexto = FontWeight.Bold
                )

                BotonPrincipal(
                    texto = "Crear cuenta",
                    onClick = viewModel::onRegistrar,
                    modifier = Modifier.weight(1.5f),
                    colores = ButtonDefaults.buttonColors(
                        containerColor = colores.secondary,
                        contentColor = colores.onSecondary
                    ),
                    forma = RoundedCornerShape(16.dp),
                    altura = 52.dp,
                    elevacion = ButtonDefaults.buttonElevation(),
                    relleno = ButtonDefaults.ContentPadding,
                    pesoTexto = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun CampoFormulario(
    valor: String,
    onCambia: (String) -> Unit,
    etiqueta: String,
    error: String?,
    teclado: KeyboardType = KeyboardType.Text,
    ayuda: String? = null
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        CampoTexto(
            valor = valor,
            onValorCambia = onCambia,
            etiqueta = etiqueta,
            error = error,
            tipoTeclado = teclado,
            ayuda = ayuda,
            forma = RoundedCornerShape(12.dp),
            colores = OutlinedTextFieldDefaults.colors(),
            accionTeclado = ImeAction.Default,
            reservarEspacioAyuda = false,
            mostrarMensajeError = false
        )
        error?.let { ErrorFormulario(it) }
    }
}

@Composable
private fun ErrorFormulario(texto: String) {
    Text(
        text = texto,
        color = MaterialTheme.colorScheme.error,
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium
    )
}