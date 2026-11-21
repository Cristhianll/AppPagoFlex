package com.example.pagoflex.ui.screens

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pagoflex.ui.components.BotonPrincipal
import com.example.pagoflex.ui.components.BotonSecundario
import com.example.pagoflex.ui.components.CampoTexto
import com.example.pagoflex.ui.components.SelectorDesplegable
import com.example.pagoflex.ui.components.leerImagenReducida
import com.example.pagoflex.ui.styles.EstilosCampo
import com.example.pagoflex.viewmodel.TicketUiState
import com.example.pagoflex.viewmodel.TicketViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private enum class VistaTicket { LISTA, NUEVO, DETALLE }

private fun etiquetaDeuda(deuda: Map<String, String>): String {
    val base = "${deuda["concepto"].orEmpty()} · ${deuda["empresa"].orEmpty()}"
    val total = deuda["cantidadCuotas"]?.toIntOrNull() ?: 1
    val actual = deuda["cuotaActual"]?.toIntOrNull()
    val cuota = if (total >= 2 && actual != null) " (cuota $actual de $total)" else ""
    val pagada = if (deuda["estado"] == "PAGADA") " [pagada]" else ""
    return base + cuota + pagada
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketsUsuarioScreen(
    viewModel: TicketViewModel,
    deudas: List<Map<String, String>>,
    onVolver: () -> Unit
) {
    val colores = MaterialTheme.colorScheme
    val estado by viewModel.uiState.collectAsState()

    var vista by remember { mutableStateOf(VistaTicket.LISTA) }
    var ticketId by remember { mutableStateOf<Long?>(null) }

    LaunchedEffect(Unit) {
        viewModel.cargarTickets()
    }

    LaunchedEffect(estado.ticketCreadoId) {
        if (estado.ticketCreadoId != null) {
            vista = VistaTicket.LISTA
            viewModel.limpiarCreado()
        }
    }

    fun volverALista() {
        vista = VistaTicket.LISTA
        ticketId = null
        viewModel.cerrarDetalle()
        viewModel.cargarTickets()
    }

    BackHandler(enabled = vista != VistaTicket.LISTA) {
        volverALista()
    }

    Scaffold(
        containerColor = colores.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        when (vista) {
                            VistaTicket.LISTA -> "Reportar un problema"
                            VistaTicket.NUEVO -> "Nuevo reporte"
                            VistaTicket.DETALLE -> "Detalle del reporte"
                        }
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (vista == VistaTicket.LISTA) onVolver() else volverALista()
                        }
                    ) {
                        Text("←", fontSize = 22.sp, color = colores.onPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colores.primary,
                    titleContentColor = colores.onPrimary,
                    navigationIconContentColor = colores.onPrimary,
                    actionIconContentColor = colores.onPrimary
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (vista) {
                VistaTicket.LISTA -> ListaTicketsUsuario(
                    estado = estado,
                    onNuevo = {
                        viewModel.limpiarMensajes()
                        vista = VistaTicket.NUEVO
                    },
                    onAbrir = {
                        viewModel.limpiarMensajes()
                        ticketId = it
                        vista = VistaTicket.DETALLE
                    },
                    onActualizar = { viewModel.cargarTickets() },
                    onCerrarMensaje = { viewModel.limpiarMensajes() }
                )

                VistaTicket.NUEVO -> FormularioTicket(
                    deudas = deudas,
                    estado = estado,
                    onEnviar = { titulo, descripcion, deudaId, referencia, fotos ->
                        viewModel.crear(titulo, descripcion, deudaId, referencia, fotos)
                    }
                )

                VistaTicket.DETALLE -> {
                    val id = ticketId
                    if (id != null) {
                        DetalleTicketContenido(
                            viewModel = viewModel,
                            ticketId = id,
                            miRol = "USUARIO",
                            puedeResponder = true,
                            puedeAdjuntar = true,
                            puedeCerrar = false
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ListaTicketsUsuario(
    estado: TicketUiState,
    onNuevo: () -> Unit,
    onAbrir: (Long) -> Unit,
    onActualizar: () -> Unit,
    onCerrarMensaje: () -> Unit
) {
    val colores = MaterialTheme.colorScheme

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Si tuviste un problema con una cuota o con un pago, cuéntanos. " +
                        "Un gestor revisará tu reporte y te responderá por aquí.",
                color = colores.onSurfaceVariant
            )
        }

        item {
            BotonPrincipal(
                texto = "Reportar un problema",
                onClick = onNuevo,
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (!estado.aviso.isNullOrBlank()) {
            item {
                Surface(
                    color = colores.primaryContainer,
                    contentColor = colores.onPrimaryContainer,
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = estado.aviso.orEmpty(),
                            modifier = Modifier
                                .weight(1f)
                                .padding(vertical = 8.dp)
                        )
                        TextButton(onClick = onCerrarMensaje) { Text("Cerrar") }
                    }
                }
            }
        }

        if (!estado.error.isNullOrBlank()) {
            item {
                Text(text = estado.error.orEmpty(), color = colores.error)
            }
        }

        item {
            Row {
                Text(
                    text = "Mis reportes",
                    modifier = Modifier.weight(1f),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = colores.onBackground
                )
                TextButton(onClick = onActualizar, enabled = !estado.cargando) {
                    Text(if (estado.cargando) "Actualizando..." else "Actualizar")
                }
            }
        }

        if (estado.cargando) {
            item { LinearProgressIndicator(modifier = Modifier.fillMaxWidth()) }
        }

        if (estado.tickets.isEmpty() && !estado.cargando) {
            item {
                Text(
                    text = "Aún no has reportado ningún problema.",
                    color = colores.onSurfaceVariant
                )
            }
        }

        items(estado.tickets, key = { it.id }) { ticket ->
            TarjetaTicket(
                ticket = ticket,
                mostrarUsuario = false,
                onClick = { onAbrir(ticket.id) }
            )
        }
    }
}

@Composable
private fun FormularioTicket(
    deudas: List<Map<String, String>>,
    estado: TicketUiState,
    onEnviar: (String, String, Long?, String?, List<ByteArray>) -> Unit
) {
    val colores = MaterialTheme.colorScheme
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var seleccion by remember { mutableIntStateOf(0) }
    var titulo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var fotos by remember { mutableStateOf<List<ByteArray>>(emptyList()) }

    val opciones = remember(deudas) {
        listOf("Sin deuda específica") + deudas.map { etiquetaDeuda(it) }
    }

    val galeria = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            scope.launch {
                val bytes = withContext(Dispatchers.IO) { leerImagenReducida(context, uri) }
                if (bytes != null) {
                    fotos = fotos + bytes
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Cuéntanos qué pasó. Puedes adjuntar fotos como prueba, por ejemplo " +
                    "el comprobante de tu pago.",
            color = colores.onSurfaceVariant
        )

        SelectorDesplegable(
            etiqueta = "Deuda relacionada (opcional)",
            textoSeleccionado = opciones.getOrElse(seleccion) { opciones.first() },
            opciones = opciones,
            onSeleccion = { seleccion = it },
            habilitado = !estado.enviando
        )

        CampoTexto(
            valor = titulo,
            onValorCambia = { if (it.length <= 120) titulo = it },
            etiqueta = "Título del problema",
            habilitado = !estado.enviando
        )

        OutlinedTextField(
            value = descripcion,
            onValueChange = { if (it.length <= 1000) descripcion = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Describe el problema") },
            minLines = 4,
            maxLines = 8,
            enabled = !estado.enviando,
            shape = EstilosCampo.forma,
            colors = EstilosCampo.colores()
        )

        if (fotos.isNotEmpty()) {
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                fotos.forEachIndexed { indice, bytes ->
                    MiniaturaLocal(bytes) {
                        fotos = fotos.filterIndexed { j, _ -> j != indice }
                    }
                }
            }
        }

        BotonSecundario(
            texto = "Adjuntar foto (${fotos.size}/$MAX_FOTOS_TICKET)",
            onClick = { galeria.launch("image/*") },
            habilitado = fotos.size < MAX_FOTOS_TICKET && !estado.enviando,
            modifier = Modifier.fillMaxWidth()
        )

        if (!estado.error.isNullOrBlank()) {
            Text(text = estado.error.orEmpty(), color = colores.error)
        }

        if (estado.enviando) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }

        BotonPrincipal(
            texto = if (estado.enviando) "Enviando..." else "Enviar reporte",
            onClick = {
                val elegida = if (seleccion > 0) deudas.getOrNull(seleccion - 1) else null
                onEnviar(
                    titulo,
                    descripcion,
                    elegida?.get("id")?.toLongOrNull(),
                    elegida?.let { etiquetaDeuda(it).take(200) },
                    fotos
                )
            },
            habilitado = titulo.isNotBlank() && descripcion.isNotBlank() && !estado.enviando,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
