package com.example.pagoflex.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.pagoflex.viewmodel.TicketViewModel

private val FILTROS_TICKET = listOf(
    "Abiertos" to "ABIERTO",
    "En proceso" to "EN_PROCESO",
    "Cerrados" to "CERRADO",
    "Todos" to null
)

@Composable
fun PanelTickets(
    viewModel: TicketViewModel,
    miRol: String,
    puedeCerrar: Boolean
) {
    val colores = MaterialTheme.colorScheme
    val estado by viewModel.uiState.collectAsState()

    var ticketId by remember { mutableStateOf<Long?>(null) }
    var filtro by remember { mutableStateOf<String?>("ABIERTO") }

    LaunchedEffect(filtro) {
        viewModel.cargarTickets(filtro)
    }

    fun volverALista() {
        ticketId = null
        viewModel.cerrarDetalle()
        viewModel.cargarTickets(filtro)
    }

    BackHandler(enabled = ticketId != null) {
        volverALista()
    }

    val id = ticketId

    if (id != null) {
        Column(modifier = Modifier.fillMaxSize()) {
            TextButton(onClick = { volverALista() }) {
                Text("← Volver a los tickets")
            }
            DetalleTicketContenido(
                viewModel = viewModel,
                ticketId = id,
                miRol = miRol,
                puedeResponder = puedeCerrar,
                puedeAdjuntar = false,
                puedeCerrar = puedeCerrar,
                modifier = Modifier.weight(1f)
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = "Reportes de los usuarios",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge,
                    color = colores.onBackground
                )
            }

            item {
                Text(
                    text = if (puedeCerrar) {
                        "Responde a los usuarios y cierra cada ticket como resuelto o sin resolver."
                    } else {
                        "Este perfil es de solo lectura: puedes consultar los tickets, no responderlos."
                    },
                    color = colores.onSurfaceVariant
                )
            }

            item {
                Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                    FILTROS_TICKET.forEach { (titulo, valor) ->
                        val seleccionado = filtro == valor
                        TextButton(onClick = { filtro = valor }) {
                            Text(
                                text = titulo,
                                fontWeight = if (seleccionado) FontWeight.Bold else FontWeight.Normal,
                                color = if (seleccionado) colores.primary else colores.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            if (estado.cargando) {
                item { LinearProgressIndicator(modifier = Modifier.fillMaxWidth()) }
            }

            if (!estado.error.isNullOrBlank()) {
                item { Text(text = estado.error.orEmpty(), color = colores.error) }
            }

            if (estado.tickets.isEmpty() && !estado.cargando) {
                item {
                    Text(
                        text = "No hay tickets en esta categoría.",
                        color = colores.onSurfaceVariant
                    )
                }
            }

            items(estado.tickets, key = { it.id }) { ticket ->
                TarjetaTicket(
                    ticket = ticket,
                    mostrarUsuario = true,
                    onClick = {
                        viewModel.limpiarMensajes()
                        ticketId = ticket.id
                    }
                )
            }
        }
    }
}
