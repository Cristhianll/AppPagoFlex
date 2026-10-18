
package com.example.pagoflex.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pagoflex.data.PagoFlexDbHelper
import java.text.NumberFormat
import java.util.Locale

private val AzulPagoFlex = Color(0xFF0B2D4D)
private val VerdePagoFlex = Color(0xFFD0FFAA)
private val FondoPagoFlex = Color(0xFFF1F5F9)
private val GrisPagoFlex = Color(0xFF64748B)

@Composable
fun UsuarioScreen(
    db: PagoFlexDbHelper,
    usuarioId: Int,
    nombreUsuario: String = "Usuario",
    onSalir: () -> Unit = {}
){
    var actualizar by remember { mutableIntStateOf(0) }
    var mostrarHistorial by remember { mutableStateOf(false) }
    var deudaSeleccionadaId by remember { mutableStateOf<Int?>(null) }
    var mensajePago by remember { mutableStateOf("") }

    val deudas = remember(usuarioId, actualizar) {
        db.listarDeudas(usuarioId)
    }

    val pagos = remember(usuarioId, actualizar) {
        db.listarPagos(usuarioId)
    }

    val deudaSeleccionada = deudas.firstOrNull {
        it["id"]?.toIntOrNull() == deudaSeleccionadaId &&
                it["estado"] == "PENDIENTE"
    }

    // Si se seleccionó una deuda, mostrar su detalle
    if (deudaSeleccionada != null) {
        DetallePagoScreen(
            onVolverAtras = {
                deudaSeleccionadaId = null
            },
            onConfirmarPago = {
                val idDeuda = deudaSeleccionadaId

                if (idDeuda != null) {
                    val resultado = db.pagarDeuda(
                        deudaId = idDeuda,
                        usuarioId = usuarioId
                    )

                    if (resultado) {
                        actualizar++
                        deudaSeleccionadaId = null
                        mensajePago = "Pago realizado correctamente."
                    } else {
                        mensajePago = "No se pudo realizar el pago. Inténtalo nuevamente."
                    }
                }
            },
            empresa = deudaSeleccionada["empresa"].orEmpty(),
            concepto = deudaSeleccionada["concepto"].orEmpty(),
            monto = deudaSeleccionada["monto"]?.toDoubleOrNull() ?: 0.0
        )
        return
    }

    val pendientes = deudas.filter {
        it["estado"] == "PENDIENTE"
    }

    val totalPendiente = pendientes.sumOf {
        it["monto"]?.toDoubleOrNull() ?: 0.0
    }

    val formatoPesos = NumberFormat.getCurrencyInstance(
        Locale("es", "CL")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoPagoFlex)
    ) {
        // Encabezado
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    AzulPagoFlex,
                    RoundedCornerShape(
                        bottomStart = 28.dp,
                        bottomEnd = 28.dp
                    )
                )
                .padding(24.dp)
        ) {
            Text(
                text = "PagoFlex",
                color = VerdePagoFlex,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Hola, $nombreUsuario",
                color = Color.White,
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Administra tus cuentas y pagos",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 14.sp
            )
        }

        // Resumen de deuda
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .offset(y = (-16).dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(3.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = "Total pendiente",
                    color = GrisPagoFlex,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = formatoPesos.format(totalPendiente),
                    color = AzulPagoFlex,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "${pendientes.size} cuenta(s) pendiente(s)",
                    color = GrisPagoFlex,
                    fontSize = 13.sp
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (mostrarHistorial) "Historial de pagos" else "Mis cuentas",
                color = AzulPagoFlex,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            TextButton(
                onClick = {
                    mostrarHistorial = !mostrarHistorial
                    mensajePago = ""
                }
            ) {
                Text(
                    text = if (mostrarHistorial) "Ver pendientes" else "Ver historial",
                    color = AzulPagoFlex
                )
            }
        }

        if (mensajePago.isNotBlank()) {
            Text(
                text = mensajePago,
                modifier = Modifier.padding(
                    horizontal = 20.dp,
                    vertical = 8.dp
                ),
                color = if (mensajePago.startsWith("Pago realizado")) {
                    Color(0xFF247A3D)
                } else {
                    Color(0xFFB42318)
                },
                fontWeight = FontWeight.Medium
            )
        }

        if (!mostrarHistorial) {
            if (pendientes.isEmpty()) {
                EstadoVacio(
                    titulo = "¡Todo al día!",
                    descripcion = "No tienes cuentas pendientes por pagar."
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = 20.dp,
                        end = 20.dp,
                        top = 8.dp,
                        bottom = 24.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = pendientes,
                        key = { it["id"].orEmpty() }
                    ) { deuda ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White
                            ),
                            elevation = CardDefaults.cardElevation(2.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(18.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Column(
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(
                                            text = deuda["empresa"]
                                                .orEmpty()
                                                .ifBlank { "Empresa" },
                                            color = AzulPagoFlex,
                                            fontSize = 17.sp,
                                            fontWeight = FontWeight.Bold
                                        )

                                        Spacer(modifier = Modifier.height(4.dp))

                                        Text(
                                            text = deuda["concepto"]
                                                .orEmpty()
                                                .ifBlank { "Cuenta pendiente" },
                                            color = GrisPagoFlex,
                                            fontSize = 14.sp
                                        )
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = Color(0xFFFFF0C2)
                                    ) {
                                        Text(
                                            text = "Pendiente",
                                            modifier = Modifier.padding(
                                                horizontal = 9.dp,
                                                vertical = 5.dp
                                            ),
                                            color = Color(0xFF805B00),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Text(
                                    text = formatoPesos.format(
                                        deuda["monto"]?.toDoubleOrNull() ?: 0.0
                                    ),
                                    color = AzulPagoFlex,
                                    fontSize = 23.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(modifier = Modifier.height(5.dp))

                                Text(
                                    text = "Vencimiento: ${
                                        deuda["vencimiento"].orEmpty()
                                            .ifBlank { "No informado" }
                                    }",
                                    color = GrisPagoFlex,
                                    fontSize = 12.sp
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                Button(
                                    onClick = {
                                        mensajePago = ""
                                        deudaSeleccionadaId =
                                            deuda["id"]?.toIntOrNull()
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = AzulPagoFlex,
                                        contentColor = Color.White
                                    ),
                                    enabled = deuda["id"]?.toIntOrNull() != null
                                ) {
                                    Text(
                                        text = "Revisar y pagar",
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        } else {
            if (pagos.isEmpty()) {
                EstadoVacio(
                    titulo = "Sin pagos todavía",
                    descripcion = "Aquí aparecerán las cuentas que hayas pagado."
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = 20.dp,
                        end = 20.dp,
                        top = 8.dp,
                        bottom = 24.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = pagos,
                        key = { pago ->
                            pago["id"] ?: "${pago["fecha"]}-${pago["monto"]}"
                        }
                    ) { pago ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(18.dp)
                            ) {
                                Text(
                                    text = pago["concepto"]
                                        .orEmpty()
                                        .ifBlank { "Pago realizado" },
                                    color = AzulPagoFlex,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = formatoPesos.format(
                                        pago["monto"]?.toDoubleOrNull() ?: 0.0
                                    ),
                                    color = Color(0xFF247A3D),
                                    fontSize = 21.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = "Fecha: ${
                                        pago["fecha"].orEmpty()
                                            .ifBlank { "No informada" }
                                    }",
                                    color = GrisPagoFlex,
                                    fontSize = 12.sp
                                )

                                Text(
                                    text = "Estado: Pagado",
                                    color = Color(0xFF247A3D),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EstadoVacio(
    titulo: String,
    descripcion: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 28.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "✓",
            color = AzulPagoFlex,
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = titulo,
            color = AzulPagoFlex,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = descripcion,
            color = GrisPagoFlex,
            fontSize = 14.sp
        )
    }
}
