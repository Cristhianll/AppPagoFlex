
package com.example.pagoflex.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.pagoflex.data.PagoFlexRepository
import com.example.pagoflex.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsuarioScreen(
    repository: PagoFlexRepository,
    usuario: Map<String, String>,
    onCerrarSesion: () -> Unit
) {
    val usuarioId = usuario["id"]?.toIntOrNull() ?: -1

    var deudas by remember(usuarioId) {
        mutableStateOf(repository.listarDeudas(usuarioId))
    }
    var pagos by remember(usuarioId) {
        mutableStateOf(repository.listarPagos(usuarioId))
    }
    var mostrarHistorial by remember { mutableStateOf(false) }
    var mensaje by remember { mutableStateOf("") }
    var error by remember { mutableStateOf(false) }

    fun actualizar() {
        deudas = repository.listarDeudas(usuarioId)
        pagos = repository.listarPagos(usuarioId)
    }

    val pendientes = deudas.filter { it["estado"] == "PENDIENTE" }
    val totalPendiente = pendientes.sumOf {
        it["monto"]?.toDoubleOrNull() ?: 0.0
    }

    Scaffold(
        containerColor = FondoClaro,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("PagoFlex", fontWeight = FontWeight.Bold)
                        Text(
                            "Mi cuenta",
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                },
                actions = {
                    TextButton(onClick = onCerrarSesion) {
                        Text("Salir", color = Blanco)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Primario,
                    titleContentColor = Blanco
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Primario
                    )
                ) {
                    Column(
                        Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            "Hola, ${usuario["nombre"] ?: "Usuario"}",
                            style = MaterialTheme.typography.headlineSmall,
                            color = Blanco,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Aquí puedes revisar y gestionar tus deudas.",
                            color = PrimarioClaro
                        )
                        HorizontalDivider(color = PrimarioClaro)
                        Text(
                            "Total pendiente",
                            color = PrimarioClaro
                        )
                        Text(
                            "$${"%,.0f".format(java.util.Locale("es", "CL"), totalPendiente)}",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = Acento
                        )
                        Text(
                            "${pendientes.size} deuda(s) pendiente(s)",
                            color = Blanco
                        )
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { mostrarHistorial = false },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (!mostrarHistorial) Primario else PrimarioClaro,
                            contentColor = if (!mostrarHistorial) Blanco else Primario
                        )
                    ) {
                        Text("Mis deudas")
                    }

                    Button(
                        onClick = {
                            mostrarHistorial = true
                            pagos = repository.listarPagos(usuarioId)
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (mostrarHistorial) Primario else PrimarioClaro,
                            contentColor = if (mostrarHistorial) Blanco else Primario
                        )
                    ) {
                        Text("Historial")
                    }
                }
            }

            if (mensaje.isNotEmpty()) {
                item {
                    Text(
                        mensaje,
                        color = if (error) {
                            MaterialTheme.colorScheme.error
                        } else {
                            Exito
                        }
                    )
                }
            }

            if (!mostrarHistorial) {
                if (deudas.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = SuperficieClara
                            )
                        ) {
                            Column(Modifier.padding(20.dp)) {
                                Text(
                                    "No tienes deudas registradas.",
                                    fontWeight = FontWeight.Bold,
                                    color = Primario
                                )
                                Text("Cuando se registre una deuda, aparecerá aquí.")
                            }
                        }
                    }
                } else {
                    items(deudas, key = { it["id"] ?: it.hashCode().toString() }) { deuda ->
                        val pendiente = deuda["estado"] == "PENDIENTE"

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = SuperficieClara
                            ),
                            elevation = CardDefaults.cardElevation(2.dp)
                        ) {
                            Column(
                                Modifier.padding(18.dp),
                                verticalArrangement = Arrangement.spacedBy(7.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        deuda["concepto"].orEmpty(),
                                        modifier = Modifier.weight(1f),
                                        fontWeight = FontWeight.Bold,
                                        color = Primario
                                    )
                                    AssistChip(
                                        onClick = {},
                                        label = {
                                            Text(
                                                if (pendiente) "Pendiente" else "Pagada"
                                            )
                                        },
                                        colors = AssistChipDefaults.assistChipColors(
                                            containerColor = if (pendiente) {
                                                Acento
                                            } else {
                                                ExitoFondo
                                            }
                                        )
                                    )
                                }

                                Text("Empresa: ${deuda["empresa"].orEmpty()}")
                                Text("Vence: ${deuda["vencimiento"].orEmpty()}")

                                Text(
                                    "$${"%,.0f".format(
                                        java.util.Locale("es", "CL"),
                                        deuda["monto"]?.toDoubleOrNull() ?: 0.0
                                    )}",
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Primario
                                )

                                if (pendiente) {
                                    Button(
                                        onClick = {
                                            val id = deuda["id"]?.toIntOrNull()
                                            if (id != null &&
                                                repository.pagarDeuda(id, usuarioId)
                                            ) {
                                                mensaje = "Pago simulado realizado correctamente."
                                                error = false
                                                actualizar()
                                            } else {
                                                mensaje = "No se pudo registrar el pago."
                                                error = true
                                            }
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Primario
                                        ),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Text("Pagar deuda")
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                if (pagos.isEmpty()) {
                    item {
                        Text("Todavía no tienes pagos registrados.")
                    }
                } else {
                    items(pagos, key = { it["id"] ?: it.hashCode().toString() }) { pago ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = SuperficieClara
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(
                                Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    pago["concepto"].orEmpty(),
                                    fontWeight = FontWeight.Bold,
                                    color = Primario
                                )
                                Text(
                                    "Monto: $${"%,.0f".format(
                                        java.util.Locale("es", "CL"),
                                        pago["monto"]?.toDoubleOrNull() ?: 0.0
                                    )}"
                                )
                                Text("Fecha: ${pago["fecha"].orEmpty()}")
                                Text(
                                    "Comprobante: ${pago["comprobante"].orEmpty()}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }

            item {
                OutlinedButton(
                    onClick = { actualizar() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Actualizar información")
                }
            }
        }
    }
}
