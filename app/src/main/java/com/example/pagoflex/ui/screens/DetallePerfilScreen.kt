package com.example.pagoflex.ui.screens

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
import com.example.pagoflex.data.ValidacionesTarjeta
import com.example.pagoflex.ui.components.BotonPrincipal
import com.example.pagoflex.ui.components.BotonSecundario
import com.example.pagoflex.ui.components.CampoTexto
import com.example.pagoflex.ui.components.FotoPerfilEditable
import com.example.pagoflex.ui.components.InterruptorConTexto
import com.example.pagoflex.ui.components.TarjetaSeccion
import com.example.pagoflex.ui.theme.LocalControlTemaPagoFlex

@Composable
fun DetallePerfilScreen(
    usuarioId: Int,
    nombreUsuario: String,
    correoUsuario: String,
    ultimosDigitosVisa: String,
    tarjetaPrincipal: Boolean,
    titularTarjeta: String = "",
    vencimientoTarjeta: String = "",
    notificacionesActivas: Boolean = true,
    seccionInicial: String = "cuenta",
    onVolver: () -> Unit,
    onCerrarSesion: () -> Unit,
    onVincularTarjeta: (String, String, String) -> Boolean,
    onAlternarNotificaciones: () -> Unit = {}
) {
    val colores = MaterialTheme.colorScheme

    var pestanaSeleccionada by remember(seccionInicial) {
        mutableStateOf(seccionInicial)
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
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onVolver
                ) {
                    Text(
                        text = "←",
                        fontSize = 24.sp,
                        color = colores.onBackground
                    )
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
                    nombreUsuario = nombreUsuario
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = nombreUsuario.ifBlank { "Usuario" },
                    color = colores.onBackground,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Cliente Registrado • PagoFlex",
                    color = colores.onSurfaceVariant,
                    fontSize = 12.sp
                )
            }

            Spacer(Modifier.height(20.dp))


            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                BotonPestanaPerfil(
                    titulo = "Cuenta",
                    seleccionado = pestanaSeleccionada == "cuenta"
                ) {
                    pestanaSeleccionada = "cuenta"
                }

                BotonPestanaPerfil(
                    titulo = "Tarjetas",
                    seleccionado = pestanaSeleccionada == "tarjetas"
                ) {
                    pestanaSeleccionada = "tarjetas"
                }

                BotonPestanaPerfil(
                    titulo = "Ajustes",
                    seleccionado = pestanaSeleccionada == "configuracion"
                ) {
                    pestanaSeleccionada = "configuracion"
                }
            }

            Spacer(Modifier.height(20.dp))



            when (pestanaSeleccionada) {
                "tarjetas" -> {
                    ContenidoMisTarjetas(
                        ultimosDigitosVisa = ultimosDigitosVisa,
                        tarjetaPrincipal = tarjetaPrincipal,
                        titularTarjeta = titularTarjeta,
                        vencimientoTarjeta = vencimientoTarjeta,
                        onVincularTarjeta = onVincularTarjeta
                    )
                }

                "configuracion" -> {
                    ContenidoConfiguracion(
                        notificacionesActivas = notificacionesActivas,
                        onAlternarNotificaciones = onAlternarNotificaciones,
                        onCerrarSesion = onCerrarSesion
                    )
                }

                else -> {
                    ContenidoMiCuenta(
                        nombre = nombreUsuario,
                        correo = correoUsuario
                    )
                }
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
        value = MaterialTheme.typography.labelLarge.copy(
            fontSize = 13.sp
        )
    ) {
        BotonPrincipal(
            texto = titulo,
            onClick = onClick,
            altura = 40.dp,
            forma = RoundedCornerShape(12.dp),
            colores = ButtonDefaults.buttonColors(
                containerColor = if (seleccionado) {
                    colores.primary
                } else {
                    colores.surfaceVariant
                },
                contentColor = if (seleccionado) {
                    colores.onPrimary
                } else {
                    colores.onSurfaceVariant
                }
            ),
            elevacion = ButtonDefaults.buttonElevation(),
            relleno = PaddingValues(horizontal = 18.dp),
            pesoTexto = FontWeight.Bold
        )
    }
}


@Composable
fun ContenidoMiCuenta(
    nombre: String,
    correo: String
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "INFORMACIÓN PERSONAL",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )

        CardItemPerfil(
            label = "Nombre completo",
            valor = nombre.ifBlank { "Sin nombre" }
        )

        CardItemPerfil(
            label = "Correo electrónico",
            valor = correo.ifBlank { "Sin correo" }
        )

        CardItemPerfil(
            label = "Estado de cuenta",
            valor = "Registrada"
        )
    }
}


