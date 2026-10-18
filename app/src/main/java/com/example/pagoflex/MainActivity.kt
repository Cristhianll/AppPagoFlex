package com.example.pagoflex

import android.os.Bundle
import android.util.Patterns
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.pagoflex.data.PagoFlexRepository
import com.example.pagoflex.ui.screens.AdminScreen
import com.example.pagoflex.ui.screens.DashboardScreen
import com.example.pagoflex.ui.screens.DetallePagoScreen
import com.example.pagoflex.ui.screens.DetallePerfilScreen
import com.example.pagoflex.ui.screens.FormularioScreen
import com.example.pagoflex.ui.screens.PagosScreen
import com.example.pagoflex.ui.screens.ResumenHistorialScreen
import com.example.pagoflex.ui.theme.Acento
import com.example.pagoflex.ui.theme.Blanco
import com.example.pagoflex.ui.theme.PagoFlexTheme
import com.example.pagoflex.ui.theme.PrimarioOscuro
import com.example.pagoflex.viewmodel.FormularioViewModel
import com.example.pagoflex.viewmodel.PagoViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.NumberFormat
import java.util.Calendar
import java.util.Locale
import com.example.pagoflex.ui.theme.LocalControlTemaPagoFlex

class MainActivity : ComponentActivity() {

    private val formularioViewModel: FormularioViewModel by viewModels()
    private val pagoViewModel: PagoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repository = PagoFlexRepository(applicationContext)

        setContent {
            PagoFlexTheme {
                PagoFlexApp(
                    repository = repository,
                    formularioViewModel = formularioViewModel,
                    pagoViewModel = pagoViewModel
                )
            }
        }
    }
}

