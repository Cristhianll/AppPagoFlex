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
import com.example.pagoflex.ui.screens.*
import com.example.pagoflex.viewmodel.FormularioViewModel
import com.example.pagoflex.viewmodel.PagoViewModel
import kotlinx.coroutines.CancellationException
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
    var tarjetas by remember {
        mutableStateOf<List<Map<String, String>>>(emptyList())
    }

    var deudaSeleccionadaId by remember {
        mutableStateOf<String?>(null)
    }
    var tarjetaSeleccionadaId by remember {
        mutableStateOf<String?>(null)
    }
    var ultimoPago by remember {
        mutableStateOf<Map<String, String>?>(null)
    }

    var verPerfil by remember { mutableStateOf(false) }
    var seccionPerfil by remember { mutableStateOf("cuenta") }

    val usuarioId = sesion?.get("id")?.toIntOrNull()
    val rol = sesion?.get("rol")

    val tarjetaElegida = tarjetas.firstOrNull {
        it["id"] == tarjetaSeleccionadaId
    } ?: tarjetas.firstOrNull {
        it["principal"] == "1"
    } ?: tarjetas.firstOrNull()

    suspend fun cargarDatos(id: Int) {
        val (deudasList, pagosList, tarjetasList) =
            withContext(Dispatchers.IO) {
                Triple(
                    repository.listarDeudas(id),
                    repository.listarPagos(id),
                    repository.listarTarjetasUsuario(id)
                )
            }

        if (sesion?.get("id")?.toIntOrNull() == id) {
            deudas = deudasList
            pagos = pagosList
            tarjetas = tarjetasList
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
        tarjetas = emptyList()

        deudaSeleccionadaId = null
        tarjetaSeleccionadaId = null
        ultimoPago = null

        verPerfil = false
        seccionPerfil = "cuenta"
    }

    fun actualizarDatos() {
        val id = usuarioId ?: return
        if (cargandoDatos || ocupado) return

        scope.launch {
            cargandoDatos = true

            try {
                cargarDatos(id)
            } catch (c: CancellationException) {
                throw c
            } catch (_: Exception) {
                mensaje = "No se pudo actualizar la información."
            } finally {
                cargandoDatos = false
            }
        }
    }

    fun recargarTarjetas() {
        val id = usuarioId ?: return

        scope.launch {
            try {
                val lista = withContext(Dispatchers.IO) {
                    repository.listarTarjetasUsuario(id)
                }

                if (sesion?.get("id")?.toIntOrNull() == id) {
                    tarjetas = lista
                }
            } catch (c: CancellationException) {
                throw c
            } catch (_: Exception) {
                mensaje = "No se pudieron actualizar las tarjetas."
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
        tarjetaSeleccionadaId = null
        mensaje = ""
        pantalla = "detallePago"
    }

    fun confirmarPagoSeleccionado(deuda: Map<String, String>) {
        if (ocupado || cargandoDatos) return

        val idUsuario = usuarioId
        val idDeuda = deuda["id"]?.toIntOrNull()

        if (idUsuario == null || idDeuda == null) {
            mensaje = "No se pudo identificar al usuario o la deuda."
            return
        }

        val idTarjeta = tarjetaElegida?.get("id")?.toIntOrNull()

        if (
            tarjetaElegida == null ||
            idTarjeta == null ||
            !ValidacionesTarjeta.vencimientoVigente(
                tarjetaElegida["vencimiento"].orEmpty()
            )
        ) {
            mensaje = "Selecciona una tarjeta vigente para continuar."
            seccionPerfil = "tarjetas"
            verPerfil = true
            return
        }

        val etiquetaTarjeta =
            "${tarjetaElegida["marca"].orEmpty()} •••• " +
                    tarjetaElegida["ultimosDigitos"].orEmpty()

        scope.launch {
            ocupado = true

            try {
                val pagada = withContext(Dispatchers.IO) {
                    repository.pagarDeuda(
                        deudaId = idDeuda,
                        usuarioId = idUsuario,
                        tarjetaId = idTarjeta
                    )
                }

                if (pagada) {
                    ultimoPago = mapOf(
                        "concepto" to deuda["concepto"].orEmpty(),
                        "empresa" to deuda["empresa"].orEmpty(),
                        "monto" to deuda["monto"].orEmpty(),
                        "tarjeta" to etiquetaTarjeta
                    )

                    deudas = deudas.map { actual ->
                        if (actual["id"] == idDeuda.toString()) {
                            actual + ("estado" to "PAGADA")
                        } else {
                            actual
                        }
                    }

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
                    } catch (c: CancellationException) {
                        throw c
                    } catch (_: Exception) {
                        mensaje =
                            "El pago se registró, pero no se pudo actualizar el historial."
                    }
                } else {
                    mensaje =
                        "No se pudo registrar el pago. Actualiza las deudas."
                    pantalla = "pagos"
                    deudaSeleccionadaId = null

                    try {
                        cargarDatos(idUsuario)
                    } catch (c: CancellationException) {
                        throw c
                    } catch (_: Exception) {
                        mensaje += " Tampoco se pudo actualizar el listado."
                    }
                }
            } catch (c: CancellationException) {
                throw c
            } catch (_: Exception) {
                mensaje =
                    "No se pudo completar la operación. Revisa el historial antes de reintentar."
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
            } catch (c: CancellationException) {
                throw c
            } catch (_: Exception) {
                mensaje = "No se pudieron cargar tus deudas y pagos."
            } finally {
                cargandoDatos = false
            }
        }
    }

    LaunchedEffect(formularioState.registroExitoso, modo) {
        if (modo == "registro" && formularioState.registroExitoso) {
            ocupado = true

            try {
                val datos = formularioState

                val error = withContext(Dispatchers.IO) {
                    repository.registrarUsuario(
                        nombre = datos.nombre,
                        rut = datos.rut,
                        correo = datos.correo,
                        clave = datos.contrasena,
                        pregunta1 = datos.pregunta1,
                        respuesta1 = datos.respuesta1,
                        pregunta2 = datos.pregunta2,
                        respuesta2 = datos.respuesta2
                    )
                }

                if (error == null) {
                    correo = datos.correo.trim()
                    clave = ""
                    mensaje = "Cuenta creada. Ahora inicia sesión."
                    formularioViewModel.onLimpiar()
                    modo = "login"
                } else {
                    formularioViewModel.onErrorRegistro(error)
                }
            } catch (c: CancellationException) {
                throw c
            } catch (_: Exception) {
                formularioViewModel.onErrorRegistro(
                    "Ocurrió un error al registrar la cuenta."
                )
            } finally {
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
    }

    when {
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

        sesion == null && modo == "recuperar" -> {
            RecuperarCuentaScreen(
                repository = repository,
                correoInicial = correo,
                onVolver = {
                    modo = "login"
                },
                onFinalizado = { correoActualizado ->
                    correo = correoActualizado
                    clave = ""
                    mensaje = "Contraseña actualizada. Ahora inicia sesión."
                    modo = "login"
                }
            )
        }

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
                onRecuperar = {
                    mensaje = ""
                    modo = "recuperar"
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

                                val idSesion =
                                    resultado?.get("id")?.toIntOrNull()

                                if (resultado == null || idSesion == null) {
                                    mensaje = "Correo o contraseña incorrectos."
                                } else {
                                    pagoViewModel.cargarUsuario(idSesion)

                                    deudas = emptyList()
                                    pagos = emptyList()
                                    tarjetas = emptyList()
                                    deudaSeleccionadaId = null
                                    tarjetaSeleccionadaId = null
                                    ultimoPago = null

                                    verPerfil = false
                                    seccionPerfil = "cuenta"
                                    pantalla = "inicio"
                                    mensaje = ""
                                    clave = ""

                                    sesion = resultado
                                }
                            } catch (c: CancellationException) {
                                throw c
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
                    repository = repository,
                    usuario = usuario,
                    tarjetas = tarjetas,
                    notificacionesActivas = pagoState.notificacionesActivas,
                    seccionInicial = seccionPerfil,
                    onVolver = {
                        verPerfil = false
                    },
                    onCerrarSesion = {
                        cerrarSesion()
                    },
                    onTarjetasCambiaron = {
                        recargarTarjetas()
                    },
                    onUsuarioActualizado = {
                        sesion = it
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
                        MensajePagoFlex(
                            texto = mensaje,
                            onCerrar = {
                                mensaje = ""
                            }
                        )
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
                                        texto =
                                            "Regresa a Pagos y actualiza tus deudas.",
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
                                        tarjetas = tarjetas,
                                        tarjetaSeleccionadaId =
                                            tarjetaElegida?.get("id"),
                                        onSeleccionarTarjeta = {
                                            tarjetaSeleccionadaId = it
                                        },
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
                                    nombreUsuario =
                                        usuario["nombre"].orEmpty(),
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