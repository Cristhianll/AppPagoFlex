package com.example.pagoflex.ui.screens

import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.ui.unit.sp
import com.example.pagoflex.data.PagoFlexRepository
import com.example.pagoflex.validation.PreguntasSecretas

import com.example.pagoflex.validation.ValidacionesRut
import com.example.pagoflex.validation.ValidacionesTarjeta
import com.example.pagoflex.ui.components.BotonPrincipal
import com.example.pagoflex.ui.components.BotonSecundario
import com.example.pagoflex.ui.components.CampoTexto
import com.example.pagoflex.ui.components.FotoPerfilEditable
import com.example.pagoflex.ui.components.InterruptorConTexto
import com.example.pagoflex.ui.components.SelectorDesplegable
import com.example.pagoflex.ui.components.TarjetaSeccion
import com.example.pagoflex.ui.theme.LocalControlTemaPagoFlex
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun DetallePerfilScreen(
    repository: PagoFlexRepository,
    usuario: Map<String, String>,
    tarjetas: List<Map<String, String>>,
    notificacionesActivas: Boolean,
    seccionInicial: String,
    onVolver: () -> Unit,
    onCerrarSesion: () -> Unit,
    onTarjetasCambiaron: () -> Unit,
    onUsuarioActualizado: (Map<String, String>) -> Unit,
    onAlternarNotificaciones: () -> Unit
) {
    val colores = MaterialTheme.colorScheme
    val usuarioId = usuario["id"]?.toIntOrNull() ?: 0
    val nombre = usuario["nombre"].orEmpty()
    val rut = usuario["rut"].orEmpty()

    var pestana by remember(seccionInicial) { mutableStateOf(seccionInicial) }

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
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onVolver) {
                    Text("←", fontSize = 24.sp, color = colores.onBackground)
                }
                Text(
                    text = "Gestión de Perfil",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = colores.onBackground
                )
                Spacer(Modifier.width(48.dp))
            }

            Spacer(Modifier.height(12.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                FotoPerfilEditable(
                    usuarioId = usuarioId,
                    nombreUsuario = nombre,
                    repository = repository
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = nombre.ifBlank { "Usuario" },
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = if (rut.isBlank()) {
                        "RUT no registrado (complétalo en Editar datos)"
                    } else {
                        "RUT ${ValidacionesRut.formatear(rut)}"
                    },
                    color = colores.primary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = "Cliente Registrado • PagoFlex",
                    color = colores.onSurfaceVariant,
                    fontSize = 12.sp
                )
            }

            Spacer(Modifier.height(20.dp))

            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                BotonPestanaPerfil("Cuenta", pestana == "cuenta") { pestana = "cuenta" }
                BotonPestanaPerfil("Editar datos", pestana == "editar") { pestana = "editar" }
                BotonPestanaPerfil("Tarjetas", pestana == "tarjetas") { pestana = "tarjetas" }
                BotonPestanaPerfil("Ajustes", pestana == "configuracion") { pestana = "configuracion" }
            }

            Spacer(Modifier.height(20.dp))

            when (pestana) {
                "editar" -> ContenidoEditarDatos(
                    repository = repository,
                    usuario = usuario,
                    onUsuarioActualizado = onUsuarioActualizado
                )

                "tarjetas" -> ContenidoMisTarjetas(
                    repository = repository,
                    usuarioId = usuarioId,
                    tarjetas = tarjetas,
                    onTarjetasCambiaron = onTarjetasCambiaron
                )

                "configuracion" -> ContenidoConfiguracion(
                    notificacionesActivas = notificacionesActivas,
                    onAlternarNotificaciones = onAlternarNotificaciones,
                    onCerrarSesion = onCerrarSesion
                )

                else -> ContenidoMiCuenta(
                    nombre = nombre,
                    rut = rut,
                    correo = usuario["correo"].orEmpty(),
                    cantidadTarjetas = tarjetas.size,
                    maxTarjetas = repository.maxTarjetas
                )
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
fun BotonPestanaPerfil(
    titulo: String,
    seleccionado: Boolean,
    onClick: () -> Unit
) {
    val colores = MaterialTheme.colorScheme

    ProvideTextStyle(
        value = MaterialTheme.typography.labelLarge.copy(fontSize = 13.sp)
    ) {
        BotonPrincipal(
            texto = titulo,
            onClick = onClick,
            altura = 40.dp,
            forma = RoundedCornerShape(12.dp),
            colores = ButtonDefaults.buttonColors(
                containerColor = if (seleccionado) colores.primary else colores.surfaceVariant,
                contentColor = if (seleccionado) colores.onPrimary else colores.onSurfaceVariant
            ),
            elevacion = ButtonDefaults.buttonElevation(),
            relleno = PaddingValues(horizontal = 16.dp),
            pesoTexto = FontWeight.Bold
        )
    }
}

@Composable
fun ContenidoMiCuenta(
    nombre: String,
    rut: String,
    correo: String,
    cantidadTarjetas: Int,
    maxTarjetas: Int
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "INFORMACIÓN PERSONAL",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )

        CardItemPerfil("Nombre completo", nombre.ifBlank { "Sin nombre" })
        CardItemPerfil(
            "RUT",
            if (rut.isBlank()) "Sin RUT registrado" else ValidacionesRut.formatear(rut)
        )
        CardItemPerfil("Correo electrónico", correo.ifBlank { "Sin correo" })
        CardItemPerfil("Tarjetas registradas", "$cantidadTarjetas de $maxTarjetas")
        CardItemPerfil("Estado de cuenta", "Registrada")
    }
}