@Composable
fun ContenidoMisTarjetas(
    ultimosDigitosVisa: String,
    tarjetaPrincipal: Boolean,
    titularTarjeta: String,
    vencimientoTarjeta: String,
    onVincularTarjeta: (String, String, String) -> Boolean
) {
    val colores = MaterialTheme.colorScheme

    var mostrarDialogo by remember {
        mutableStateOf(false)
    }

    var numeroTarjeta by remember {
        mutableStateOf("")
    }

    var titular by remember {
        mutableStateOf("")
    }

    var vencimiento by remember {
        mutableStateOf("")
    }

    var mensajeError by remember {
        mutableStateOf("")
    }

    var mensajeExito by remember {
        mutableStateOf("")
    }

    fun cerrarDialogo() {
        numeroTarjeta = ""
        titular = ""
        vencimiento = ""
        mensajeError = ""
        mostrarDialogo = false
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "MÉTODOS DE PAGO REGISTRADOS",
            color = colores.onSurfaceVariant,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )



        if (ultimosDigitosVisa.isNotBlank()) {
            TarjetaSeccion(
                forma = RoundedCornerShape(16.dp),
                colores = CardDefaults.cardColors(
                    containerColor = colores.primaryContainer,
                    contentColor = colores.onPrimaryContainer
                ),
                elevacion = CardDefaults.cardElevation(
                    defaultElevation = 0.dp
                ),
                relleno = PaddingValues(20.dp),
                separacion = 12.dp
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Tarjeta de demostración",
                        modifier = Modifier.weight(1f),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )

                    if (tarjetaPrincipal) {
                        Text(
                            text = "Principal",
                            fontSize = 10.sp
                        )
                    }
                }

                Text(
                    text = "•••• •••• •••• $ultimosDigitosVisa",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )

                if (titularTarjeta.isNotBlank()) {
                    Text(
                        text = titularTarjeta,
                        fontSize = 13.sp
                    )
                }

                if (vencimientoTarjeta.isNotBlank()) {
                    val vigente =
                        ValidacionesTarjeta.vencimientoVigente(
                            vencimientoTarjeta
                        )

                    Text(
                        text = if (vigente) {
                            "Vence: $vencimientoTarjeta"
                        } else {
                            "Vencida: $vencimientoTarjeta"
                        },
                        color = if (vigente) {
                            colores.onPrimaryContainer
                        } else {
                            colores.error
                        },
                        fontSize = 12.sp
                    )
                }
            }
        } else {

            TarjetaSeccion(
                forma = CardDefaults.shape,
                colores = CardDefaults.cardColors(
                    containerColor = colores.surface,
                    contentColor = colores.onSurface
                ),
                elevacion = CardDefaults.cardElevation(
                    defaultElevation = 0.dp
                ),
                relleno = PaddingValues(20.dp),
                separacion = 0.dp
            ) {
                Text(
                    text = "Aún no tienes tarjetas vinculadas."
                )
            }
        }

        if (mensajeExito.isNotBlank()) {
            Text(
                text = mensajeExito,
                color = colores.primary,
                fontSize = 13.sp
            )
        }



        BotonPrincipal(
            texto = if (ultimosDigitosVisa.isBlank()) {
                "+ Vincular tarjeta de prueba"
            } else {
                "Reemplazar tarjeta de prueba"
            },
            onClick = {
                numeroTarjeta = ""
                titular = ""
                vencimiento = ""
                mensajeError = ""
                mensajeExito = ""
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
            text = "Simulación académica. Una tarjeta principal por usuario. " +
                    "No se realizan cargos bancarios.",
            color = colores.onSurfaceVariant,
            fontSize = 12.sp
        )
    }



    if (mostrarDialogo) {
        AlertDialog(
            onDismissRequest = {
                cerrarDialogo()
            },

            title = {
                Text("Vincular tarjeta de prueba")
            },

            text = {
                Column(
                    modifier = Modifier.verticalScroll(
                        rememberScrollState()
                    ),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Usa datos ficticios. Solo se conservan " +
                                "los últimos cuatro dígitos, el titular " +
                                "y el vencimiento."
                    )

                    if (ultimosDigitosVisa.isNotBlank()) {
                        Text(
                            text = "La nueva tarjeta reemplazará a la actual.",
                            fontSize = 12.sp
                        )
                    }

                    CampoTarjetaPerfil(
                        valor = titular,
                        onValorCambia = {
                            titular = it
                            mensajeError = ""
                        },
                        etiqueta = "Titular"
                    )

                    CampoTarjetaPerfil(
                        valor = numeroTarjeta,
                        onValorCambia = { entrada ->
                            val limpio = entrada
                                .replace(" ", "")
                                .replace("-", "")

                            if (
                                limpio.length <= 16 &&
                                limpio.all { it in '0'..'9' }
                            ) {
                                numeroTarjeta = limpio
                                mensajeError = ""
                            }
                        },
                        etiqueta = "Número de tarjeta de prueba",
                        ayuda = "16 dígitos ficticios.",
                        tipoTeclado = KeyboardType.Number
                    )

                    CampoTarjetaPerfil(
                        valor = vencimiento,
                        onValorCambia = { entrada ->
                            val limpio = entrada.replace("/", "")

                            if (
                                limpio.length <= 4 &&
                                limpio.all { it in '0'..'9' }
                            ) {
                                vencimiento = if (limpio.length > 2) {
                                    limpio.take(2) + "/" + limpio.drop(2)
                                } else {
                                    limpio
                                }

                                mensajeError = ""
                            }
                        },
                        etiqueta = "Vencimiento MM/AA",
                        placeholder = "12/30",
                        tipoTeclado = KeyboardType.Number
                    )

                    if (mensajeError.isNotBlank()) {
                        Text(
                            text = mensajeError,
                            color = colores.error
                        )
                    }
                }
            },

            confirmButton = {
                BotonPrincipal(
                    texto = "Vincular",
                    onClick = {
                        mensajeError = when {
                            titular.isBlank() -> {
                                "Ingresa el titular."
                            }

                            numeroTarjeta.length != 16 -> {
                                "Ingresa exactamente 16 dígitos."
                            }

                            !ValidacionesTarjeta.vencimientoVigente(
                                vencimiento
                            ) -> {
                                "Ingresa un vencimiento válido en MM/AA."
                            }

                            else -> ""
                        }

                        if (mensajeError.isBlank()) {
                            val guardada = onVincularTarjeta(
                                numeroTarjeta.takeLast(4),
                                titular.trim(),
                                vencimiento
                            )

                            if (guardada) {
                                cerrarDialogo()
                                mensajeExito =
                                    "Tarjeta vinculada correctamente."
                            } else {
                                mensajeError = "No se pudo vincular. " +
                                        "Vuelve a iniciar sesión."
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
                TextButton(
                    onClick = {
                        cerrarDialogo()
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}


@Composable
private fun CampoTarjetaPerfil(
    valor: String,
    onValorCambia: (String) -> Unit,
    etiqueta: String,
    ayuda: String? = null,
    placeholder: String? = null,
    tipoTeclado: KeyboardType = KeyboardType.Text
) {
    CampoTexto(
        valor = valor,
        onValorCambia = onValorCambia,
        etiqueta = etiqueta,
        icono = null,
        ayuda = ayuda,
        placeholder = placeholder,
        tipoTeclado = tipoTeclado,
        forma = OutlinedTextFieldDefaults.shape,
        colores = OutlinedTextFieldDefaults.colors(),
        accionTeclado = ImeAction.Default,
        reservarEspacioAyuda = false
    )
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

    var mostrarTerminos by remember {
        mutableStateOf(false)
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
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
            onCambiar = {
                tema.cambiarModoOscuro(!tema.oscuro)
            }
        )

        Text(
            text = if (tema.sigueSistema) {
                "Se está utilizando el tema del teléfono."
            } else {
                "Se está utilizando tu elección para PagoFlex."
            },
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
            text = "Las notificaciones siguen siendo una preferencia de " +
                    "demostración: todavía no se envían avisos.",
            color = colores.onSurfaceVariant,
            fontSize = 11.sp
        )


        Card(
            onClick = {
                mostrarTerminos = true
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = colores.surface,
                contentColor = colores.onSurface
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "📄",
                    fontSize = 18.sp
                )

                Spacer(Modifier.width(12.dp))

                Text(
                    text = "Términos y condiciones",
                    modifier = Modifier.weight(1f),
                    fontSize = 13.sp
                )

                Text(
                    text = "Ver",
                    color = colores.onSurfaceVariant,
                    fontSize = 12.sp
                )
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
            onDismissRequest = {
                mostrarTerminos = false
            },

            title = {
                Text("Términos y condiciones")
            },

            text = {
                Text(
                    text = "PagoFlex es una demostración académica. " +
                            "Los pagos y tarjetas son simulados. " +
                            "No se realizan cargos bancarios. " +
                            "Usa datos ficticios al vincular una tarjeta."
                )
            },

            confirmButton = {
                TextButton(
                    onClick = {
                        mostrarTerminos = false
                    }
                ) {
                    Text("Cerrar")
                }
            }
        )
    }
}



@Composable
fun CardItemPerfil(
    label: String,
    valor: String
) {
    val colores = MaterialTheme.colorScheme

    TarjetaSeccion(
        forma = RoundedCornerShape(12.dp),
        colores = CardDefaults.cardColors(
            containerColor = colores.surface,
            contentColor = colores.onSurface
        ),
        elevacion = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        ),
        relleno = PaddingValues(14.dp),
        separacion = 4.dp
    ) {
        Text(
            text = label,
            color = colores.onSurfaceVariant,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = valor,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )
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
        elevacion = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        ),
        relleno = PaddingValues(
            horizontal = 14.dp,
            vertical = 8.dp
        ),
        separacion = 0.dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = icono,
                fontSize = 18.sp
            )

            Spacer(Modifier.width(12.dp))

            InterruptorConTexto(
                texto = titulo,
                activado = activado,
                onCambio = {
                    onCambiar()
                },
                modifier = Modifier.weight(1f),
                filaPulsable = false,
                rellenoVertical = 0.dp,
                estiloTexto = LocalTextStyle.current.copy(
                    fontSize = 13.sp
                )
            )
        }
    }
}