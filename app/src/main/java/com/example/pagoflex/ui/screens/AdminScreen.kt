
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
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    repository: PagoFlexRepository,
    usuario: Map<String, String>,
    onCerrarSesion: () -> Unit
) {
    var usuarios by remember { mutableStateOf(repository.listarUsuarios()) }
    var deudas by remember { mutableStateOf(repository.listarDeudas()) }
    var usuarioSeleccionado by remember { mutableStateOf<Map<String, String>?>(null) }

    var concepto by remember { mutableStateOf("") }
    var empresa by remember { mutableStateOf("") }
    var monto by remember { mutableStateOf("") }
    var vencimiento by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf("") }
    var error by remember { mutableStateOf(false) }
    var expandido by remember { mutableStateOf(false) }

    fun actualizar() {
        usuarios = repository.listarUsuarios()
        deudas = repository.listarDeudas()
    }

    fun fechaValida(fechaTexto: String): Boolean {
        return try {
            val formato = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            formato.isLenient = false
            val fecha = formato.parse(fechaTexto)
            fecha != null && formato.format(fecha) == fechaTexto
        } catch (_: Exception) {
            false
        }
    }

    Scaffold(
        containerColor = FondoClaro,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("PagoFlex", fontWeight = FontWeight.Bold)
                        Text(
                            "Panel administrador",
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                },
                actions = {
                    TextButton(onClick = onCerrarSesion) {
                        Text("Salir")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Primario,
                    titleContentColor = Blanco,
                    actionIconContentColor = Blanco
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
                    colors = CardDefaults.cardColors(containerColor = Primario),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.padding(20.dp)) {
                        Text(
                            "Hola, ${usuario["nombre"] ?: "Administrador"}",
                            color = Blanco,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Administra las cuentas y sus deudas.",
                            color = PrimarioClaro
                        )
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = SuperficieClara
                    )
                ) {
                    Column(
                        Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            "Crear una deuda",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Primario
                        )

                        ExposedDropdownMenuBox(
                            expanded = expandido,
                            onExpandedChange = { expandido = !expandido }
                        ) {
                            OutlinedTextField(
                                value = usuarioSeleccionado?.let {
                                    "${it["nombre"]} (${it["correo"]})"
                                } ?: "",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Usuario destinatario") },
                                placeholder = { Text("Selecciona una cuenta") },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(
                                        expanded = expandido
                                    )
                                },
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )

                            ExposedDropdownMenu(
                                expanded = expandido,
                                onDismissRequest = { expandido = false }
                            ) {
                                usuarios.forEach { u ->
                                    DropdownMenuItem(
                                        text = {
                                            Text("${u["nombre"]} — ${u["correo"]}")
                                        },
                                        onClick = {
                                            usuarioSeleccionado = u
                                            expandido = false
                                        }
                                    )
                                }
                            }
                        }

                        OutlinedTextField(
                            value = concepto,
                            onValueChange = { concepto = it },
                            label = { Text("Concepto de la deuda") },
                            placeholder = { Text("Ej. Cuenta de electricidad") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = empresa,
                            onValueChange = { empresa = it },
                            label = { Text("Empresa o servicio") },
                            placeholder = { Text("Ej. Empresa eléctrica") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = monto,
                            onValueChange = {
                                if (it.isEmpty() || it.matches(Regex("""\d+([.,]\d{0,2})?"""))) {
                                    monto = it
                                }
                            },
                            label = { Text("Monto en pesos chilenos") },
                            placeholder = { Text("45000") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = vencimiento,
                            onValueChange = { vencimiento = it },
                            label = { Text("Fecha de vencimiento") },
                            placeholder = { Text("2026-11-30") },
                            supportingText = { Text("Formato: AAAA-MM-DD") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        if (mensaje.isNotEmpty()) {
                            Text(
                                mensaje,
                                color = if (error) {
                                    MaterialTheme.colorScheme.error
                                } else {
                                    Exito
                                },
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        Button(
                            onClick = {
                                val valor = monto.replace(",", ".").toDoubleOrNull()
                                val id = usuarioSeleccionado?.get("id")?.toIntOrNull()

                                when {
                                    id == null -> {
                                        mensaje = "Selecciona un usuario."
                                        error = true
                                    }
                                    concepto.isBlank() || empresa.isBlank() -> {
                                        mensaje = "Completa el concepto y la empresa."
                                        error = true
                                    }
                                    valor == null || valor <= 0 -> {
                                        mensaje = "Ingresa un monto mayor que cero."
                                        error = true
                                    }
                                    !fechaValida(vencimiento) -> {
                                        mensaje = "La fecha no es válida. Usa AAAA-MM-DD."
                                        error = true
                                    }
                                    else -> {
                                        val creada = repository.crearDeuda(
                                            id, concepto.trim(), empresa.trim(),
                                            valor, vencimiento
                                        )
                                        if (creada) {
                                            concepto = ""
                                            empresa = ""
                                            monto = ""
                                            vencimiento = ""
                                            mensaje = "Deuda creada correctamente."
                                            error = false
                                            actualizar()
                                        } else {
                                            mensaje = "No se pudo crear la deuda."
                                            error = true
                                        }
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Primario
                            )
                        ) {
                            Text("Guardar deuda", fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = { actualizar() },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Actualizar información")
                        }
                    }
                }
            }

            item {
                Text(
                    "Deudas registradas",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Primario
                )
            }

            if (deudas.isEmpty()) {
                item {
                    Text("Todavía no hay deudas registradas.")
                }
            } else {
                items(deudas, key = { it["id"] ?: it.hashCode().toString() }) { deuda ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = SuperficieClara
                        )
                    ) {
                        Column(
                            Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Text(
                                deuda["concepto"].orEmpty(),
                                fontWeight = FontWeight.Bold,
                                color = Primario
                            )
                            Text("Usuario: ${deuda["nombreUsuario"].orEmpty()}")
                            Text("Empresa: ${deuda["empresa"].orEmpty()}")
                            Text(
                                "Monto: $${deuda["monto"].orEmpty()}",
                                fontWeight = FontWeight.SemiBold
                            )
                            Text("Vencimiento: ${deuda["vencimiento"].orEmpty()}")
                            AssistChip(
                                onClick = {},
                                label = { Text(deuda["estado"].orEmpty()) }
                            )
                        }
                    }
                }
            }
        }
    }
}
