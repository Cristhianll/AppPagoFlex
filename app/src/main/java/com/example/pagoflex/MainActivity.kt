
package com.example.pagoflex

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.pagoflex.data.PagoFlexDbHelper
import com.example.pagoflex.ui.screens.DashboardScreen
import com.example.pagoflex.ui.screens.DetallePagoScreen
import com.example.pagoflex.ui.screens.DetallePerfilScreen
import com.example.pagoflex.ui.screens.FormularioScreen
import com.example.pagoflex.ui.screens.HistorialScreen
import com.example.pagoflex.ui.screens.PagoExitosoScreen
import com.example.pagoflex.ui.theme.PagoFlexTheme
import com.example.pagoflex.viewmodel.FormularioViewModel
import com.example.pagoflex.viewmodel.PagoViewModel
import java.text.NumberFormat
import java.util.Locale

class MainActivity : ComponentActivity() {

    private val viewModel: FormularioViewModel by viewModels()
    private val pagoViewModel: PagoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = PagoFlexDbHelper(applicationContext)

        setContent {
            PagoFlexTheme {
                val uiState by viewModel.uiState.collectAsState()
                val pagoUiState by pagoViewModel.uiState.collectAsState()

                var modo by remember { mutableStateOf("login") }
                var sesion by remember {
                    mutableStateOf<Map<String, String>?>(null)
                }
                var correo by remember { mutableStateOf("") }
                var clave by remember { mutableStateOf("") }
                var mensaje by remember { mutableStateOf("") }

                var pantallaActual by remember { mutableStateOf("inicio") }
                var seccionPerfilActual by remember { mutableStateOf("cuenta") }
                var verPerfilDetalle by remember { mutableStateOf(false) }

                LaunchedEffect(uiState.registroExitoso, modo) {
                    if (modo == "registro" && uiState.registroExitoso) {
                        val creado = db.registrarUsuario(
                            uiState.nombre,
                            uiState.correo,
                            uiState.contrasena
                        )

                        mensaje = if (creado) {
                            "Cuenta creada. Ahora inicia sesión."
                        } else {
                            "Ese correo ya está registrado. Usa otro correo."
                        }

                        viewModel.onLimpiar()
                        modo = "login"
                    }
                }

                when {
                    sesion == null && modo == "registro" -> {
                        Box(Modifier.fillMaxSize()) {
                            FormularioScreen(viewModel = viewModel)

                            TextButton(
                                onClick = {
                                    viewModel.onLimpiar()
                                    modo = "login"
                                },
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .padding(12.dp)
                            ) {
                                Text("← Volver al inicio")
                            }
                        }
                    }

                    sesion == null -> {
                        LoginScreen(
                            correo = correo,
                            clave = clave,
                            mensaje = mensaje,
                            onCorreo = { correo = it },
                            onClave = { clave = it },
                            onEntrar = {
                                val resultado = db.iniciarSesion(correo, clave)

                                if (resultado == null) {
                                    mensaje = "Correo o contraseña incorrectos."
                                } else {
                                    sesion = resultado
                                    mensaje = ""
                                    pantallaActual = "inicio"
                                }
                            },
                            onRegistro = {
                                mensaje = ""
                                viewModel.onLimpiar()
                                modo = "registro"
                            }
                        )
                    }

                    sesion?.get("rol") == "ADMIN" -> {
                        AdminScreen(
                            db = db,
                            admin = sesion!!,
                            onSalir = {
                                sesion = null
                                correo = ""
                                clave = ""
                                mensaje = ""
                                pantallaActual = "inicio"
                            }
                        )
                    }

                    else -> {
                        val usuario = sesion!!

                        if (verPerfilDetalle) {
                            DetallePerfilScreen(
                                nombreUsuario = usuario["nombre"].orEmpty(),
                                correoUsuario = usuario["correo"].orEmpty(),
                                ultimosDigitosVisa =
                                    pagoUiState.ultimosDigitosVisa,
                                tarjetaPrincipal =
                                    pagoUiState.tarjetaPrincipal,
                                seccionInicial = seccionPerfilActual,
                                onVolver = {
                                    verPerfilDetalle = false
                                },
                                onCerrarSesion = {
                                    verPerfilDetalle = false
                                    sesion = null
                                    viewModel.onLimpiar()
                                }
                            )
                        } else {
                            when (pantallaActual) {
                                "detallePago" -> {
                                    DetallePagoScreen(
                                        onVolverAtras = {
                                            pantallaActual = "inicio"
                                        },
                                        onConfirmarPago = {
                                            val usuarioId =
                                                usuario["id"]!!.toInt()

                                            val pendiente = db
                                                .listarDeudas(usuarioId)
                                                .firstOrNull {
                                                    it["estado"] == "PENDIENTE"
                                                }

                                            if (pendiente != null) {
                                                db.pagarDeuda(
                                                    pendiente["id"]!!.toInt(),
                                                    usuarioId
                                                )
                                            }

                                            pagoViewModel.confirmarPago()
                                            pantallaActual = "pagoExitoso"
                                        }
                                    )
                                }

                                "pagoExitoso" -> {
                                    PagoExitosoScreen(
                                        onVolverInicio = {
                                            pantallaActual = "inicio"
                                        },
                                        onVerHistorial = {
                                            pantallaActual = "historial"
                                        }
                                    )
                                }

                                "historial" -> {
                                    HistorialScreen(
                                        onVolverInicio = {
                                            pantallaActual = "inicio"
                                        },
                                        onIrAPagos = {
                                            pantallaActual = "detallePago"
                                        }
                                    )
                                }

                                else -> {
                                    val usuarioId =
                                        usuario["id"]!!.toInt()

                                    DashboardScreen(
                                        nombreUsuario =
                                            usuario["nombre"].orEmpty(),
                                        deudaPendiente = db
                                            .listarDeudas(usuarioId)
                                            .any {
                                                it["estado"] == "PENDIENTE"
                                            },
                                        onIrADetallePago = {
                                            pantallaActual = "detallePago"
                                        },
                                        onIrAPagos = {
                                            pantallaActual = "detallePago"
                                        },
                                        onIrAHistorial = {
                                            pantallaActual = "historial"
                                        },
                                        onSeleccionarOpcionPerfil = { opcion ->
                                            if (opcion == "salir") {
                                                sesion = null
                                                viewModel.onLimpiar()
                                            } else {
                                                seccionPerfilActual = opcion
                                                verPerfilDetalle = true
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
private fun LoginScreen(
    correo: String,
    clave: String,
    mensaje: String,
    onCorreo: (String) -> Unit,
    onClave: (String) -> Unit,
    onEntrar: () -> Unit,
    onRegistro: () -> Unit
) {
    var mostrarClave by remember { mutableStateOf(false) }
    var errorCorreo by remember { mutableStateOf<String?>(null) }
    var errorClave by remember { mutableStateOf<String?>(null) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = com.example.pagoflex.ui.theme.PrimarioOscuro
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                modifier = Modifier.size(72.dp),
                shape = RoundedCornerShape(20.dp),
                color = com.example.pagoflex.ui.theme.Acento
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        "P",
                        color = com.example.pagoflex.ui.theme.PrimarioOscuro,
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            Text(
                "PagoFlex",
                color = com.example.pagoflex.ui.theme.Blanco,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )

            Text(
                "Tus pagos, más simples",
                color = com.example.pagoflex.ui.theme.Blanco.copy(alpha = 0.8f),
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(Modifier.height(28.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = com.example.pagoflex.ui.theme.SuperficieClara
                ),
                elevation = CardDefaults.cardElevation(6.dp)
            ) {
                Column(
                    modifier = Modifier.padding(22.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        "Iniciar sesión",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = com.example.pagoflex.ui.theme.Primario
                    )

                    OutlinedTextField(
                        value = correo,
                        onValueChange = {
                            onCorreo(it)
                            errorCorreo = null
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Correo electrónico") },
                        singleLine = true,
                        isError = errorCorreo != null,
                        supportingText = {
                            errorCorreo?.let { Text(it) }
                        },
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            keyboardType = androidx.compose.ui.text.input.KeyboardType.Email,
                            imeAction = androidx.compose.ui.text.input.ImeAction.Next
                        )
                    )

                    OutlinedTextField(
                        value = clave,
                        onValueChange = {
                            onClave(it)
                            errorClave = null
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Contraseña") },
                        singleLine = true,
                        isError = errorClave != null,
                        supportingText = {
                            errorClave?.let { Text(it) }
                        },
                        visualTransformation = if (mostrarClave) {
                            androidx.compose.ui.text.input.VisualTransformation.None
                        } else {
                            PasswordVisualTransformation()
                        },
                        trailingIcon = {
                            TextButton(
                                onClick = { mostrarClave = !mostrarClave }
                            ) {
                                Text(
                                    if (mostrarClave) "Ocultar" else "Ver",
                                    color = com.example.pagoflex.ui.theme.Secundario
                                )
                            }
                        }
                    )

                    if (mensaje.isNotBlank()) {
                        Text(
                            mensaje,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Button(
                        onClick = {
                            val correoValido = android.util.Patterns.EMAIL_ADDRESS
                                .matcher(correo.trim())
                                .matches()

                            errorCorreo = if (correo.isBlank()) {
                                "Ingresa tu correo."
                            } else if (!correoValido) {
                                "Ingresa un correo válido."
                            } else null

                            errorClave = if (clave.isBlank()) {
                                "Ingresa tu contraseña."
                            } else null

                            if (errorCorreo == null && errorClave == null) {
                                onEntrar()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = com.example.pagoflex.ui.theme.Primario,
                            contentColor = com.example.pagoflex.ui.theme.Blanco
                        )
                    ) {
                        Text("Ingresar", fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = onRegistro,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = com.example.pagoflex.ui.theme.Primario
                        )
                    ) {
                        Text("Crear cuenta")
                    }
                }
            }

            Spacer(Modifier.height(18.dp))

            Text(
                "Accesos de demostración",
                color = com.example.pagoflex.ui.theme.Acento,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                "Admin: admin@pagoflex.cl",
                color = com.example.pagoflex.ui.theme.Blanco
            )
            Text(
                "Usuario: emilia@pagoflex.cl",
                color = com.example.pagoflex.ui.theme.Blanco
            )
        }
    }
}


@Composable
private fun AdminScreen(
    db: PagoFlexDbHelper,
    admin: Map<String, String>,
    onSalir: () -> Unit
) {
    var refrescar by remember { mutableIntStateOf(0) }
    var concepto by remember { mutableStateOf("") }
    var empresa by remember { mutableStateOf("") }
    var monto by remember { mutableStateOf("") }
    var vencimiento by remember { mutableStateOf("2026-12-31") }
    var usuarioId by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf("") }

    val usuarios = remember(refrescar) {
        db.listarUsuarios().filter { it["rol"] == "USUARIO" }
    }

    val deudas = remember(refrescar) {
        db.listarDeudas()
    }

    LaunchedEffect(usuarios) {
        if (usuarioId.isBlank() && usuarios.isNotEmpty()) {
            usuarioId = usuarios.first()["id"].orEmpty()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Panel administrador",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Text("Bienvenido, ${admin["nombre"].orEmpty()}")

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Crear deuda",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                OutlinedTextField(
                    value = concepto,
                    onValueChange = { concepto = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Concepto") },
                    singleLine = true
                )

                OutlinedTextField(
                    value = empresa,
                    onValueChange = { empresa = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Empresa / servicio") },
                    singleLine = true
                )

                OutlinedTextField(
                    value = monto,
                    onValueChange = { monto = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Monto en pesos") },
                    singleLine = true
                )

                OutlinedTextField(
                    value = vencimiento,
                    onValueChange = { vencimiento = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Vencimiento (AAAA-MM-DD)") },
                    singleLine = true
                )

                Text(
                    text = "Asignar a usuario",
                    fontWeight = FontWeight.SemiBold
                )

                usuarios.forEach { usuario ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = usuarioId == usuario["id"],
                            onClick = {
                                usuarioId = usuario["id"].orEmpty()
                            }
                        )

                        Text(
                            "${usuario["nombre"]} (${usuario["correo"]})"
                        )
                    }
                }

                if (mensaje.isNotBlank()) {
                    Text(
                        text = mensaje,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Button(
                    onClick = {
                        val valor = monto
                            .replace(".", "")
                            .replace(",", ".")
                            .toDoubleOrNull()

                        mensaje = when {
                            concepto.isBlank() || empresa.isBlank() ->
                                "Completa concepto y empresa."

                            valor == null || valor <= 0 ->
                                "Ingresa un monto válido."

                            !Regex("\\d{4}-\\d{2}-\\d{2}")
                                .matches(vencimiento) ->
                                "Usa fecha AAAA-MM-DD."

                            usuarioId.isBlank() ->
                                "Registra un usuario antes de crear deudas."

                            db.crearDeuda(
                                usuarioId.toInt(),
                                concepto,
                                empresa,
                                valor,
                                vencimiento
                            ) -> {
                                concepto = ""
                                empresa = ""
                                monto = ""
                                refrescar++
                                "Deuda creada correctamente."
                            }

                            else -> "No se pudo crear la deuda."
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Guardar deuda")
                }
            }
        }

        Text(
            text = "Deudas registradas",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        AnimatedVisibility(visible = deudas.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                deudas.forEach { deuda ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(14.dp)) {
                            Text(
                                text = deuda["concepto"].orEmpty(),
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                "Usuario: ${deuda["usuario"].orEmpty()} · " +
                                        deuda["empresa"].orEmpty()
                            )

                            val formato = NumberFormat.getCurrencyInstance(
                                Locale("es", "CL")
                            )

                            val montoDeuda =
                                deuda["monto"]?.toDoubleOrNull() ?: 0.0

                            Text(
                                "${formato.format(montoDeuda)} · " +
                                        "Vence ${deuda["vencimiento"].orEmpty()}"
                            )

                            Text("Estado: ${deuda["estado"].orEmpty()}")
                        }
                    }
                }
            }
        }

        Button(
            onClick = onSalir,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cerrar sesión")
        }
    }
}