@Composable
private fun ContenidoEditarDatos(
    repository: PagoFlexRepository,
    usuario: Map<String, String>,
    onUsuarioActualizado: (Map<String, String>) -> Unit
) {
    val colores = MaterialTheme.colorScheme
    val scope = rememberCoroutineScope()
    val usuarioId = usuario["id"]?.toIntOrNull() ?: 0

    var nombre by remember(usuarioId) { mutableStateOf(usuario["nombre"].orEmpty()) }
    var rut by remember(usuarioId) {
        mutableStateOf(ValidacionesRut.formatear(usuario["rut"].orEmpty()))
    }
    var correo by remember(usuarioId) { mutableStateOf(usuario["correo"].orEmpty()) }
    var pregunta1 by remember { mutableStateOf("") }
    var respuesta1 by remember { mutableStateOf("") }
    var pregunta2 by remember { mutableStateOf("") }
    var respuesta2 by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf("") }
    var esError by remember { mutableStateOf(false) }
    var guardando by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "EDITAR DATOS PERSONALES",
            color = colores.onSurfaceVariant,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )

        CampoEditar(nombre, { nombre = it; mensaje = "" }, "Nombre completo", !guardando)
        CampoEditar(
            valor = rut,
            onCambia = {
                rut = it.filter { c -> c.isDigit() || c in ".-kK" }.take(12)
                mensaje = ""
            },
            etiqueta = "RUT",
            habilitado = !guardando,
            ayuda = "Ejemplo: 12.345.678-5"
        )
        CampoEditar(
            correo, { correo = it; mensaje = "" }, "Correo electrónico",
            !guardando, KeyboardType.Email
        )

        Text(
            text = "PREGUNTAS SECRETAS (OPCIONAL)",
            color = colores.onSurfaceVariant,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Déjalas vacías para conservar las actuales. Si las completas, " +
                    "reemplazan a las anteriores.",
            color = colores.onSurfaceVariant,
            fontSize = 12.sp
        )

        SelectorDesplegable(
            etiqueta = "Pregunta secreta 1",
            textoSeleccionado = pregunta1,
            opciones = PreguntasSecretas.lista,
            onSeleccion = { pregunta1 = PreguntasSecretas.lista[it]; mensaje = "" },
            habilitado = !guardando
        )
        CampoEditar(respuesta1, { respuesta1 = it; mensaje = "" }, "Respuesta 1", !guardando)

        SelectorDesplegable(
            etiqueta = "Pregunta secreta 2",
            textoSeleccionado = pregunta2,
            opciones = PreguntasSecretas.lista,
            onSeleccion = { pregunta2 = PreguntasSecretas.lista[it]; mensaje = "" },
            habilitado = !guardando
        )
        CampoEditar(respuesta2, { respuesta2 = it; mensaje = "" }, "Respuesta 2", !guardando)

        if (mensaje.isNotBlank()) {
            Text(
                text = mensaje,
                color = if (esError) colores.error else colores.primary,
                fontWeight = FontWeight.Medium
            )
        }

        BotonPrincipal(
            texto = if (guardando) "Guardando..." else "Guardar cambios",
            habilitado = !guardando,
            onClick = {
                scope.launch {
                    guardando = true
                    val error = try {
                        withContext(Dispatchers.IO) {
                            repository.actualizarPerfil(
                                usuarioId, nombre, rut, correo,
                                pregunta1, respuesta1, pregunta2, respuesta2
                            )
                        }
                    } catch (_: Exception) {
                        "No se pudo guardar. Intenta nuevamente."
                    }
                    guardando = false

                    if (error == null) {
                        esError = false
                        mensaje = "Datos actualizados correctamente."
                        pregunta1 = ""; respuesta1 = ""; pregunta2 = ""; respuesta2 = ""
                        onUsuarioActualizado(
                            usuario + mapOf(
                                "nombre" to nombre.trim(),
                                "rut" to ValidacionesRut.normalizar(rut),
                                "correo" to correo.trim().lowercase()
                            )
                        )
                    } else {
                        esError = true
                        mensaje = error
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            altura = 48.dp,
            forma = RoundedCornerShape(12.dp),
            colores = ButtonDefaults.buttonColors(
                containerColor = colores.secondary,
                contentColor = colores.onSecondary
            ),
            elevacion = ButtonDefaults.buttonElevation(),
            relleno = ButtonDefaults.ContentPadding,
            pesoTexto = FontWeight.Bold
        )
    }
}

@Composable
private fun CampoEditar(
    valor: String,
    onCambia: (String) -> Unit,
    etiqueta: String,
    habilitado: Boolean,
    teclado: KeyboardType = KeyboardType.Text,
    ayuda: String? = null
) {
    CampoTexto(
        valor = valor,
        onValorCambia = onCambia,
        etiqueta = etiqueta,
        icono = null,
        habilitado = habilitado,
        tipoTeclado = teclado,
        ayuda = ayuda,
        forma = OutlinedTextFieldDefaults.shape,
        colores = OutlinedTextFieldDefaults.colors(),
        accionTeclado = ImeAction.Default,
        reservarEspacioAyuda = false
    )
}

private fun detectarMarca(numero: String): String = when {
    numero.startsWith("4") -> "Visa"
    numero.startsWith("5") -> "Mastercard"
    numero.startsWith("3") -> "Amex"
    else -> "Tarjeta"
}

@Composable
fun ContenidoMisTarjetas(
    repository: PagoFlexRepository,
    usuarioId: Int,
    tarjetas: List<Map<String, String>>,
    onTarjetasCambiaron: () -> Unit
) {
    val colores = MaterialTheme.colorScheme
    val scope = rememberCoroutineScope()
    val maximo = repository.maxTarjetas

    var mostrarDialogo by remember { mutableStateOf(false) }
    var numeroTarjeta by remember { mutableStateOf("") }
    var titular by remember { mutableStateOf("") }
    var vencimiento by remember { mutableStateOf("") }
    var mensajeError by remember { mutableStateOf("") }
    var mensajeLista by remember { mutableStateOf("") }
    var ocupado by remember { mutableStateOf(false) }

    fun cerrarDialogo() {
        numeroTarjeta = ""
        titular = ""
        vencimiento = ""
        mensajeError = ""
        mostrarDialogo = false
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "MÉTODOS DE PAGO (${tarjetas.size}/$maximo)",
            color = colores.onSurfaceVariant,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )

        if (tarjetas.isEmpty()) {
            TarjetaSeccion(
                forma = CardDefaults.shape,
                colores = CardDefaults.cardColors(
                    containerColor = colores.surface,
                    contentColor = colores.onSurface
                ),
                elevacion = CardDefaults.cardElevation(defaultElevation = 0.dp),
                relleno = PaddingValues(20.dp),
                separacion = 0.dp
            ) {
                Text("Aún no tienes tarjetas vinculadas.")
            }
        }

        tarjetas.forEach { tarjeta ->
            val id = tarjeta["id"]?.toIntOrNull() ?: 0
            val esPrincipal = tarjeta["principal"] == "1"
            val venc = tarjeta["vencimiento"].orEmpty()
            val vigente = ValidacionesTarjeta.vencimientoVigente(venc)

            TarjetaSeccion(
                forma = RoundedCornerShape(16.dp),
                colores = CardDefaults.cardColors(
                    containerColor = colores.primaryContainer,
                    contentColor = colores.onPrimaryContainer
                ),
                elevacion = CardDefaults.cardElevation(defaultElevation = 0.dp),
                relleno = PaddingValues(18.dp),
                separacion = 8.dp
            ) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = tarjeta["marca"].orEmpty() + " de demostración",
                        modifier = Modifier.weight(1f),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    if (esPrincipal) {
                        Text("★ Principal", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Text(
                    text = "•••• •••• •••• ${tarjeta["ultimosDigitos"].orEmpty()}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
                Text(text = tarjeta["titular"].orEmpty(), fontSize = 13.sp)
                Text(
                    text = if (vigente) "Vence: $venc" else "Vencida: $venc",
                    color = if (vigente) colores.onPrimaryContainer else colores.error,
                    fontSize = 12.sp
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (!esPrincipal) {
                        BotonSecundario(
                            texto = "Hacer principal",
                            habilitado = !ocupado,
                            onClick = {
                                scope.launch {
                                    ocupado = true
                                    val ok = withContext(Dispatchers.IO) {
                                        repository.marcarTarjetaPrincipal(id, usuarioId)
                                    }
                                    ocupado = false
                                    mensajeLista = if (ok) "Tarjeta principal actualizada."
                                    else "No se pudo actualizar la tarjeta."
                                    onTarjetasCambiaron()
                                }
                            },
                            altura = 40.dp,
                            relleno = ButtonDefaults.ContentPadding
                        )
                    }
                    BotonSecundario(
                        texto = "Eliminar",
                        habilitado = !ocupado,
                        onClick = {
                            scope.launch {
                                ocupado = true
                                val ok = withContext(Dispatchers.IO) {
                                    repository.eliminarTarjeta(id, usuarioId)
                                }
                                ocupado = false
                                mensajeLista = if (ok) "Tarjeta eliminada."
                                else "No se pudo eliminar la tarjeta."
                                onTarjetasCambiaron()
                            }
                        },
                        colores = ButtonDefaults.outlinedButtonColors(contentColor = colores.error),
                        altura = 40.dp,
                        relleno = ButtonDefaults.ContentPadding
                    )
                }
            }
        }

        if (mensajeLista.isNotBlank()) {
            Text(text = mensajeLista, color = colores.primary, fontSize = 13.sp)
        }

        BotonPrincipal(
            texto = if (tarjetas.size >= maximo) "Límite de $maximo tarjetas alcanzado"
            else "+ Agregar tarjeta de prueba",
            habilitado = tarjetas.size < maximo && !ocupado,
            onClick = {
                cerrarDialogo()
                mensajeLista = ""
                mostrarDialogo = true
            },
            modifier = Modifier.fillMaxWidth(),
            altura = 48.dp,
            forma = RoundedCornerShape(12.dp),
            colores = ButtonDefaults.buttonColors(
                containerColor = colores.secondary,
                contentColor = colores.onSecondary
            ),
            elevacion = ButtonDefaults.buttonElevation(),
            relleno = ButtonDefaults.ContentPadding,
            pesoTexto = FontWeight.Bold
        )

        Text(
            text = "Simulación académica. Puedes registrar hasta $maximo tarjetas y " +
                    "elegir con cuál pagar. No se realizan cargos bancarios.",
            color = colores.onSurfaceVariant,
            fontSize = 12.sp
        )
    }

    if (mostrarDialogo) {
        AlertDialog(
            onDismissRequest = { cerrarDialogo() },
            title = { Text("Agregar tarjeta de prueba") },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "Usa datos ficticios. Solo se conservan los últimos cuatro " +
                                "dígitos, el titular y el vencimiento."
                    )

                    CampoEditar(titular, { titular = it; mensajeError = "" }, "Titular", true)

                    CampoEditar(
                        valor = numeroTarjeta,
                        onCambia = { entrada ->
                            val limpio = entrada.replace(" ", "").replace("-", "")
                            if (limpio.length <= 16 && limpio.all { it in '0'..'9' }) {
                                numeroTarjeta = limpio
                                mensajeError = ""
                            }
                        },
                        etiqueta = "Número de tarjeta de prueba",
                        habilitado = true,
                        teclado = KeyboardType.Number,
                        ayuda = "16 dígitos ficticios. Empieza con 4 (Visa) o 5 (Mastercard)."
                    )

                    CampoEditar(
                        valor = vencimiento,
                        onCambia = { entrada ->
                            val limpio = entrada.replace("/", "")
                            if (limpio.length <= 4 && limpio.all { it in '0'..'9' }) {
                                vencimiento = if (limpio.length > 2) {
                                    limpio.take(2) + "/" + limpio.drop(2)
                                } else limpio
                                mensajeError = ""
                            }
                        },
                        etiqueta = "Vencimiento MM/AA",
                        habilitado = true,
                        teclado = KeyboardType.Number,
                        ayuda = "Ejemplo: 12/30"
                    )

                    if (mensajeError.isNotBlank()) {
                        Text(text = mensajeError, color = colores.error)
                    }
                }
            },
            confirmButton = {
                BotonPrincipal(
                    texto = if (ocupado) "Guardando..." else "Agregar",
                    habilitado = !ocupado,
                    onClick = {
                        mensajeError = when {
                            titular.isBlank() -> "Ingresa el titular."
                            numeroTarjeta.length != 16 -> "Ingresa exactamente 16 dígitos."
                            !ValidacionesTarjeta.vencimientoVigente(vencimiento) ->
                                "Ingresa un vencimiento válido y vigente en MM/AA."
                            else -> ""
                        }

                        if (mensajeError.isBlank()) {
                            scope.launch {
                                ocupado = true
                                var exito = false
                                try {
                                    exito = withContext(Dispatchers.IO) {
                                        repository.agregarTarjeta(
                                            usuarioId,
                                            numeroTarjeta.takeLast(4),
                                            titular.trim(),
                                            vencimiento,
                                            detectarMarca(numeroTarjeta)
                                        )
                                    }
                                } catch (_: Exception) {
                                    exito = false
                                }
                                ocupado = false

                                if (exito) {
                                    cerrarDialogo()
                                    mensajeLista = "Tarjeta agregada correctamente."
                                    onTarjetasCambiaron()
                                } else {
                                    mensajeError = "No se pudo guardar la tarjeta. Revisa que no esté duplicada."
                                }
                            }
                        }
                    },
                    altura = null,
                    forma = ButtonDefaults.shape,
                    colores = ButtonDefaults.buttonColors(),
                    elevacion = ButtonDefaults.buttonElevation(),
                    relleno = ButtonDefaults.ContentPadding
                )
            },
            dismissButton = {
                TextButton(onClick = { cerrarDialogo() }) { Text("Cancelar") }
            }
        )
    }
}

