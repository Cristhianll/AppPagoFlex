package com.example.pagoflex.ui.screens

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pagoflex.data.remote.AdjuntoResponse
import com.example.pagoflex.data.remote.TicketResponse
import com.example.pagoflex.ui.components.BotonPrincipal
import com.example.pagoflex.ui.components.BotonSecundario
import com.example.pagoflex.ui.components.leerImagenReducida
import com.example.pagoflex.ui.styles.EstilosCampo
import com.example.pagoflex.viewmodel.TicketViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal const val MAX_FOTOS_TICKET = 5

internal fun textoEstadoTicket(ticket: TicketResponse): String = when (ticket.estado) {
    "ABIERTO" -> "Abierto"
    "EN_PROCESO" -> "En proceso"
    "CERRADO" -> if (ticket.resuelto == true) "Cerrado · Resuelto" else "Cerrado · Sin resolver"
    else -> ticket.estado
}

internal fun textoFechaTicket(valor: String?): String =
    valor.orEmpty().take(16).replace('T', ' ')

@Composable
internal fun EtiquetaEstadoTicket(ticket: TicketResponse) {
    val colores = MaterialTheme.colorScheme
    val (fondo, texto) = when {
        ticket.estado == "CERRADO" && ticket.resuelto == true ->
            colores.primaryContainer to colores.onPrimaryContainer
        ticket.estado == "CERRADO" ->
            colores.surfaceVariant to colores.onSurfaceVariant
        ticket.estado == "EN_PROCESO" ->
            colores.secondaryContainer to colores.onSecondaryContainer
        else ->
            colores.tertiaryContainer to colores.onTertiaryContainer
    }
    Surface(color = fondo, contentColor = texto, shape = RoundedCornerShape(50)) {
        Text(
            text = textoEstadoTicket(ticket),
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
internal fun TarjetaTicket(
    ticket: TicketResponse,
    mostrarUsuario: Boolean,
    onClick: () -> Unit
) {
    val colores = MaterialTheme.colorScheme

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = colores.surface,
            contentColor = colores.onSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = ticket.titulo,
                    modifier = Modifier.weight(1f),
                    fontWeight = FontWeight.Bold,
                    maxLines = 2
                )
                Spacer(Modifier.width(8.dp))
                EtiquetaEstadoTicket(ticket)
            }

            if (mostrarUsuario && ticket.usuarioCorreo.orEmpty().isNotBlank()) {
                Text(
                    text = ticket.usuarioCorreo.orEmpty(),
                    color = colores.onSurfaceVariant,
                    fontSize = 13.sp
                )
            }

            if (ticket.referencia.orEmpty().isNotBlank()) {
                Text(
                    text = ticket.referencia.orEmpty(),
                    color = colores.primary,
                    fontSize = 13.sp
                )
            }

            Text(
                text = textoFechaTicket(ticket.fechaCreacion),
                color = colores.onSurfaceVariant,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
internal fun ImagenAdjunta(
    viewModel: TicketViewModel,
    ticketId: Long,
    adjunto: AdjuntoResponse,
    onAbrir: (Bitmap) -> Unit
) {
    val colores = MaterialTheme.colorScheme
    val bitmap by produceState<Bitmap?>(initialValue = null, ticketId, adjunto.id) {
        value = viewModel.descargarAdjunto(ticketId, adjunto.id)?.let {
            BitmapFactory.decodeByteArray(it, 0, it.size)
        }
    }

    Box(
        modifier = Modifier
            .size(96.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(colores.surfaceVariant)
            .clickable(enabled = bitmap != null) { bitmap?.let(onAbrir) },
        contentAlignment = Alignment.Center
    ) {
        val imagen = bitmap
        if (imagen != null) {
            Image(
                bitmap = imagen.asImageBitmap(),
                contentDescription = adjunto.nombreOriginal ?: "Prueba adjunta",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Text("...", color = colores.onSurfaceVariant)
        }
    }
}

@Composable
internal fun MiniaturaLocal(bytes: ByteArray, onQuitar: () -> Unit) {
    val colores = MaterialTheme.colorScheme
    val bitmap = remember(bytes) { BitmapFactory.decodeByteArray(bytes, 0, bytes.size) }

    Box(modifier = Modifier.size(96.dp)) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(12.dp))
                .background(colores.surfaceVariant)
        ) {
            if (bitmap != null) {
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "Foto seleccionada",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }
        Surface(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(4.dp)
                .clickable { onQuitar() },
            shape = CircleShape,
            color = colores.error
        ) {
            Text(
                text = "✕",
                color = colores.onError,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun DetalleTicketContenido(
    viewModel: TicketViewModel,
    ticketId: Long,
    miRol: String,
    puedeResponder: Boolean,
    puedeAdjuntar: Boolean,
    puedeCerrar: Boolean,
    modifier: Modifier = Modifier
) {
    val colores = MaterialTheme.colorScheme
    val estado by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var texto by remember(ticketId) { mutableStateOf("") }
    var fotosNuevas by remember(ticketId) { mutableStateOf<List<ByteArray>>(emptyList()) }
    var cierrePendiente by remember(ticketId) { mutableStateOf<Boolean?>(null) }
    var ampliada by remember { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(ticketId) {
        viewModel.abrirTicket(ticketId)
    }

    val galeria = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            scope.launch {
                val bytes = withContext(Dispatchers.IO) { leerImagenReducida(context, uri) }
                if (bytes != null) {
                    fotosNuevas = fotosNuevas + bytes
                }
            }
        }
    }

    val ticket = estado.ticketActual
    val cerrado = ticket?.estado == "CERRADO"
    val totalFotos = estado.adjuntos.size + fotosNuevas.size

    Column(modifier = modifier.fillMaxSize()) {
        if (estado.cargando || estado.enviando) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }

        if (!estado.error.isNullOrBlank()) {
            Surface(color = colores.errorContainer, contentColor = colores.onErrorContainer) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = estado.error.orEmpty(),
                        modifier = Modifier.weight(1f),
                        fontSize = 14.sp
                    )
                    TextButton(onClick = { viewModel.limpiarMensajes() }) { Text("Cerrar") }
                }
            }
        }

        if (!estado.aviso.isNullOrBlank()) {
            Surface(color = colores.primaryContainer, contentColor = colores.onPrimaryContainer) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = estado.aviso.orEmpty(),
                        modifier = Modifier.weight(1f),
                        fontSize = 14.sp
                    )
                    TextButton(onClick = { viewModel.limpiarMensajes() }) { Text("Cerrar") }
                }
            }
        }

        if (ticket == null) {
            if (!estado.cargando) {
                Text(
                    text = "No se pudo cargar el ticket.",
                    modifier = Modifier.padding(16.dp),
                    color = colores.onBackground
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = colores.surface,
                            contentColor = colores.onSurface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = ticket.titulo,
                                    modifier = Modifier.weight(1f),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                )
                                Spacer(Modifier.width(8.dp))
                                EtiquetaEstadoTicket(ticket)
                            }

                            if (miRol != "USUARIO" && ticket.usuarioCorreo.orEmpty().isNotBlank()) {
                                Text(
                                    text = "Reportado por ${ticket.usuarioCorreo.orEmpty()}",
                                    color = colores.onSurfaceVariant,
                                    fontSize = 13.sp
                                )
                            }

                            if (ticket.referencia.orEmpty().isNotBlank()) {
                                Text(
                                    text = "Relacionado con: ${ticket.referencia.orEmpty()}",
                                    color = colores.primary,
                                    fontSize = 13.sp
                                )
                            }

                            Text(text = ticket.descripcion)

                            Text(
                                text = "Creado: ${textoFechaTicket(ticket.fechaCreacion)}",
                                color = colores.onSurfaceVariant,
                                fontSize = 12.sp
                            )

                            if (cerrado) {
                                Text(
                                    text = "Cerrado: ${textoFechaTicket(ticket.fechaCierre)}",
                                    color = colores.onSurfaceVariant,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }

                if (estado.adjuntos.isNotEmpty()) {
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "Pruebas adjuntas",
                                fontWeight = FontWeight.Bold,
                                color = colores.onBackground
                            )
                            Row(
                                modifier = Modifier.horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                estado.adjuntos.forEach { adjunto ->
                                    ImagenAdjunta(
                                        viewModel = viewModel,
                                        ticketId = ticketId,
                                        adjunto = adjunto,
                                        onAbrir = { ampliada = it }
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    Text(
                        text = "Conversación",
                        fontWeight = FontWeight.Bold,
                        color = colores.onBackground
                    )
                }

                if (estado.mensajes.isEmpty()) {
                    item {
                        Text(
                            text = "Aún no hay mensajes.",
                            color = colores.onSurfaceVariant
                        )
                    }
                }

                items(estado.mensajes, key = { it.id }) { mensaje ->
                    val mio = mensaje.rolAutor == miRol
                    val autor = when {
                        mio -> "Tú"
                        mensaje.rolAutor == "GESTOR" -> "Gestor"
                        else -> "Usuario"
                    }

                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = if (mio) Alignment.CenterEnd else Alignment.CenterStart
                    ) {
                        Surface(
                            color = if (mio) colores.primaryContainer else colores.surfaceVariant,
                            contentColor = if (mio) colores.onPrimaryContainer else colores.onSurfaceVariant,
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.widthIn(max = 300.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(text = autor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Text(text = mensaje.mensaje)
                                Text(text = textoFechaTicket(mensaje.fecha), fontSize = 10.sp)
                            }
                        }
                    }
                }

                if (cerrado) {
                    item {
                        Text(
                            text = if (ticket.resuelto == true) {
                                "Este ticket fue cerrado como resuelto."
                            } else {
                                "Este ticket fue cerrado sin resolver."
                            },
                            color = colores.onSurfaceVariant,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            if (!cerrado && (puedeResponder || puedeCerrar)) {
                Surface(tonalElevation = 3.dp, color = colores.surface) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (fotosNuevas.isNotEmpty()) {
                            Row(
                                modifier = Modifier.horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                fotosNuevas.forEachIndexed { indice, bytes ->
                                    MiniaturaLocal(bytes) {
                                        fotosNuevas = fotosNuevas.filterIndexed { j, _ -> j != indice }
                                    }
                                }
                            }
                        }

                        if (puedeResponder) {
                            OutlinedTextField(
                                value = texto,
                                onValueChange = { if (it.length <= 1000) texto = it },
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text("Escribe un mensaje") },
                                minLines = 1,
                                maxLines = 4,
                                shape = EstilosCampo.forma,
                                colors = EstilosCampo.colores()
                            )

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                if (puedeAdjuntar) {
                                    BotonSecundario(
                                        texto = "Foto ($totalFotos/$MAX_FOTOS_TICKET)",
                                        onClick = { galeria.launch("image/*") },
                                        habilitado = totalFotos < MAX_FOTOS_TICKET && !estado.enviando,
                                        modifier = Modifier.weight(1f),
                                        altura = 48.dp
                                    )
                                }
                                BotonPrincipal(
                                    texto = "Enviar",
                                    onClick = {
                                        viewModel.responder(ticketId, texto, fotosNuevas)
                                        texto = ""
                                        fotosNuevas = emptyList()
                                    },
                                    habilitado = (texto.isNotBlank() || fotosNuevas.isNotEmpty()) &&
                                            !estado.enviando,
                                    modifier = Modifier.weight(1f),
                                    altura = 48.dp
                                )
                            }
                        }

                        if (puedeCerrar) {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                BotonSecundario(
                                    texto = "Cerrar: resuelto",
                                    onClick = { cierrePendiente = true },
                                    habilitado = !estado.enviando,
                                    modifier = Modifier.weight(1f),
                                    altura = 48.dp
                                )
                                BotonSecundario(
                                    texto = "Cerrar: sin resolver",
                                    onClick = { cierrePendiente = false },
                                    habilitado = !estado.enviando,
                                    modifier = Modifier.weight(1f),
                                    altura = 48.dp
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    cierrePendiente?.let { resuelto ->
        AlertDialog(
            onDismissRequest = { cierrePendiente = null },
            title = { Text("Cerrar ticket") },
            text = {
                Text(
                    if (resuelto) {
                        "El ticket quedará cerrado y marcado como resuelto. Ya no se podrán enviar más mensajes."
                    } else {
                        "El ticket quedará cerrado sin resolver. Ya no se podrán enviar más mensajes."
                    }
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        cierrePendiente = null
                        viewModel.cerrar(ticketId, resuelto)
                    }
                ) { Text("Cerrar ticket") }
            },
            dismissButton = {
                TextButton(onClick = { cierrePendiente = null }) { Text("Cancelar") }
            }
        )
    }

    ampliada?.let { imagen ->
        AlertDialog(
            onDismissRequest = { ampliada = null },
            confirmButton = {
                TextButton(onClick = { ampliada = null }) { Text("Cerrar") }
            },
            text = {
                Image(
                    bitmap = imagen.asImageBitmap(),
                    contentDescription = "Prueba adjunta ampliada",
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.Fit
                )
            }
        )
    }
}