private fun tarjetaDemoVigente(vencimiento: String): Boolean {
    if (!vencimiento.matches(Regex("[0-9]{2}/[0-9]{2}"))) {
        return false
    }

    val mes = vencimiento.take(2).toIntOrNull() ?: return false
    val anioCorto = vencimiento.takeLast(2).toIntOrNull() ?: return false
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
private fun PagoFlexApp(
    repository: PagoFlexRepository,
    formularioViewModel: FormularioViewModel,
    pagoViewModel: PagoViewModel
) {
    val scope = rememberCoroutineScope()
    val colores = MaterialTheme.colorScheme

    val formularioState by formularioViewModel.uiState.collectAsState()
    val pagoState by pagoViewModel.uiState.collectAsState()

    var sesion by remember {
        mutableStateOf<Map<String, String>?>(null)
    }

    var modo by remember { mutableStateOf("login") }
    var pantalla by remember { mutableStateOf("inicio") }

    var correo by remember { mutableStateOf("") }
    var clave by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf("") }

    var ocupado by remember { mutableStateOf(false) }
    var cargandoDatos by remember { mutableStateOf(false) }

    var deudas by remember {
        mutableStateOf<List<Map<String, String>>>(emptyList())
    }

    var pagos by remember {
        mutableStateOf<List<Map<String, String>>>(emptyList())
    }

    var deudaSeleccionadaId by remember {
        mutableStateOf<String?>(null)
    }

    var ultimoPago by remember {
        mutableStateOf<Map<String, String>?>(null)
    }

    var verPerfil by remember { mutableStateOf(false) }
    var seccionPerfil by remember { mutableStateOf("cuenta") }

    val usuarioId = sesion?.get("id")?.toIntOrNull()
    val rol = sesion?.get("rol")

    suspend fun cargarDatos(id: Int) {
        val resultado = withContext(Dispatchers.IO) {
            repository.listarDeudas(id) to repository.listarPagos(id)
        }

        if (sesion?.get("id")?.toIntOrNull() == id) {
            deudas = resultado.first
            pagos = resultado.second
        }
    }

    fun cerrarSesion() {
        pagoViewModel.limpiarSesion()
        formularioViewModel.onLimpiar()

        sesion = null
        modo = "login"
        pantalla = "inicio"

        correo = ""
        clave = ""
        mensaje = ""

        deudas = emptyList()
        pagos = emptyList()

        deudaSeleccionadaId = null
        ultimoPago = null

        verPerfil = false
        seccionPerfil = "cuenta"
    }

    fun actualizarDatos() {
        val id = usuarioId ?: return

        if (cargandoDatos || ocupado) {
            return
        }

        scope.launch {
            cargandoDatos = true

            try {
                cargarDatos(id)
            } catch (_: Exception) {
                mensaje = "No se pudo actualizar la información."
            } finally {
                cargandoDatos = false
            }
        }
    }

    fun irAInicio() {
        pantalla = "inicio"
        deudaSeleccionadaId = null
    }

    fun irAPagos() {
        pantalla = "pagos"
        deudaSeleccionadaId = null
        actualizarDatos()
    }

    fun irAHistorial() {
        pantalla = "historial"
        deudaSeleccionadaId = null
        actualizarDatos()
    }

    fun seleccionarDeuda(id: String) {
        deudaSeleccionadaId = id
        mensaje = ""
        pantalla = "detallePago"
    }

    fun confirmarPagoSeleccionado(deuda: Map<String, String>) {
        if (ocupado || cargandoDatos) {
            return
        }

        val idUsuario = usuarioId
        val idDeuda = deuda["id"]?.toIntOrNull()

        if (idUsuario == null || idDeuda == null) {
            mensaje = "No se pudo identificar al usuario o la deuda."
            return
        }

        val tarjetaValida =
            pagoState.tarjetaVisaRegistrada &&
                    pagoState.ultimosDigitosVisa.matches(
                        Regex("[0-9]{4}")
                    ) &&
                    tarjetaDemoVigente(
                        pagoState.vencimientoTarjeta
                    )

        if (!tarjetaValida) {
            mensaje = "Vincula una tarjeta de prueba vigente para continuar."
            seccionPerfil = "tarjetas"
            verPerfil = true
            return
        }

        scope.launch {
            ocupado = true

            try {
                val pagada = withContext(Dispatchers.IO) {
                    repository.pagarDeuda(
                        deudaId = idDeuda,
                        usuarioId = idUsuario
                    )
                }

                if (pagada) {
                    ultimoPago = mapOf(
                        "concepto" to deuda["concepto"].orEmpty(),
                        "empresa" to deuda["empresa"].orEmpty(),
                        "monto" to deuda["monto"].orEmpty()
                    )

                    deudas = deudas.map { actual ->
                        if (actual["id"] == idDeuda.toString()) {
                            actual + ("estado" to "PAGADA")
                        } else {
                            actual
                        }
                    }

                    pagoViewModel.confirmarPago()

                    deudaSeleccionadaId = null
                    pantalla = "pagoExitoso"
                    mensaje = ""

                    try {
                        cargarDatos(idUsuario)

                        pagos.firstOrNull {
                            it["deudaId"] == idDeuda.toString()
                        }?.let {
                            ultimoPago = it
                        }
                    } catch (_: Exception) {
                        mensaje = "El pago se registró, pero no se pudo " +
                                "actualizar el historial."
                    }
                } else {
                    mensaje = "No se pudo registrar el pago. " +
                            "Actualiza las deudas."

                    pantalla = "pagos"
                    deudaSeleccionadaId = null

                    try {
                        cargarDatos(idUsuario)
                    } catch (_: Exception) {
                        mensaje += " Tampoco se pudo actualizar el listado."
                    }
                }
            } catch (_: Exception) {
                mensaje = "No se pudo completar la operación. " +
                        "Revisa el historial antes de reintentar."
            } finally {
                ocupado = false
            }
        }
    }

    LaunchedEffect(usuarioId, rol) {
        val id = usuarioId

        if (id != null && rol == "USUARIO") {
            cargandoDatos = true

            try {
                cargarDatos(id)
            } catch (_: Exception) {
                mensaje = "No se pudieron cargar tus deudas y pagos."
            } finally {
                cargandoDatos = false
            }
        }
    }

    LaunchedEffect(
        formularioState.registroExitoso,
        modo
    ) {
        if (
            modo == "registro" &&
            formularioState.registroExitoso
        ) {
            ocupado = true

            try {
                val nombreRegistrar = formularioState.nombre
                val correoRegistrar = formularioState.correo
                val claveRegistrar = formularioState.contrasena

                val creado = withContext(Dispatchers.IO) {
                    repository.registrarUsuario(
                        nombreRegistrar,
                        correoRegistrar,
                        claveRegistrar
                    )
                }

                mensaje = if (creado) {
                    correo = correoRegistrar
                    "Cuenta creada. Ahora inicia sesión."
                } else {
                    "No se pudo crear la cuenta. " +
                            "Revisa los datos o utiliza otro correo."
                }
            } catch (_: Exception) {
                mensaje = "Ocurrió un error al registrar la cuenta."
            } finally {
                formularioViewModel.onLimpiar()
                modo = "login"
                ocupado = false
            }
        }
    }

    val pendientes = deudas.filter {
        it["estado"] == "PENDIENTE"
    }

    val deudaSeleccionada = pendientes.firstOrNull {
        it["id"] == deudaSeleccionadaId
    }

    BackHandler(
        enabled = sesion != null &&
                rol != "ADMIN" &&
                !ocupado &&
                (verPerfil || pantalla != "inicio")
    ) {
        when {
            verPerfil -> {
                verPerfil = false
            }

            pantalla == "detallePago" -> {
                pantalla = "pagos"
                deudaSeleccionadaId = null
            }

            else -> {
                irAInicio()
            }
        }
    }

    BackHandler(
        enabled = ocupado && pantalla == "detallePago"
    ) {
        // Espera a que termine el registro del pago.
    }

    when {
        sesion == null && modo == "registro" -> {
            BackHandler(enabled = !ocupado) {
                formularioViewModel.onLimpiar()
                modo = "login"
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(colores.background)
                    .statusBarsPadding()
            ) {
                TextButton(
                    onClick = {
                        formularioViewModel.onLimpiar()
                        modo = "login"
                    },
                    enabled = !ocupado
                ) {
                    Text("← Volver al inicio de sesión")
                }

                if (ocupado) {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Box(
                    modifier = Modifier.weight(1f)
                ) {
                    FormularioScreen(
                        viewModel = formularioViewModel
                    )

                    if (ocupado) {
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = colores.background.copy(alpha = 0.95f)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment =
                                        Alignment.CenterHorizontally
                                ) {
                                    CircularProgressIndicator()

                                    Spacer(Modifier.height(12.dp))

                                    Text("Creando cuenta...")
                                }
                            }
                        }
                    }
                }
            }
        }

        sesion == null -> {
            LoginPagoFlex(
                correo = correo,
                clave = clave,
                mensaje = mensaje,
                ocupado = ocupado,
                onCorreo = {
                    correo = it
                },
                onClave = {
                    clave = it
                },
                onRegistro = {
                    mensaje = ""
                    formularioViewModel.onLimpiar()
                    modo = "registro"
                },
                onEntrar = {
                    if (!ocupado) {
                        val correoIngresado = correo.trim()
                        val claveIngresada = clave

                        scope.launch {
                            ocupado = true

                            try {
                                val resultado = withContext(Dispatchers.IO) {
                                    repository.iniciarSesion(
                                        correoIngresado,
                                        claveIngresada
                                    )
                                }

                                val idSesion = resultado
                                    ?.get("id")
                                    ?.toIntOrNull()

                                if (
                                    resultado == null ||
                                    idSesion == null
                                ) {
                                    mensaje = "Correo o contraseña incorrectos."
                                } else {
                                    pagoViewModel.cargarUsuario(idSesion)

                                    deudas = emptyList()
                                    pagos = emptyList()

                                    deudaSeleccionadaId = null
                                    ultimoPago = null

                                    verPerfil = false
                                    seccionPerfil = "cuenta"

                                    pantalla = "inicio"
                                    mensaje = ""
                                    clave = ""

                                    sesion = resultado
                                }
                            } catch (_: Exception) {
                                mensaje = "No se pudo iniciar sesión."
                            } finally {
                                ocupado = false
                            }
                        }
                    }
                }
            )
        }

        rol == "ADMIN" -> {
            AdminScreen(
                repository = repository,
                usuario = sesion.orEmpty(),
                onCerrarSesion = {
                    cerrarSesion()
                }
            )
        }

        else -> {
            val usuario = sesion.orEmpty()

            if (verPerfil) {
                DetallePerfilScreen(
                    usuarioId = usuarioId ?: 0,
                    nombreUsuario = usuario["nombre"].orEmpty(),
                    correoUsuario = usuario["correo"].orEmpty(),
                    ultimosDigitosVisa = pagoState.ultimosDigitosVisa,
                    tarjetaPrincipal = pagoState.tarjetaPrincipal,
                    titularTarjeta = pagoState.titularTarjeta,
                    vencimientoTarjeta = pagoState.vencimientoTarjeta,
                    notificacionesActivas = pagoState.notificacionesActivas,
                    seccionInicial = seccionPerfil,
                    onVolver = {
                        verPerfil = false
                    },
                    onCerrarSesion = {
                        cerrarSesion()
                    },
                    onVincularTarjeta = { digitos, titular, vencimiento ->
                        pagoViewModel.vincularTarjeta(
                            ultimosDigitos = digitos,
                            titular = titular,
                            vencimiento = vencimiento
                        )
                    },
                    onAlternarNotificaciones = {
                        pagoViewModel.alternarNotificaciones()
                    }
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(colores.background)
                ) {
                    if (mensaje.isNotBlank()) {
                        Surface(
                            color = colores.errorContainer,
                            contentColor = colores.onErrorContainer
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .statusBarsPadding()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = mensaje,
                                    modifier = Modifier.weight(1f)
                                )

                                TextButton(
                                    onClick = {
                                        mensaje = ""
                                    },
                                    colors = ButtonDefaults.textButtonColors(
                                        contentColor = colores.onErrorContainer
                                    )
                                ) {
                                    Text("Cerrar")
                                }
                            }
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        when (pantalla) {
                            "pagos" -> {
                                PagosScreen(
                                    deudasPendientes = pendientes,
                                    cargando = cargandoDatos,
                                    onPagarDeuda = {
                                        seleccionarDeuda(it)
                                    },
                                    onIrAInicio = {
                                        irAInicio()
                                    },
                                    onIrAHistorial = {
                                        irAHistorial()
                                    },
                                    onActualizar = {
                                        actualizarDatos()
                                    }
                                )
                            }

                            "detallePago" -> {
                                val deuda = deudaSeleccionada

                                if (deuda == null) {
                                    PantallaInformativaPago(
                                        titulo = "Deuda no disponible",
                                        texto = "Regresa a Pagos y actualiza tus deudas.",
                                        textoBotonVolver = "Volver a Pagos",
                                        onVolver = {
                                            irAPagos()
                                        }
                                    )
                                } else if (ocupado) {
                                    Surface(
                                        modifier = Modifier.fillMaxSize(),
                                        color = colores.background,
                                        contentColor = colores.onBackground
                                    ) {
                                        Box(
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Column(
                                                horizontalAlignment =
                                                    Alignment.CenterHorizontally
                                            ) {
                                                CircularProgressIndicator()

                                                Spacer(
                                                    Modifier.height(16.dp)
                                                )

                                                Text("Registrando pago...")
                                            }
                                        }
                                    }
                                } else {
                                    DetallePagoScreen(
                                        empresa = deuda["empresa"].orEmpty(),
                                        concepto = deuda["concepto"].orEmpty(),
                                        monto = deuda["monto"]
                                            ?.toDoubleOrNull() ?: 0.0,
                                        ultimosDigitosTarjeta =
                                            pagoState.ultimosDigitosVisa,
                                        titularTarjeta =
                                            pagoState.titularTarjeta,
                                        onIrAVincularTarjeta = {
                                            mensaje = ""
                                            seccionPerfil = "tarjetas"
                                            verPerfil = true
                                        },
                                        onVolverAtras = {
                                            pantalla = "pagos"
                                            deudaSeleccionadaId = null
                                        },
                                        onConfirmarPago = {
                                            confirmarPagoSeleccionado(deuda)
                                        }
                                    )
                                }
                            }

                            "pagoExitoso" -> {
                                val formato = NumberFormat
                                    .getCurrencyInstance(
                                        Locale("es", "CL")
                                    )

                                val montoPagado = ultimoPago
                                    ?.get("monto")
                                    ?.toDoubleOrNull() ?: 0.0

                                PantallaInformativaPago(
                                    titulo = "Pago registrado",
                                    texto = buildString {
                                        append(
                                            ultimoPago
                                                ?.get("empresa")
                                                .orEmpty()
                                        )

                                        append("\n")

                                        append(
                                            ultimoPago
                                                ?.get("concepto")
                                                .orEmpty()
                                        )

                                        append("\n")
                                        append(
                                            formato.format(montoPagado)
                                        )

                                        val comprobante = ultimoPago
                                            ?.get("comprobante")

                                        if (!comprobante.isNullOrBlank()) {
                                            append("\nReferencia: ")
                                            append(comprobante)
                                        }

                                        append(
                                            "\n\nRegistro de demostración. " +
                                                    "No se realiza un cargo bancario."
                                        )
                                    },
                                    onVolver = {
                                        irAInicio()
                                    },
                                    onVerHistorial = {
                                        irAHistorial()
                                    }
                                )
                            }

                            "historial" -> {
                                ResumenHistorialScreen(
                                    pagos = pagos,
                                    onVolverInicio = {
                                        irAInicio()
                                    },
                                    onIrAPagos = {
                                        irAPagos()
                                    }
                                )
                            }

                            else -> {
                                DashboardScreen(
                                    nombreUsuario = usuario["nombre"].orEmpty(),
                                    deudasPendientes = pendientes,
                                    cargando = cargandoDatos,
                                    onActualizar = {
                                        actualizarDatos()
                                    },
                                    onPagarDeuda = {
                                        seleccionarDeuda(it)
                                    },
                                    onIrAPagos = {
                                        irAPagos()
                                    },
                                    onIrAHistorial = {
                                        irAHistorial()
                                    },
                                    onSeleccionarOpcionPerfil = { opcion ->
                                        if (opcion == "salir") {
                                            cerrarSesion()
                                        } else {
                                            mensaje = ""
                                            seccionPerfil = opcion
                                            verPerfil = true
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

@Composable
private fun LoginPagoFlex(
    correo: String,
    clave: String,
    mensaje: String,
    ocupado: Boolean,
    onCorreo: (String) -> Unit,
    onClave: (String) -> Unit,
    onEntrar: () -> Unit,
    onRegistro: () -> Unit
) {
    val colores = MaterialTheme.colorScheme
    val tema = LocalControlTemaPagoFlex.current

    val fondoLogin = if (tema.oscuro) {
        colores.background
    } else {
        PrimarioOscuro
    }

    var mostrarClave by remember {
        mutableStateOf(false)
    }

    var errorLocal by remember {
        mutableStateOf("")
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = fondoLogin,
        contentColor = Blanco
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                modifier = Modifier.size(64.dp),
                shape = RoundedCornerShape(16.dp),
                color = Acento,
                contentColor = PrimarioOscuro
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "P",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            Text(
                text = "PagoFlex",
                color = Blanco,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Tus pagos, más simples",
                color = Blanco.copy(alpha = 0.85f)
            )

            Spacer(Modifier.height(24.dp))

            // El formulario sí cambia según el tema elegido.
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = colores.surface,
                    contentColor = colores.onSurface
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "Iniciar sesión",
                        color = colores.onSurface,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    OutlinedTextField(
                        value = correo,
                        onValueChange = {
                            errorLocal = ""
                            onCorreo(it)
                        },
                        label = {
                            Text("Correo electrónico")
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email
                        ),
                        singleLine = true,
                        enabled = !ocupado,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = clave,
                        onValueChange = {
                            errorLocal = ""
                            onClave(it)
                        },
                        label = {
                            Text("Contraseña")
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password
                        ),
                        singleLine = true,
                        enabled = !ocupado,
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = if (mostrarClave) {
                            VisualTransformation.None
                        } else {
                            PasswordVisualTransformation()
                        },
                        trailingIcon = {
                            TextButton(
                                onClick = {
                                    mostrarClave = !mostrarClave
                                },
                                enabled = !ocupado
                            ) {
                                Text(
                                    text = if (mostrarClave) {
                                        "Ocultar"
                                    } else {
                                        "Ver"
                                    }
                                )
                            }
                        }
                    )

                    if (errorLocal.isNotBlank()) {
                        Text(
                            text = errorLocal,
                            color = colores.error
                        )
                    }

                    if (mensaje.isNotBlank()) {
                        Text(
                            text = mensaje,
                            color = colores.onSurfaceVariant
                        )
                    }

                    Button(
                        enabled = !ocupado,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colores.secondary,
                            contentColor = colores.onSecondary
                        ),
                        onClick = {
                            errorLocal = when {
                                !Patterns.EMAIL_ADDRESS
                                    .matcher(correo.trim())
                                    .matches() -> {
                                    "Ingresa un correo válido."
                                }

                                clave.isBlank() -> {
                                    "Ingresa tu contraseña."
                                }

                                else -> ""
                            }

                            if (errorLocal.isBlank()) {
                                onEntrar()
                            }
                        }
                    ) {
                        Text(
                            text = if (ocupado) {
                                "Ingresando..."
                            } else {
                                "Ingresar"
                            },
                            fontWeight = FontWeight.Bold
                        )
                    }

                    OutlinedButton(
                        onClick = onRegistro,
                        enabled = !ocupado,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Crear cuenta")
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            Text(
                text = "Accesos de demostración",
                color = Acento,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = "Admin: admin@pagoflex.cl",
                color = Blanco.copy(alpha = 0.85f)
            )

            Text(
                text = "Usuario: emilia@pagoflex.cl",
                color = Blanco.copy(alpha = 0.85f)
            )
        }
    }
}

@Composable
private fun PantallaInformativaPago(
    titulo: String,
    texto: String,
    onVolver: () -> Unit,
    textoBotonVolver: String = "Volver al inicio",
    onVerHistorial: (() -> Unit)? = null
) {
    val colores = MaterialTheme.colorScheme

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
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = titulo,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(20.dp))

            Text(
                text = texto,
                color = colores.onSurfaceVariant
            )

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = onVolver,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colores.secondary,
                    contentColor = colores.onSecondary
                )
            ) {
                Text(textoBotonVolver)
            }

            onVerHistorial?.let { accion ->
                OutlinedButton(
                    onClick = accion,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Ver historial")
                }
            }
        }
    }
}