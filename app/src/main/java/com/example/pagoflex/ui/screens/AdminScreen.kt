package com.example.pagoflex.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.pagoflex.data.PagoFlexRepository
import com.example.pagoflex.data.ValidacionesDeuda
import com.example.pagoflex.ui.theme.LocalControlTemaPagoFlex
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    repository: PagoFlexRepository,
    usuario: Map<String, String>,
    onCerrarSesion: () -> Unit
) {
    val colores = MaterialTheme.colorScheme
    val tema = LocalControlTemaPagoFlex.current

    val scope = rememberCoroutineScope()
    val listaState = rememberLazyListState()

    val formatoMoneda = remember {
        NumberFormat.getCurrencyInstance(Locale("es", "CL"))
    }

    var mostrarConfiguracion by remember {
        mutableStateOf(false)
    }

    var usuarios by remember {
        mutableStateOf<List<Map<String, String>>>(emptyList())
    }

    var deudas by remember {
        mutableStateOf<List<Map<String, String>>>(emptyList())
    }

    var usuarioSeleccionadoId by remember {
        mutableStateOf<String?>(null)
    }

    var deudaEditandoId by remember {
        mutableStateOf<Int?>(null)
    }

    var deudaAEliminar by remember {
        mutableStateOf<Map<String, String>?>(null)
    }

    var concepto by remember { mutableStateOf("") }
    var empresa by remember { mutableStateOf("") }
    var monto by remember { mutableStateOf("") }
    var vencimiento by remember { mutableStateOf("") }

    var mensaje by remember { mutableStateOf("") }
    var error by remember { mutableStateOf(false) }
    var expandido by remember { mutableStateOf(false) }
    var ocupado by remember { mutableStateOf(false) }

    fun limpiarFormulario() {
        deudaEditandoId = null
        usuarioSeleccionadoId = null
        concepto = ""
        empresa = ""
        monto = ""
        vencimiento = ""
        expandido = false
    }

    suspend fun cargarDatos() {
        val resultado = withContext(Dispatchers.IO) {
            repository.listarClientes() to repository.listarDeudas()
        }

        usuarios = resultado.first
        deudas = resultado.second
    }

    suspend fun recargarDespuesDeOperacion() {
        try {
            cargarDatos()
        } catch (_: Exception) {
            mensaje += " No se pudo recargar el listado. " +
                    "Vuelve a ingresar al panel para verificarlo."
            error = true
        }
    }

    fun guardarDeuda() {
        if (ocupado) return

        val usuarioId = usuarioSeleccionadoId?.toIntOrNull()

        val montoIngresado = monto.trim()
            .replace(",", ".")
            .toDoubleOrNull()

        val fechaIngresada = vencimiento.trim()

        val problema = ValidacionesDeuda.obtenerError(
            usuarioId = usuarioId,
            concepto = concepto,
            empresa = empresa,
            monto = montoIngresado,
            vencimiento = fechaIngresada
        )

        if (problema != null) {
            mensaje = problema
            error = true
            return
        }

        val idUsuario = usuarioId ?: return
        val valorMonto = montoIngresado ?: return
        val idEdicion = deudaEditandoId

        val conceptoGuardar = concepto.trim()
        val empresaGuardar = empresa.trim()

        scope.launch {
            ocupado = true

            try {
                val guardada = withContext(Dispatchers.IO) {
                    if (idEdicion == null) {
                        repository.crearDeuda(
                            usuarioId = idUsuario,
                            concepto = conceptoGuardar,
                            empresa = empresaGuardar,
                            monto = valorMonto,
                            vencimiento = fechaIngresada
                        )
                    } else {
                        repository.actualizarDeuda(
                            deudaId = idEdicion,
                            usuarioId = idUsuario,
                            concepto = conceptoGuardar,
                            empresa = empresaGuardar,
                            monto = valorMonto,
                            vencimiento = fechaIngresada
                        )
                    }
                }

                if (guardada) {
                    limpiarFormulario()

                    mensaje = if (idEdicion == null) {
                        "Deuda creada correctamente."
                    } else {
                        "Deuda modificada correctamente."
                    }

                    error = false
                    recargarDespuesDeOperacion()
                } else {
                    mensaje = if (idEdicion == null) {
                        "No se pudo crear la deuda. Revisa el usuario y los datos."
                    } else {
                        "No se pudo modificar. Puede que ya tenga pagos o que no exista."
                    }

                    error = true
                }
            } catch (_: Exception) {
                mensaje = "Ocurrió un error al guardar. " +
                        "Verifica el listado antes de volver a intentarlo."
                error = true
            } finally {
                ocupado = false
            }
        }
    }

    fun editarDeuda(deuda: Map<String, String>) {
        if (ocupado) return

        val id = deuda["id"]?.toIntOrNull()

        if (id == null) {
            mensaje = "La deuda no tiene un identificador válido."
            error = true
            return
        }

        deudaEditandoId = id

        usuarioSeleccionadoId = deuda["usuarioId"]
            ?: deuda["usuario_id"]

        concepto = deuda["concepto"].orEmpty()
        empresa = deuda["empresa"].orEmpty()

        monto = deuda["monto"].orEmpty().let {
            if (it.endsWith(".0")) {
                it.dropLast(2)
            } else {
                it
            }
        }

        vencimiento = deuda["vencimiento"].orEmpty()
        mensaje = "Editando deuda #$id."
        error = false
        expandido = false

        scope.launch {
            listaState.animateScrollToItem(0)
        }
    }

    fun eliminarDeuda(deuda: Map<String, String>) {
        if (ocupado) return

        val id = deuda["id"]?.toIntOrNull()
        deudaAEliminar = null

        if (id == null) {
            mensaje = "La deuda no tiene un identificador válido."
            error = true
            return
        }

        scope.launch {
            ocupado = true

            try {
                val eliminada = withContext(Dispatchers.IO) {
                    repository.eliminarDeuda(id)
                }

                if (eliminada) {
                    if (deudaEditandoId == id) {
                        limpiarFormulario()
                    }

                    mensaje = "Deuda eliminada correctamente."
                    error = false
                    recargarDespuesDeOperacion()
                } else {
                    mensaje = "No se pudo eliminar. " +
                            "Las deudas con pagos registrados se conservan."
                    error = true
                }
            } catch (_: Exception) {
                mensaje = "Ocurrió un error al eliminar. " +
                        "Verifica el listado antes de volver a intentarlo."
                error = true
            } finally {
                ocupado = false
            }
        }
    }

    LaunchedEffect(repository) {
        ocupado = true

        try {
            cargarDatos()
        } catch (_: Exception) {
            mensaje = "No se pudo cargar la información. " +
                    "Vuelve a ingresar al panel."
            error = true
        } finally {
            ocupado = false
        }
    }

    val usuarioSeleccionado = usuarios.firstOrNull {
        it["id"] == usuarioSeleccionadoId
    }

    Scaffold(
        containerColor = colores.background,

        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "PagoFlex",
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "Panel administrador",
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            mostrarConfiguracion = true
                        },
                        enabled = !ocupado,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = colores.onPrimary,
                            disabledContentColor = colores.onPrimary.copy(
                                alpha = 0.5f
                            )
                        )
                    ) {
                        Text("Configuración")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colores.primary,
                    titleContentColor = colores.onPrimary,
                    actionIconContentColor = colores.onPrimary
                )
            )
        }
    ) { padding ->
        LazyColumn(
            state = listaState,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .imePadding(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = colores.primaryContainer,
                        contentColor = colores.onPrimaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "Hola, ${
                                usuario["nombre"]
                                    .orEmpty()
                                    .ifBlank { "Administrador" }
                            }",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "Crea y administra las deudas de tus clientes."
                        )
                    }
                }
            }

            item {
                OutlinedButton(
                    onClick = onCerrarSesion,
                    enabled = !ocupado,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = colores.error
                    )
                ) {
                    Text(
                        text = "Cerrar sesión",
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (ocupado || mensaje.isNotBlank()) {
                item {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (ocupado) {
                            LinearProgressIndicator(
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        if (mensaje.isNotBlank()) {
                            Text(
                                text = mensaje,
                                color = if (error) {
                                    colores.error
                                } else {
                                    colores.primary
                                }
                            )
                        }
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = colores.surface,
                        contentColor = colores.onSurface
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = if (deudaEditandoId == null) {
                                "Crear una deuda"
                            } else {
                                "Editar deuda #$deudaEditandoId"
                            },
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = colores.primary
                        )

                        if (usuarios.isEmpty() && !ocupado) {
                            Text(
                                text = "No hay clientes registrados. " +
                                        "Crea una cuenta desde el inicio de sesión.",
                                color = colores.onSurfaceVariant
                            )
                        }

                        ExposedDropdownMenuBox(
                            expanded = expandido,
                            onExpandedChange = {
                                if (!ocupado && usuarios.isNotEmpty()) {
                                    expandido = !expandido
                                }
                            }
                        ) {
                            OutlinedTextField(
                                value = usuarioSeleccionado?.let {
                                    "${it["nombre"]} (${it["correo"]})"
                                }.orEmpty(),
                                onValueChange = {},
                                readOnly = true,
                                enabled = !ocupado && usuarios.isNotEmpty(),
                                label = {
                                    Text("Usuario destinatario")
                                },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(
                                        expanded = expandido
                                    )
                                },
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth()
                            )

                            ExposedDropdownMenu(
                                expanded = expandido,
                                onDismissRequest = {
                                    expandido = false
                                }
                            ) {
                                usuarios.forEach { cliente ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = "${cliente["nombre"]} — " +
                                                        "${cliente["correo"]}"
                                            )
                                        },
                                        onClick = {
                                            usuarioSeleccionadoId = cliente["id"]
                                            expandido = false
                                        }
                                    )
                                }
                            }
                        }

                        OutlinedTextField(
                            value = concepto,
                            onValueChange = {
                                concepto = it
                            },
                            label = {
                                Text("Concepto")
                            },
                            enabled = !ocupado,
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = empresa,
                            onValueChange = {
                                empresa = it
                            },
                            label = {
                                Text("Empresa o servicio")
                            },
                            enabled = !ocupado,
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = monto,
                            onValueChange = {
                                monto = it
                            },
                            label = {
                                Text("Monto en CLP")
                            },
                            supportingText = {
                                Text(
                                    text = "Sin separadores de miles. Ejemplo: 45000"
                                )
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Decimal
                            ),
                            enabled = !ocupado,
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = vencimiento,
                            onValueChange = {
                                vencimiento = it
                            },
                            label = {
                                Text("Fecha de vencimiento")
                            },
                            placeholder = {
                                Text("2026-11-30")
                            },
                            supportingText = {
                                Text(
                                    text = "Formato AAAA-MM-DD. Debe ser una fecha real."
                                )
                            },
                            enabled = !ocupado,
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Button(
                            onClick = {
                                guardarDeuda()
                            },
                            enabled = !ocupado && usuarios.isNotEmpty(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colores.secondary,
                                contentColor = colores.onSecondary
                            )
                        ) {
                            Text(
                                text = if (deudaEditandoId == null) {
                                    "Crear deuda"
                                } else {
                                    "Guardar cambios"
                                },
                                fontWeight = FontWeight.Bold
                            )
                        }

                        if (deudaEditandoId != null) {
                            OutlinedButton(
                                onClick = {
                                    limpiarFormulario()
                                    mensaje = ""
                                    error = false
                                },
                                enabled = !ocupado,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Cancelar edición")
                            }
                        }
                    }
                }
            }

            item {
                Text(
                    text = "Deudas registradas (${deudas.size})",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = colores.onBackground
                )
            }

            if (deudas.isEmpty() && !ocupado) {
                item {
                    Text(
                        text = "Todavía no hay deudas registradas.",
                        color = colores.onBackground
                    )
                }
            }

            items(
                items = deudas,
                key = {
                    it["id"].orEmpty()
                }
            ) { deuda ->
                val pendiente = deuda["estado"] == "PENDIENTE"
                val montoDeuda = deuda["monto"]?.toDoubleOrNull() ?: 0.0
                val fecha = deuda["vencimiento"].orEmpty()

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = colores.surface,
                        contentColor = colores.onSurface
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = deuda["concepto"].orEmpty(),
                            color = colores.primary,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "Usuario: ${deuda["nombreUsuario"].orEmpty()}"
                        )

                        Text(
                            text = "Empresa: ${deuda["empresa"].orEmpty()}"
                        )

                        Text(
                            text = "Monto: ${formatoMoneda.format(montoDeuda)}",
                            fontWeight = FontWeight.SemiBold
                        )

                        Text(
                            text = "Vencimiento: $fecha"
                        )

                        if (pendiente) {
                            val dias = ValidacionesDeuda.diasHasta(fecha)

                            Text(
                                text = ValidacionesDeuda.textoVencimiento(fecha),
                                color = if (dias == null || dias < 0) {
                                    colores.error
                                } else {
                                    colores.primary
                                }
                            )
                        }

                        Text(
                            text = "Estado: ${deuda["estado"].orEmpty()}",
                            fontWeight = FontWeight.SemiBold
                        )

                        if (pendiente) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        editarDeuda(deuda)
                                    },
                                    enabled = !ocupado,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Editar")
                                }

                                OutlinedButton(
                                    onClick = {
                                        deudaAEliminar = deuda
                                    },
                                    enabled = !ocupado,
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = colores.error
                                    )
                                ) {
                                    Text("Eliminar")
                                }
                            }
                        } else {
                            Text(
                                text = "Se conserva para mantener el historial de pagos.",
                                color = colores.onSurfaceVariant,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }
    }

    deudaAEliminar?.let { deuda ->
        AlertDialog(
            onDismissRequest = {
                deudaAEliminar = null
            },
            title = {
                Text("Eliminar deuda")
            },
            text = {
                Text(
                    text = "¿Eliminar \"${deuda["concepto"].orEmpty()}\" " +
                            "de ${deuda["nombreUsuario"].orEmpty()}?\n\n" +
                            "Esta acción no se puede deshacer."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        eliminarDeuda(deuda)
                    },
                    enabled = !ocupado
                ) {
                    Text(
                        text = "Eliminar",
                        color = colores.error
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        deudaAEliminar = null
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    if (mostrarConfiguracion) {
        AlertDialog(
            onDismissRequest = {
                mostrarConfiguracion = false
            },
            title = {
                Text(
                    text = "Configuración",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "Modo oscuro",
                                color = colores.onSurface,
                                fontWeight = FontWeight.SemiBold
                            )

                            Text(
                                text = if (tema.oscuro) {
                                    "Activado"
                                } else {
                                    "Desactivado"
                                },
                                color = colores.onSurfaceVariant,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        Switch(
                            checked = tema.oscuro,
                            onCheckedChange = { activar ->
                                tema.cambiarModoOscuro(activar)
                            }
                        )
                    }

                    Text(
                        text = if (tema.sigueSistema) {
                            "PagoFlex está utilizando el tema del teléfono."
                        } else {
                            "PagoFlex está utilizando la apariencia que elegiste."
                        },
                        color = colores.onSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall
                    )

                    if (!tema.sigueSistema) {
                        OutlinedButton(
                            onClick = {
                                tema.usarTemaDelSistema()
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Usar tema del teléfono")
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        mostrarConfiguracion = false
                    }
                ) {
                    Text("Cerrar")
                }
            }
        )
    }
}