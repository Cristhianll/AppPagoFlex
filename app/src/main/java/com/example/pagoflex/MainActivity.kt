package com.example.pagoflex

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.example.pagoflex.ui.screens.ResumenHistorialScreen
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
                var refrescarDatos by remember { mutableIntStateOf(0) }
                var deudaSeleccionadaId by remember { mutableStateOf<String?>(null) }

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
                                    refrescarDatos++
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
                        val usuarioId = usuario["id"]!!.toInt()

                        val deudasUsuario = remember(usuarioId, refrescarDatos) {
                            db.listarDeudas(usuarioId)
                        }
                        val pagosUsuario = remember(usuarioId, refrescarDatos) {
                            db.listarPagos(usuarioId)
                        }
                        val deudasPendientes = deudasUsuario.filter {
                            it["estado"] == "PENDIENTE"
                        }
                        val deudaPendiente = deudasPendientes.firstOrNull {
                            it["id"] == deudaSeleccionadaId
                        } ?: deudasPendientes.firstOrNull()

                        if (verPerfilDetalle) {
                            DetallePerfilScreen(
                                nombreUsuario = usuario["nombre"].orEmpty(),
                                correoUsuario = usuario["correo"].orEmpty(),
                                ultimosDigitosVisa = pagoUiState.ultimosDigitosVisa,
                                tarjetaPrincipal = pagoUiState.tarjetaPrincipal,
                                notificacionesActivas = pagoUiState.notificacionesActivas,
                                modoOscuroAutomatico = pagoUiState.modoOscuroAutomatico,
                                biometriaActiva = pagoUiState.biometriaActiva,
                                seccionInicial = seccionPerfilActual,
                                onVolver = {
                                    verPerfilDetalle = false
                                },
                                onCerrarSesion = {
                                    verPerfilDetalle = false
                                    sesion = null
                                    viewModel.onLimpiar()
                                },
                                onVincularTarjeta = { digitos ->
                                    pagoViewModel.vincularTarjeta(digitos)
                                },
                                onAlternarNotificaciones = {
                                    pagoViewModel.alternarNotificaciones()
                                },
                                onAlternarModoOscuro = {
                                    pagoViewModel.alternarModoOscuro()
                                },
                                onAlternarBiometria = {
                                    pagoViewModel.alternarBiometria()
                                }
                            )
                        } else {
                            when (pantallaActual) {
                                "detallePago" -> {
                                    if (deudaPendiente == null) {
                                        LaunchedEffect(Unit) {
                                            pantallaActual = "movimientos"
                                        }
                                    } else {
                                        DetallePagoScreen(
                                            onVolverAtras = {
                                                deudaSeleccionadaId = null
                                                pantallaActual = "inicio"
                                            },
                                            onConfirmarPago = {
                                                val ok = db.pagarDeuda(
                                                    deudaPendiente["id"]!!.toInt(),
                                                    usuarioId
                                                )
                                                if (ok) {
                                                    pagoViewModel.confirmarPago()
                                                    deudaSeleccionadaId = null
                                                    refrescarDatos++
                                                    pantallaActual = "pagoExitoso"
                                                }
                                            },
                                            empresa = deudaPendiente["empresa"].orEmpty(),
                                            concepto = deudaPendiente["concepto"].orEmpty(),
                                            monto = deudaPendiente["monto"]
                                                ?.toDoubleOrNull() ?: 0.0
                                        )
                                    }
                                }

                                "pagoExitoso" -> {
                                    val ultimoPago = pagosUsuario.firstOrNull()
                                    PagoExitosoScreen(
                                        onVolverInicio = {
                                            pantallaActual = "inicio"
                                        },
                                        onVerHistorial = {
                                            pantallaActual = "movimientos"
                                        },
                                        concepto = ultimoPago?.get("concepto").orEmpty(),
                                        empresa = "",
                                        monto = ultimoPago?.get("monto")
                                            ?.toDoubleOrNull() ?: 0.0,
                                        comprobante = ultimoPago?.get("comprobante").orEmpty()
                                    )
                                }

                                "movimientos" -> {
                                    val digitos = pagoUiState.ultimosDigitosVisa
                                    val metodo = if (digitos.isNotBlank()) {
                                        "Visa •••• $digitos"
                                    } else {
                                        "Visa •••• 4582"
                                    }
                                    HistorialScreen(
                                        onVolverInicio = {
                                            pantallaActual = "inicio"
                                        },
                                        onIrAPagos = { },
                                        onIrAHistorial = {
                                            pantallaActual = "historial"
                                        },
                                        pagos = pagosUsuario,
                                        metodoPago = metodo
                                    )
                                }

                                "historial" -> {
                                    ResumenHistorialScreen(
                                        onVolverInicio = {
                                            pantallaActual = "inicio"
                                        },
                                        onIrAPagos = {
                                            pantallaActual = "movimientos"
                                        },
                                        pagos = pagosUsuario
                                    )
                                }

                                else -> {
                                    DashboardScreen(
                                        nombreUsuario = usuario["nombre"].orEmpty(),
                                        deudasPendientes = deudasPendientes,
                                        onPagarDeuda = { idDeuda ->
                                            deudaSeleccionadaId = idDeuda
                                            pantallaActual = "detallePago"
                                        },
                                        onIrAPagos = {
                                            pantallaActual = "movimientos"
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

    val azulFondo = Color(0xFF001F3F)
    val verdeAccento = Color(0xFFD4ED91)
    val textoSuave = Color(0xFFE2E8F0)
    val textoOscuro = Color(0xFF0F172A)
    val grisBorde = Color(0xFF94A3B8)

    val coloresCampo = OutlinedTextFieldDefaults.colors(
        focusedTextColor = textoOscuro,
        unfocusedTextColor = textoOscuro,
        focusedContainerColor = Color.White,
        unfocusedContainerColor = Color.White,
        focusedBorderColor = azulFondo,
        unfocusedBorderColor = grisBorde,
        focusedLabelColor = azulFondo,
        unfocusedLabelColor = Color(0xFF64748B),
        cursorColor = azulFondo
    )

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = azulFondo
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                shape = RoundedCornerShape(26.dp),
                color = verdeAccento,
                modifier = Modifier.size(88.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "$",
                        style = MaterialTheme.typography.displayLarge,
                        color = azulFondo,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            Text(
                text = "PagoFlex",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Text(
                text = "Tus pagos, más simples",
                style = MaterialTheme.typography.bodyLarge,
                color = textoSuave
            )

            Spacer(Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "Iniciar sesión",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = azulFondo
                    )

                    OutlinedTextField(
                        value = correo,
                        onValueChange = onCorreo,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Correo electrónico") },
                        singleLine = true,
                        colors = coloresCampo
                    )

                    OutlinedTextField(
                        value = clave,
                        onValueChange = onClave,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Contraseña") },
                        singleLine = true,
                        colors = coloresCampo,
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
                                    color = azulFondo
                                )
                            }
                        }
                    )

                    if (mensaje.isNotBlank()) {
                        Text(
                            text = mensaje,
                            color = if (mensaje.contains("creada", ignoreCase = true)) {
                                Color(0xFF247A3D)
                            } else {
                                Color(0xFFB42318)
                            },
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Button(
                        onClick = onEntrar,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = verdeAccento,
                            contentColor = azulFondo
                        )
                    ) {
                        Text("Ingresar", fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = onRegistro,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = azulFondo
                        )
                    ) {
                        Text("Crear cuenta", fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(Modifier.height(18.dp))

            Text(
                text = "Acceso de demostración",
                fontWeight = FontWeight.SemiBold,
                color = verdeAccento
            )
            Text(
                "Admin: admin@pagoflex.cl / Admin123$",
                color = textoSuave
            )
            Text(
                text = "Los usuarios se crean con \"Crear cuenta\"",
                style = MaterialTheme.typography.bodySmall,
                color = textoSuave
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

    val azulFondo = Color(0xFF001F3F)
    val verdeAccento = Color(0xFFD4ED91)
    val textoOscuro = Color(0xFF0F172A)
    val grisBorde = Color(0xFF94A3B8)
    val fondoClaro = Color(0xFFF1F5F9)

    val coloresCampo = OutlinedTextFieldDefaults.colors(
        focusedTextColor = textoOscuro,
        unfocusedTextColor = textoOscuro,
        focusedContainerColor = Color.White,
        unfocusedContainerColor = Color.White,
        focusedBorderColor = azulFondo,
        unfocusedBorderColor = grisBorde,
        focusedLabelColor = azulFondo,
        unfocusedLabelColor = Color(0xFF64748B),
        cursorColor = azulFondo
    )

    val usuarios = remember(refrescar) {
        db.listarUsuarios()
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
            .background(azulFondo)
            .verticalScroll(rememberScrollState())
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "Panel administrador",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "Bienvenido, ${admin["nombre"].orEmpty()}",
                color = verdeAccento
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    fondoClaro,
                    RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
                )
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Crear deuda",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = azulFondo
                    )

                    OutlinedTextField(
                        value = concepto,
                        onValueChange = { concepto = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Concepto") },
                        singleLine = true,
                        colors = coloresCampo
                    )

                    OutlinedTextField(
                        value = empresa,
                        onValueChange = { empresa = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Empresa / servicio") },
                        singleLine = true,
                        colors = coloresCampo
                    )

                    OutlinedTextField(
                        value = monto,
                        onValueChange = { monto = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Monto en pesos") },
                        singleLine = true,
                        colors = coloresCampo
                    )

                    OutlinedTextField(
                        value = vencimiento,
                        onValueChange = { vencimiento = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Vencimiento (AAAA-MM-DD)") },
                        singleLine = true,
                        colors = coloresCampo
                    )

                    Text(
                        text = "Asignar a usuario",
                        fontWeight = FontWeight.SemiBold,
                        color = azulFondo
                    )

                    if (usuarios.isEmpty()) {
                        Text(
                            text = "No hay usuarios. Crea una cuenta desde el login.",
                            color = Color(0xFFB42318)
                        )
                    } else {
                        usuarios.forEach { usuario ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                RadioButton(
                                    selected = usuarioId == usuario["id"],
                                    onClick = {
                                        usuarioId = usuario["id"].orEmpty()
                                    },
                                    colors = RadioButtonDefaults.colors(
                                        selectedColor = azulFondo
                                    )
                                )
                                Text(
                                    "${usuario["nombre"]} (${usuario["correo"]})",
                                    color = textoOscuro
                                )
                            }
                        }
                    }

                    if (mensaje.isNotBlank()) {
                        Text(
                            text = mensaje,
                            color = if (mensaje.contains("correctamente")) {
                                Color(0xFF247A3D)
                            } else {
                                Color(0xFFB42318)
                            }
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
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = verdeAccento,
                            contentColor = azulFondo
                        )
                    ) {
                        Text("Guardar deuda", fontWeight = FontWeight.Bold)
                    }
                }
            }

            Text(
                text = "Deudas registradas",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = azulFondo
            )

            AnimatedVisibility(visible = deudas.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    deudas.forEach { deuda ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(Modifier.padding(14.dp)) {
                                Text(
                                    text = deuda["concepto"].orEmpty(),
                                    fontWeight = FontWeight.Bold,
                                    color = azulFondo
                                )
                                Text(
                                    "Usuario: ${deuda["nombreUsuario"].orEmpty()} · " +
                                            deuda["empresa"].orEmpty(),
                                    color = Color(0xFF64748B)
                                )

                                val formato = NumberFormat.getCurrencyInstance(
                                    Locale("es", "CL")
                                )
                                val montoDeuda =
                                    deuda["monto"]?.toDoubleOrNull() ?: 0.0

                                Text(
                                    "${formato.format(montoDeuda)} · " +
                                            "Vence ${deuda["vencimiento"].orEmpty()}",
                                    color = textoOscuro
                                )
                                Text(
                                    "Estado: ${deuda["estado"].orEmpty()}",
                                    color = if (deuda["estado"] == "PAGADA") {
                                        Color(0xFF247A3D)
                                    } else {
                                        Color(0xFFB45309)
                                    },
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }

            if (deudas.isEmpty()) {
                Text(
                    "Aún no hay deudas creadas.",
                    color = Color(0xFF64748B)
                )
            }

            Button(
                onClick = onSalir,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFEF4444),
                    contentColor = Color.White
                )
            ) {
                Text("Cerrar sesión", fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}