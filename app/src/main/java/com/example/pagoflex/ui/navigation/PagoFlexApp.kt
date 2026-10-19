package com.example.pagoflex.ui.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.pagoflex.data.PagoFlexRepository
import com.example.pagoflex.data.ValidacionesTarjeta
import com.example.pagoflex.ui.components.CargaPagoFlex
import com.example.pagoflex.ui.components.MensajePagoFlex
import com.example.pagoflex.ui.screens.AdminScreen
import com.example.pagoflex.ui.screens.DashboardScreen
import com.example.pagoflex.ui.screens.DetallePagoScreen
import com.example.pagoflex.ui.screens.DetallePerfilScreen
import com.example.pagoflex.ui.screens.FormularioScreen
import com.example.pagoflex.ui.screens.LoginScreen
import com.example.pagoflex.ui.screens.PagosScreen
import com.example.pagoflex.ui.screens.PantallaInformativaPago
import com.example.pagoflex.ui.screens.ResumenHistorialScreen
import com.example.pagoflex.ui.screens.ResultadoPagoScreen
import com.example.pagoflex.viewmodel.FormularioViewModel
import com.example.pagoflex.viewmodel.PagoViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun PagoFlexApp(
    repository: PagoFlexRepository,
    formularioViewModel: FormularioViewModel,
    pagoViewModel: PagoViewModel
) {
    val scope = rememberCoroutineScope()
    val colores = MaterialTheme.colorScheme

    val formularioState by formularioViewModel.uiState.collectAsState()
    val pagoState by pagoViewModel.uiState.collectAsState()

    // =====================================================
    // SESIÓN Y NAVEGACIÓN
    // =====================================================

    var sesion by remember {
        mutableStateOf<Map<String, String>?>(null)
    }

    var modo by remember {
        mutableStateOf("login")
    }

    var pantalla by remember {
        mutableStateOf("inicio")
    }

    // =====================================================
    // LOGIN Y MENSAJES
    // =====================================================

    var correo by remember {
        mutableStateOf("")
    }

    var clave by remember {
        mutableStateOf("")
    }

    var mensaje by remember {
        mutableStateOf("")
    }

    // =====================================================
    // ESTADOS DE CARGA
    // =====================================================

    var ocupado by remember {
        mutableStateOf(false)
    }

    var cargandoDatos by remember {
        mutableStateOf(false)
    }

    // =====================================================
    // DEUDAS Y PAGOS
    // =====================================================

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

    // =====================================================
    // PERFIL
    // =====================================================

    var verPerfil by remember {
        mutableStateOf(false)
    }

    var seccionPerfil by remember {
        mutableStateOf("cuenta")
    }

    val usuarioId = sesion?.get("id")?.toIntOrNull()
    val rol = sesion?.get("rol")

    // =====================================================
    // CARGA DE DATOS
    // =====================================================

    suspend fun cargarDatos(id: Int) {
        val resultado = withContext(Dispatchers.IO) {
            repository.listarDeudas(id) to
                    repository.listarPagos(id)
        }

        if (sesion?.get("id")?.toIntOrNull() == id) {
            deudas = resultado.first
            pagos = resultado.second
        }
    }

    // =====================================================
    // CIERRE DE SESIÓN
    // =====================================================

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

    // =====================================================
    // ACTUALIZACIÓN Y NAVEGACIÓN
    // =====================================================

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

    // =====================================================
    // CONFIRMACIÓN DEL PAGO
    // =====================================================

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
                    ValidacionesTarjeta.vencimientoVigente(
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
                        }?.let { pagoRegistrado ->
                            ultimoPago = pagoRegistrado
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

    // =====================================================
    // CARGA INICIAL DEL USUARIO
    // =====================================================

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

    // =====================================================
    // REGISTRO DEL USUARIO
    // =====================================================

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

    // =====================================================
    // SELECCIÓN DE DEUDA
    // =====================================================

    val pendientes = deudas.filter {
        it["estado"] == "PENDIENTE"
    }

    val deudaSeleccionada = pendientes.firstOrNull {
        it["id"] == deudaSeleccionadaId
    }

    // =====================================================
    // BOTÓN ATRÁS DE ANDROID
    // =====================================================

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

    // =====================================================
    // PANTALLA ACTUAL
    // =====================================================

    when {
        // REGISTRO

        sesion == null && modo == "registro" -> {
            FormularioScreen(
                viewModel = formularioViewModel,
                ocupado = ocupado,
                onVolverAlLogin = {
                    formularioViewModel.onLimpiar()
                    modo = "login"
                }
            )
        }

        // LOGIN

        sesion == null -> {
            LoginScreen(
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

        // ADMINISTRADOR

        rol == "ADMIN" -> {
            AdminScreen(
                repository = repository,
                usuario = sesion.orEmpty(),
                onCerrarSesion = {
                    cerrarSesion()
                }
            )
        }

        // USUARIO

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
                    // MENSAJE GENERAL

                    if (mensaje.isNotBlank()) {
                        MensajePagoFlex(
                            texto = mensaje,
                            onCerrar = {
                                mensaje = ""
                            }
                        )
                    }

                    // CONTENIDO DE LA SECCIÓN

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        when (pantalla) {
                            // DEUDAS PENDIENTES

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

                            // DETALLE DEL PAGO

                            "detallePago" -> {
                                val deuda = deudaSeleccionada

                                if (deuda == null) {
                                    PantallaInformativaPago(
                                        titulo = "Deuda no disponible",
                                        texto = "Regresa a Pagos y " +
                                                "actualiza tus deudas.",
                                        textoBotonVolver = "Volver a Pagos",
                                        onVolver = {
                                            irAPagos()
                                        }
                                    )
                                } else if (ocupado) {
                                    CargaPagoFlex(
                                        texto = "Registrando pago..."
                                    )
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

                            // RESULTADO DEL PAGO

                            "pagoExitoso" -> {
                                ResultadoPagoScreen(
                                    pago = ultimoPago,

                                    onVolverInicio = {
                                        irAInicio()
                                    },

                                    onVerHistorial = {
                                        irAHistorial()
                                    }
                                )
                            }

                            // HISTORIAL

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

                            // INICIO

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