@Suppress("DEPRECATION")
@Composable
fun ContenidoConfiguracion(
    notificacionesActivas: Boolean,
    onAlternarNotificaciones: () -> Unit,
    onCerrarSesion: () -> Unit
) {
    val colores = MaterialTheme.colorScheme
    val tema = LocalControlTemaPagoFlex.current
    var mostrarTerminos by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "PREFERENCIAS DE LA APLICACIÓN",
            color = colores.onSurfaceVariant,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )

        OpcionSwitchItem(
            icono = "🌙",
            titulo = "Modo oscuro",
            activado = tema.oscuro,
            onCambiar = { tema.cambiarModoOscuro(!tema.oscuro) }
        )

        Text(
            text = if (tema.sigueSistema) "Se está utilizando el tema del teléfono."
            else "Se está utilizando tu elección para PagoFlex.",
            color = colores.onSurfaceVariant,
            fontSize = 12.sp
        )

        if (!tema.sigueSistema) {
            BotonSecundario(
                texto = "Usar tema del teléfono",
                onClick = tema.usarTemaDelSistema,
                modifier = Modifier.fillMaxWidth(),
                altura = null,
                forma = ButtonDefaults.outlinedShape,
                colores = ButtonDefaults.outlinedButtonColors(),
                borde = ButtonDefaults.outlinedButtonBorder,
                relleno = ButtonDefaults.ContentPadding
            )
        }

        OpcionSwitchItem(
            icono = "🔔",
            titulo = "Notificaciones de vencimiento",
            activado = notificacionesActivas,
            onCambiar = onAlternarNotificaciones
        )

        Text(
            text = "Las notificaciones siguen siendo una preferencia de demostración: " +
                    "todavía no se envían avisos.",
            color = colores.onSurfaceVariant,
            fontSize = 11.sp
        )

        Card(
            onClick = { mostrarTerminos = true },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = colores.surface,
                contentColor = colores.onSurface
            )
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("📄", fontSize = 18.sp)
                Spacer(Modifier.width(12.dp))
                Text("Términos y condiciones", modifier = Modifier.weight(1f), fontSize = 13.sp)
                Text("Ver", color = colores.onSurfaceVariant, fontSize = 12.sp)
            }
        }

        Spacer(Modifier.height(8.dp))

        BotonPrincipal(
            texto = "Cerrar sesión actual",
            onClick = onCerrarSesion,
            modifier = Modifier.fillMaxWidth(),
            altura = 48.dp,
            forma = RoundedCornerShape(12.dp),
            colores = ButtonDefaults.buttonColors(
                containerColor = colores.error,
                contentColor = colores.onError
            ),
            elevacion = ButtonDefaults.buttonElevation(),
            relleno = ButtonDefaults.ContentPadding,
            pesoTexto = FontWeight.Bold
        )
    }

    if (mostrarTerminos) {
        AlertDialog(
            onDismissRequest = { mostrarTerminos = false },
            title = { Text("Términos y condiciones") },
            text = {
                Text(
                    "PagoFlex es una demostración académica. Los pagos y tarjetas son " +
                            "simulados. No se realizan cargos bancarios. Usa datos ficticios " +
                            "al vincular una tarjeta."
                )
            },
            confirmButton = {
                TextButton(onClick = { mostrarTerminos = false }) { Text("Cerrar") }
            }
        )
    }
}

