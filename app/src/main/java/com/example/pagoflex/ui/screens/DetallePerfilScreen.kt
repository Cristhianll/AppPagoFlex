package com.example.pagoflex.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pagoflex.ui.components.FotoPerfilEditable
import com.example.pagoflex.ui.theme.LocalControlTemaPagoFlex
import java.util.Calendar

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

            // Foto del usuario y botón para tomar una nueva.
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

    Button(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.height(40.dp),
        contentPadding = PaddingValues(horizontal = 18.dp),
        colors = ButtonDefaults.buttonColors(
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
        )
    ) {
        Text(
            text = titulo,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
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

private fun vencimientoTarjetaValido(texto: String): Boolean {
    if (!texto.matches(Regex("[0-9]{2}/[0-9]{2}"))) {
        return false
    }

    val mes = texto.take(2).toIntOrNull() ?: return false
    val anioCorto = texto.takeLast(2).toIntOrNull() ?: return false
    val anio = 2000 + anioCorto

    if (mes !in 1..12) {
        return false
    }

    val hoy = Calendar.getInstance()
    val anioActual = hoy.get(Calendar.YEAR)
    val mesActual = hoy.get(Calendar.MONTH) + 1

    return anio > anioActual ||
            (anio == anioActual && mes >= mesActual)
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

    var mostrarDialogo by remember { mutableStateOf(false) }
    var numeroTarjeta by remember { mutableStateOf("") }
    var titular by remember { mutableStateOf("") }
    var vencimiento by remember { mutableStateOf("") }
    var mensajeError by remember { mutableStateOf("") }
    var mensajeExito by remember { mutableStateOf("") }

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
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = colores.primaryContainer,
                    contentColor = colores.onPrimaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
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
                        val vigente = vencimientoTarjetaValido(
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
            }
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = colores.surface,
                    contentColor = colores.onSurface
                )
            ) {
                Text(
                    text = "Aún no tienes tarjetas vinculadas.",
                    modifier = Modifier.padding(20.dp)
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

        Button(
            onClick = {
                numeroTarjeta = ""
                titular = ""
                vencimiento = ""
                mensajeError = ""
                mensajeExito = ""
                mostrarDialogo = true
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colores.secondary,
                contentColor = colores.onSecondary
            )
        ) {
            Text(
                text = if (ultimosDigitosVisa.isBlank()) {
                    "+ Vincular tarjeta de prueba"
                } else {
                    "Reemplazar tarjeta de prueba"
                },
                fontWeight = FontWeight.Bold
            )
        }

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
                                "los últimos cuatro dígitos, el titular y el vencimiento."
                    )

                    if (ultimosDigitosVisa.isNotBlank()) {
                        Text(
                            text = "La nueva tarjeta reemplazará a la actual.",
                            fontSize = 12.sp
                        )
                    }

                    OutlinedTextField(
                        value = titular,
                        onValueChange = {
                            titular = it
                            mensajeError = ""
                        },
                        label = {
                            Text("Titular")
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = numeroTarjeta,
                        onValueChange = { entrada ->
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
                        label = {
                            Text("Número de tarjeta de prueba")
                        },
                        supportingText = {
                            Text("16 dígitos ficticios.")
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = vencimiento,
                        onValueChange = { entrada ->
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
                        label = {
                            Text("Vencimiento MM/AA")
                        },
                        placeholder = {
                            Text("12/30")
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        ),
                        modifier = Modifier.fillMaxWidth()
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
                Button(
                    onClick = {
                        mensajeError = when {
                            titular.isBlank() ->
                                "Ingresa el titular."

                            numeroTarjeta.length != 16 ->
                                "Ingresa exactamente 16 dígitos."

                            !vencimientoTarjetaValido(vencimiento) ->
                                "Ingresa un vencimiento válido en MM/AA."

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
                                mensajeExito = "Tarjeta vinculada correctamente."
                            } else {
                                mensajeError = "No se pudo vincular. " +
                                        "Vuelve a iniciar sesión."
                            }
                        }
                    }
                ) {
                    Text("Vincular")
                }
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
            OutlinedButton(
                onClick = tema.usarTemaDelSistema,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Usar tema del teléfono")
            }
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

        Button(
            onClick = onCerrarSesion,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colores.error,
                contentColor = colores.onError
            )
        ) {
            Text(
                text = "Cerrar sesión actual",
                fontWeight = FontWeight.Bold
            )
        }
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

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = colores.surface,
            contentColor = colores.onSurface
        )
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
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
}

@Composable
fun OpcionSwitchItem(
    icono: String,
    titulo: String,
    activado: Boolean,
    onCambiar: () -> Unit
) {
    val colores = MaterialTheme.colorScheme

    Card(
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
                .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = icono,
                fontSize = 18.sp
            )

            Spacer(Modifier.width(12.dp))

            Text(
                text = titulo,
                modifier = Modifier.weight(1f),
                fontSize = 13.sp
            )

            Switch(
                checked = activado,
                onCheckedChange = {
                    onCambiar()
                }
            )
        }
    }
}