@Composable
fun CardItemPerfil(label: String, valor: String) {
    val colores = MaterialTheme.colorScheme

    TarjetaSeccion(
        forma = RoundedCornerShape(12.dp),
        colores = CardDefaults.cardColors(
            containerColor = colores.surface,
            contentColor = colores.onSurface
        ),
        elevacion = CardDefaults.cardElevation(defaultElevation = 0.dp),
        relleno = PaddingValues(14.dp),
        separacion = 4.dp
    ) {
        Text(text = label, color = colores.onSurfaceVariant, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        Text(text = valor, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun OpcionSwitchItem(
    icono: String,
    titulo: String,
    activado: Boolean,
    onCambiar: () -> Unit
) {
    val colores = MaterialTheme.colorScheme

    TarjetaSeccion(
        forma = RoundedCornerShape(12.dp),
        colores = CardDefaults.cardColors(
            containerColor = colores.surface,
            contentColor = colores.onSurface
        ),
        elevacion = CardDefaults.cardElevation(defaultElevation = 0.dp),
        relleno = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
        separacion = 0.dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = icono, fontSize = 18.sp)
            Spacer(Modifier.width(12.dp))
            InterruptorConTexto(
                texto = titulo,
                activado = activado,
                onCambio = { onCambiar() },
                modifier = Modifier.weight(1f),
                filaPulsable = false,
                rellenoVertical = 0.dp,
                estiloTexto = LocalTextStyle.current.copy(fontSize = 13.sp)
            )
        }
    }
}