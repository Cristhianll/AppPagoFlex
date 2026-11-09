package com.example.pagoflex.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import androidx.compose.ui.unit.sp
import com.example.pagoflex.data.PagoFlexRepository
import com.example.pagoflex.validation.ValidacionesDeuda
import com.example.pagoflex.validation.ValidacionesRut
import com.example.pagoflex.ui.components.BotonPrincipal
import com.example.pagoflex.ui.components.BotonSecundario
import com.example.pagoflex.ui.components.CampoTexto
import com.example.pagoflex.ui.components.IconoPagoFlex
import com.example.pagoflex.ui.components.NombrePagoFlex
import com.example.pagoflex.ui.components.SelectorDesplegable
import com.example.pagoflex.ui.styles.EstilosCampo
import com.example.pagoflex.ui.theme.LocalControlTemaPagoFlex
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


private enum class SeccionAdmin(val titulo: String, val icono: String) {
    RESUMEN("Resumen", "📊"),
    DEUDAS("Deudas", "📄"),
    CLIENTES("Clientes", "👥"),
    NUEVA("Nueva", "➕")
}

private enum class FiltroDeuda(val titulo: String) {
    TODAS("Todas"),
    PENDIENTES("Pendientes"),
    VENCIDAS("Vencidas"),
    PAGADAS("Pagadas")
}

private data class Acceso(
    val icono: String,
    val texto: String,
    val onClick: () -> Unit
)

private fun estaVencida(deuda: Map<String, String>): Boolean =
    deuda["estado"] == "PENDIENTE" &&
            (ValidacionesDeuda.diasHasta(deuda["vencimiento"].orEmpty()) ?: 0L) < 0L

private fun etiquetaCliente(cliente: Map<String, String>): String {
    val rut = cliente["rut"].orEmpty()
    val detalle = if (rut.isBlank()) cliente["correo"].orEmpty() else ValidacionesRut.formatear(rut)
    return "${cliente["nombre"].orEmpty()} — $detalle"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    repository: PagoFlexRepository,
    usuario: Map<String, String>,
    onCerrarSesion: () -> Unit
) {
    val colores = MaterialTheme.colorScheme
    val tema = LocalControlTemaPagoFlex.current
    val scope = rememberCoroutineScope()

    // GESTOR crea/edita/elimina deudas. ADMIN solo consulta.
    val puedeGestionar = usuario["rol"] == "GESTOR"

    val formatoMoneda = remember { NumberFormat.getCurrencyInstance(Locale("es", "CL")) }

    var seccion by remember { mutableStateOf(SeccionAdmin.RESUMEN) }
    var filtro by remember { mutableStateOf(FiltroDeuda.TODAS) }
    var busqueda by remember { mutableStateOf("") }

    var usuarios by remember { mutableStateOf<List<Map<String, String>>>(emptyList()) }
    var deudas by remember { mutableStateOf<List<Map<String, String>>>(emptyList()) }

    var usuarioSeleccionadoId by remember { mutableStateOf<String?>(null) }
    var deudaEditandoId by remember { mutableStateOf<Int?>(null) }
    var deudaAEliminar by remember { mutableStateOf<Map<String, String>?>(null) }

    var concepto by remember { mutableStateOf("") }
    var empresa by remember { mutableStateOf("") }
    var monto by remember { mutableStateOf("") }
    var vencimiento by remember { mutableStateOf("") }

    var mensaje by remember { mutableStateOf("") }
    var error by remember { mutableStateOf(false) }
    var ocupado by remember { mutableStateOf(false) }

    var menuExpandido by remember { mutableStateOf(false) }
    var mostrarConfiguracion by remember { mutableStateOf(false) }

    val pendientes = deudas.filter { it["estado"] == "PENDIENTE" }
    val pagadas = deudas.filter { it["estado"] == "PAGADA" }
    val vencidas = pendientes.filter { estaVencida(it) }
    val totalPorCobrar = pendientes.sumOf { it["monto"]?.toDoubleOrNull() ?: 0.0 }
    val totalRecaudado = pagadas.sumOf { it["monto"]?.toDoubleOrNull() ?: 0.0 }

    val deudasFiltradas = deudas.filter { d ->
        val pasaFiltro = when (filtro) {
            FiltroDeuda.TODAS -> true
            FiltroDeuda.PENDIENTES -> d["estado"] == "PENDIENTE"
            FiltroDeuda.VENCIDAS -> estaVencida(d)
            FiltroDeuda.PAGADAS -> d["estado"] == "PAGADA"
        }
        val texto = busqueda.trim()
        val pasaBusqueda = texto.isEmpty() || listOf(
            d["concepto"], d["empresa"], d["nombreUsuario"], d["rutUsuario"]
        ).any { it.orEmpty().contains(texto, ignoreCase = true) }

        pasaFiltro && pasaBusqueda
    }

    fun limpiarFormulario() {
        deudaEditandoId = null
        usuarioSeleccionadoId = null
        concepto = ""
        empresa = ""
        monto = ""
        vencimiento = ""
    }

    suspend fun cargarDatos() {
        val resultado = withContext(Dispatchers.IO) {
            repository.listarClientes() to repository.listarDeudas()
        }
        usuarios = resultado.first
        deudas = resultado.second
    }

    suspend fun recargarDespuesDeOperacion() {
        try {
            cargarDatos()
        } catch (_: Exception) {
            mensaje += " No se pudo recargar el listado. Vuelve a ingresar al panel."
            error = true
        }
    }

    fun refrescar() {
        if (ocupado) return
        scope.launch {
            ocupado = true
            try {
                cargarDatos()
                mensaje = "Datos actualizados."
                error = false
            } catch (_: Exception) {
                mensaje = "No se pudo actualizar la información."
                error = true
            } finally {
                ocupado = false
            }
        }
    }

    fun irANuevaDeuda(idCliente: String? = null) {
        if (!puedeGestionar) return
        limpiarFormulario()
        usuarioSeleccionadoId = idCliente
        mensaje = ""
        seccion = SeccionAdmin.NUEVA
    }

    fun guardarDeuda() {
        if (!puedeGestionar || ocupado) return

        val idUsuario = usuarioSeleccionadoId?.toIntOrNull()
        val montoIngresado = monto.trim().replace(",", ".").toDoubleOrNull()
        val fechaIngresada = vencimiento.trim()

        val problema = ValidacionesDeuda.obtenerError(
            usuarioId = idUsuario,
            concepto = concepto,
            empresa = empresa,
            monto = montoIngresado,
            vencimiento = fechaIngresada
        )

        if (problema != null) {
            mensaje = problema
            error = true
            return
        }

        val usuarioGuardar = idUsuario ?: return
        val valorMonto = montoIngresado ?: return
        val idEdicion = deudaEditandoId
        val conceptoGuardar = concepto.trim()
        val empresaGuardar = empresa.trim()

        scope.launch {
            ocupado = true
            try {
                val guardada = withContext(Dispatchers.IO) {
                    if (idEdicion == null) {
                        repository.crearDeuda(
                            usuarioGuardar, conceptoGuardar, empresaGuardar, valorMonto, fechaIngresada
                        )
                    } else {
                        repository.actualizarDeuda(
                            idEdicion, usuarioGuardar, conceptoGuardar, empresaGuardar, valorMonto, fechaIngresada
                        )
                    }
                }

                if (guardada) {
                    limpiarFormulario()
                    mensaje = if (idEdicion == null) "Deuda creada correctamente."
                    else "Deuda modificada correctamente."
                    error = false
                    filtro = FiltroDeuda.TODAS
                    busqueda = ""
                    seccion = SeccionAdmin.DEUDAS
                    recargarDespuesDeOperacion()
                } else {
                    mensaje = if (idEdicion == null) "No se pudo crear la deuda. Revisa el cliente y los datos."
                    else "No se pudo modificar. Puede que ya tenga pagos o que no exista."
                    error = true
                }
            } catch (_: Exception) {
                mensaje = "Ocurrió un error al guardar. Verifica el listado antes de volver a intentarlo."
                error = true
            } finally {
                ocupado = false
            }
        }
    }

    fun editarDeuda(deuda: Map<String, String>) {
        if (!puedeGestionar || ocupado) return

        val id = deuda["id"]?.toIntOrNull()
        if (id == null) {
            mensaje = "La deuda no tiene un identificador válido."
            error = true
            return
        }

        deudaEditandoId = id
        usuarioSeleccionadoId = deuda["usuarioId"]
        concepto = deuda["concepto"].orEmpty()
        empresa = deuda["empresa"].orEmpty()
        monto = deuda["monto"].orEmpty().let { if (it.endsWith(".0")) it.dropLast(2) else it }
        vencimiento = deuda["vencimiento"].orEmpty()

        mensaje = "Editando deuda #$id."
        error = false
        seccion = SeccionAdmin.NUEVA
    }

    fun eliminarDeuda(deuda: Map<String, String>) {
        if (!puedeGestionar || ocupado) return

        val id = deuda["id"]?.toIntOrNull()
        deudaAEliminar = null

        if (id == null) {
            mensaje = "La deuda no tiene un identificador válido."
            error = true
            return
        }

        scope.launch {
            ocupado = true
            try {
                val eliminada = withContext(Dispatchers.IO) { repository.eliminarDeuda(id) }

                if (eliminada) {
                    if (deudaEditandoId == id) limpiarFormulario()
                    mensaje = "Deuda eliminada correctamente."
                    error = false
                    recargarDespuesDeOperacion()
                } else {
                    mensaje = "No se pudo eliminar. Las deudas con pagos registrados se conservan."
                    error = true
                }
            } catch (_: Exception) {
                mensaje = "Ocurrió un error al eliminar. Verifica el listado."
                error = true
            } finally {
                ocupado = false
            }
        }
    }

    LaunchedEffect(repository) {
        ocupado = true
        try {
            cargarDatos()
        } catch (_: Exception) {
            mensaje = "No se pudo cargar la información. Vuelve a ingresar al panel."
            error = true
        } finally {
            ocupado = false
        }
    }

    BackHandler(enabled = seccion != SeccionAdmin.RESUMEN && !ocupado) {
        seccion = SeccionAdmin.RESUMEN
    }

    Scaffold(
        containerColor = colores.background,

        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconoPagoFlex(tamano = 34.dp)
                        Spacer(Modifier.width(10.dp))
                        Column {
                            NombrePagoFlex(
                                tamanoFuente = 18.sp,
                                colorPago = colores.onPrimary,
                                colorFlex = colores.tertiary
                            )
                            Text(
                                text = if (puedeGestionar) "Panel de gestión"
                                else "Panel administrador · solo lectura",
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                },
                actions = {
                    Box {
                        IconButton(onClick = { menuExpandido = true }) {
                            Text("⋮", fontSize = 22.sp, color = colores.onPrimary)
                        }
                        DropdownMenu(
                            expanded = menuExpandido,
                            onDismissRequest = { menuExpandido = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("🔄  Actualizar datos") },
                                onClick = { menuExpandido = false; refrescar() }
                            )
                            DropdownMenuItem(
                                text = { Text("⚙️  Configuración") },
                                onClick = { menuExpandido = false; mostrarConfiguracion = true }
                            )
                            DropdownMenuItem(
                                text = { Text("🚪  Cerrar sesión", color = colores.error) },
                                onClick = { menuExpandido = false; onCerrarSesion() }
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colores.primary,
                    titleContentColor = colores.onPrimary,
                    actionIconContentColor = colores.onPrimary
                )
            )
        },

        bottomBar = {
            NavigationBar(containerColor = colores.surface, contentColor = colores.onSurface) {
                SeccionAdmin.entries
                    .filter { puedeGestionar || it != SeccionAdmin.NUEVA }
                    .forEach { s ->
                        NavigationBarItem(
                            selected = seccion == s,
                            onClick = {
                                if (s == SeccionAdmin.NUEVA && deudaEditandoId == null) {
                                    mensaje = ""
                                }
                                seccion = s
                            },
                            icon = { Text(s.icono) },
                            label = { Text(s.titulo, maxLines = 1) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = colores.onSecondaryContainer,
                                selectedTextColor = colores.onSurface,
                                indicatorColor = colores.secondaryContainer,
                                unselectedIconColor = colores.onSurfaceVariant,
                                unselectedTextColor = colores.onSurfaceVariant
                            )
                        )
                    }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (ocupado) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            if (mensaje.isNotBlank()) {
                Surface(
                    color = if (error) colores.errorContainer else colores.primaryContainer,
                    contentColor = if (error) colores.onErrorContainer else colores.onPrimaryContainer
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = mensaje, modifier = Modifier.weight(1f), fontSize = 14.sp)
                        TextButton(onClick = { mensaje = "" }) { Text("Cerrar") }
                    }
                }
            }

            when (seccion) {
                SeccionAdmin.RESUMEN -> PanelResumen(
                    nombre = usuario["nombre"].orEmpty().ifBlank { "Administrador" },
                    puedeGestionar = puedeGestionar,
                    totalClientes = usuarios.size,
                    totalPendientes = pendientes.size,
                    totalVencidas = vencidas.size,
                    totalPagadas = pagadas.size,
                    porCobrar = formatoMoneda.format(totalPorCobrar),
                    recaudado = formatoMoneda.format(totalRecaudado),
                    habilitado = !ocupado,
                    onNuevaDeuda = { irANuevaDeuda() },
                    onVerDeudas = {
                        filtro = FiltroDeuda.TODAS; busqueda = ""; seccion = SeccionAdmin.DEUDAS
                    },
                    onVerVencidas = {
                        filtro = FiltroDeuda.VENCIDAS; busqueda = ""; seccion = SeccionAdmin.DEUDAS
                    },
                    onVerClientes = { seccion = SeccionAdmin.CLIENTES },
                    onActualizar = { refrescar() },
                    onConfiguracion = { mostrarConfiguracion = true },
                    onCerrarSesion = onCerrarSesion
                )

                SeccionAdmin.DEUDAS -> PanelDeudas(
                    deudas = deudasFiltradas,
                    puedeGestionar = puedeGestionar,
                    filtro = filtro,
                    onFiltro = { filtro = it },
                    busqueda = busqueda,
                    onBusqueda = { busqueda = it },
                    formatoMoneda = formatoMoneda,
                    habilitado = !ocupado,
                    onEditar = { editarDeuda(it) },
                    onEliminar = { deudaAEliminar = it }
                )

                SeccionAdmin.CLIENTES -> PanelClientes(
                    clientes = usuarios,
                    deudas = deudas,
                    puedeGestionar = puedeGestionar,
                    formatoMoneda = formatoMoneda,
                    onNuevaDeudaPara = { irANuevaDeuda(it) },
                    onVerDeudasDe = { nombre ->
                        filtro = FiltroDeuda.TODAS
                        busqueda = nombre
                        seccion = SeccionAdmin.DEUDAS
                    }
                )

                SeccionAdmin.NUEVA -> if (puedeGestionar) {
                    PanelFormulario(
                        editandoId = deudaEditandoId,
                        clientes = usuarios,
                        usuarioSeleccionadoId = usuarioSeleccionadoId,
                        onUsuario = { usuarioSeleccionadoId = it },
                        concepto = concepto, onConcepto = { concepto = it },
                        empresa = empresa, onEmpresa = { empresa = it },
                        monto = monto, onMonto = { monto = it },
                        vencimiento = vencimiento, onVencimiento = { vencimiento = it },
                        habilitado = !ocupado,
                        onGuardar = { guardarDeuda() },
                        onCancelar = {
                            limpiarFormulario()
                            mensaje = ""
                            error = false
                        }
                    )
                } else {
                    Text(
                        text = "Tu rol es de solo lectura: no puedes crear ni editar deudas.",
                        modifier = Modifier.padding(16.dp),
                        color = colores.onBackground
                    )
                }
            }
        }
    }

    deudaAEliminar?.let { deuda ->
        AlertDialog(
            onDismissRequest = { deudaAEliminar = null },
            title = { Text("Eliminar deuda") },
            text = {
                Text(
                    "¿Eliminar \"${deuda["concepto"].orEmpty()}\" de " +
                            "${deuda["nombreUsuario"].orEmpty()}?\n\nEsta acción no se puede deshacer."
                )
            },
            confirmButton = {
                TextButton(onClick = { eliminarDeuda(deuda) }, enabled = !ocupado) {
                    Text("Eliminar", color = colores.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { deudaAEliminar = null }) { Text("Cancelar") }
            }
        )
    }

    if (mostrarConfiguracion) {
        AlertDialog(
            onDismissRequest = { mostrarConfiguracion = false },
            title = { Text("Configuración", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text("Modo oscuro", fontWeight = FontWeight.SemiBold)
                            Text(
                                text = if (tema.oscuro) "Activado" else "Desactivado",
                                color = colores.onSurfaceVariant,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        Switch(
                            checked = tema.oscuro,
                            onCheckedChange = { tema.cambiarModoOscuro(it) }
                        )
                    }

                    Text(
                        text = if (tema.sigueSistema) "PagoFlex está utilizando el tema del teléfono."
                        else "PagoFlex está utilizando la apariencia que elegiste.",
                        color = colores.onSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall
                    )

                    if (!tema.sigueSistema) {
                        BotonSecundario(
                            texto = "Usar tema del teléfono",
                            onClick = { tema.usarTemaDelSistema() },
                            modifier = Modifier.fillMaxWidth(),
                            altura = null
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { mostrarConfiguracion = false }) { Text("Cerrar") }
            }
        )
    }
}

@Composable
private fun PanelResumen(
    nombre: String,
    puedeGestionar: Boolean,
    totalClientes: Int,
    totalPendientes: Int,
    totalVencidas: Int,
    totalPagadas: Int,
    porCobrar: String,
    recaudado: String,
    habilitado: Boolean,
    onNuevaDeuda: () -> Unit,
    onVerDeudas: () -> Unit,
    onVerVencidas: () -> Unit,
    onVerClientes: () -> Unit,
    onActualizar: () -> Unit,
    onConfiguracion: () -> Unit,
    onCerrarSesion: () -> Unit
) {
    val colores = MaterialTheme.colorScheme

    val accesos = buildList {
        if (puedeGestionar) add(Acceso("➕", "Nueva deuda", onNuevaDeuda))
        add(Acceso("📄", "Ver deudas", onVerDeudas))
        add(Acceso("⏰", "Deudas vencidas", onVerVencidas))
        add(Acceso("👥", "Clientes", onVerClientes))
        add(Acceso("🔄", "Actualizar datos", onActualizar))
        add(Acceso("⚙️", "Configuración", onConfiguracion))
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = colores.primaryContainer,
                    contentColor = colores.onPrimaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        "Hola, $nombre",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        if (puedeGestionar) "Gestiona clientes y deudas desde un solo lugar."
                        else "Consulta clientes, deudas y recaudación. Este perfil es de solo lectura."
                    )
                }
            }
        }

        item {
            Text("Resumen general", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                TarjetaDato("CLIENTES", "$totalClientes", Modifier.weight(1f))
                TarjetaDato("PENDIENTES", "$totalPendientes", Modifier.weight(1f))
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                TarjetaDato("VENCIDAS", "$totalVencidas", Modifier.weight(1f))
                TarjetaDato("PAGADAS", "$totalPagadas", Modifier.weight(1f))
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                TarjetaDato("POR COBRAR", porCobrar, Modifier.weight(1f), destacado = true)
                TarjetaDato("RECAUDADO", recaudado, Modifier.weight(1f), destacado = true)
            }
        }

        item {
            Text("Accesos rápidos", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        }

        accesos.chunked(2).forEach { fila ->
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    fila.forEach { acceso ->
                        BotonAcceso(
                            acceso.icono, acceso.texto, acceso.onClick,
                            Modifier.weight(1f), habilitado
                        )
                    }
                    if (fila.size == 1) Spacer(Modifier.weight(1f))
                }
            }
        }

        item {
            BotonSecundario(
                texto = "Cerrar sesión",
                onClick = onCerrarSesion,
                habilitado = habilitado,
                modifier = Modifier.fillMaxWidth(),
                colores = ButtonDefaults.outlinedButtonColors(contentColor = colores.error),
                pesoTexto = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun TarjetaDato(
    titulo: String,
    valor: String,
    modifier: Modifier = Modifier,
    destacado: Boolean = false
) {
    val colores = MaterialTheme.colorScheme

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (destacado) colores.primaryContainer else colores.surface,
            contentColor = if (destacado) colores.onPrimaryContainer else colores.onSurface
        )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = titulo,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (destacado) colores.onPrimaryContainer else colores.onSurfaceVariant
            )
            Text(text = valor, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun BotonAcceso(
    icono: String,
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    habilitado: Boolean = true
) {
    val colores = MaterialTheme.colorScheme

    Card(
        onClick = onClick,
        enabled = habilitado,
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = colores.secondaryContainer,
            contentColor = colores.onSecondaryContainer
        )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(vertical = 18.dp, horizontal = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(icono, fontSize = 26.sp)
            Text(
                text = texto,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
private fun PanelDeudas(
    deudas: List<Map<String, String>>,
    puedeGestionar: Boolean,
    filtro: FiltroDeuda,
    onFiltro: (FiltroDeuda) -> Unit,
    busqueda: String,
    onBusqueda: (String) -> Unit,
    formatoMoneda: NumberFormat,
    habilitado: Boolean,
    onEditar: (Map<String, String>) -> Unit,
    onEliminar: (Map<String, String>) -> Unit
) {
    val colores = MaterialTheme.colorScheme

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            OutlinedTextField(
                value = busqueda,
                onValueChange = onBusqueda,
                label = { Text("Buscar por cliente, RUT, concepto o empresa") },
                singleLine = true,
                colors = EstilosCampo.colores(),
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FiltroDeuda.entries.forEach { f ->
                    FilterChip(
                        selected = filtro == f,
                        onClick = { onFiltro(f) },
                        label = { Text(f.titulo) }
                    )
                }
            }
        }

        item {
            Text(
                text = "${deudas.size} resultado(s)",
                color = colores.onSurfaceVariant,
                fontSize = 13.sp
            )
        }

        if (deudas.isEmpty()) {
            item {
                Text(
                    text = "No hay deudas que coincidan con el filtro.",
                    color = colores.onBackground
                )
            }
        }

        items(items = deudas, key = { it["id"].orEmpty() }) { deuda ->
            val pendiente = deuda["estado"] == "PENDIENTE"
            val vencida = estaVencida(deuda)
            val montoDeuda = deuda["monto"]?.toDoubleOrNull() ?: 0.0
            val fecha = deuda["vencimiento"].orEmpty()

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = colores.surface,
                    contentColor = colores.onSurface
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = deuda["concepto"].orEmpty(),
                            modifier = Modifier.weight(1f),
                            color = colores.primary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = when {
                                !pendiente -> colores.secondaryContainer
                                vencida -> colores.errorContainer
                                else -> colores.primaryContainer
                            },
                            contentColor = when {
                                !pendiente -> colores.onSecondaryContainer
                                vencida -> colores.onErrorContainer
                                else -> colores.onPrimaryContainer
                            }
                        ) {
                            Text(
                                text = when {
                                    !pendiente -> "PAGADA"
                                    vencida -> "VENCIDA"
                                    else -> "PENDIENTE"
                                },
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    val rut = deuda["rutUsuario"].orEmpty()
                    Text(
                        "👤 ${deuda["nombreUsuario"].orEmpty()}" +
                                if (rut.isBlank()) "" else " • ${ValidacionesRut.formatear(rut)}",
                        fontSize = 14.sp
                    )
                    Text("🏢 ${deuda["empresa"].orEmpty()}", fontSize = 14.sp)
                    Text(
                        text = formatoMoneda.format(montoDeuda),
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )

                    if (pendiente) {
                        val dias = ValidacionesDeuda.diasHasta(fecha)
                        Text(
                            text = "Vence: $fecha • ${ValidacionesDeuda.textoVencimiento(fecha)}",
                            color = if (dias == null || dias < 0) colores.error else colores.primary,
                            fontSize = 13.sp
                        )

                        if (puedeGestionar) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                BotonSecundario(
                                    texto = "Editar",
                                    onClick = { onEditar(deuda) },
                                    habilitado = habilitado,
                                    modifier = Modifier.weight(1f),
                                    altura = 42.dp
                                )
                                BotonSecundario(
                                    texto = "Eliminar",
                                    onClick = { onEliminar(deuda) },
                                    habilitado = habilitado,
                                    modifier = Modifier.weight(1f),
                                    colores = ButtonDefaults.outlinedButtonColors(contentColor = colores.error),
                                    altura = 42.dp
                                )
                            }
                        }
                    } else {
                        Text(
                            text = "Pagada • se conserva para mantener el historial.",
                            color = colores.onSurfaceVariant,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PanelClientes(
    clientes: List<Map<String, String>>,
    deudas: List<Map<String, String>>,
    puedeGestionar: Boolean,
    formatoMoneda: NumberFormat,
    onNuevaDeudaPara: (String) -> Unit,
    onVerDeudasDe: (String) -> Unit
) {
    val colores = MaterialTheme.colorScheme

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Clientes (${clientes.size})", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        }

        if (clientes.isEmpty()) {
            item {
                Text(
                    "Todavía no hay clientes registrados. Se crean desde \"Crear cuenta\" en el inicio de sesión.",
                    color = colores.onBackground
                )
            }
        }

        items(items = clientes, key = { it["id"].orEmpty() }) { cliente ->
            val id = cliente["id"].orEmpty()
            val deudasCliente = deudas.filter {
                it["usuarioId"] == id && it["estado"] == "PENDIENTE"
            }
            val total = deudasCliente.sumOf { it["monto"]?.toDoubleOrNull() ?: 0.0 }
            val rut = cliente["rut"].orEmpty()

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = colores.surface,
                    contentColor = colores.onSurface
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = cliente["nombre"].orEmpty(),
                        color = colores.primary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )
                    Text(
                        text = "RUT: " + if (rut.isBlank()) "no registrado" else ValidacionesRut.formatear(rut),
                        fontSize = 14.sp
                    )
                    Text("✉️ ${cliente["correo"].orEmpty()}", fontSize = 14.sp)
                    Text(
                        text = "${deudasCliente.size} deuda(s) pendiente(s) • ${formatoMoneda.format(total)}",
                        color = colores.onSurfaceVariant,
                        fontSize = 13.sp
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        BotonSecundario(
                            texto = "Ver deudas",
                            onClick = { onVerDeudasDe(cliente["nombre"].orEmpty()) },
                            modifier = Modifier.weight(1f),
                            altura = 42.dp
                        )
                        if (puedeGestionar) {
                            BotonPrincipal(
                                texto = "Nueva deuda",
                                onClick = { onNuevaDeudaPara(id) },
                                modifier = Modifier.weight(1f),
                                altura = 42.dp,
                                colores = ButtonDefaults.buttonColors(
                                    containerColor = colores.secondary,
                                    contentColor = colores.onSecondary
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PanelFormulario(
    editandoId: Int?,
    clientes: List<Map<String, String>>,
    usuarioSeleccionadoId: String?,
    onUsuario: (String) -> Unit,
    concepto: String,
    onConcepto: (String) -> Unit,
    empresa: String,
    onEmpresa: (String) -> Unit,
    monto: String,
    onMonto: (String) -> Unit,
    vencimiento: String,
    onVencimiento: (String) -> Unit,
    habilitado: Boolean,
    onGuardar: () -> Unit,
    onCancelar: () -> Unit
) {
    val colores = MaterialTheme.colorScheme
    val seleccionado = clientes.firstOrNull { it["id"] == usuarioSeleccionadoId }

    LazyColumn(
        modifier = Modifier.fillMaxSize().imePadding(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = colores.surface,
                    contentColor = colores.onSurface
                )
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = if (editandoId == null) "Crear una deuda" else "Editar deuda #$editandoId",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = colores.primary
                    )

                    if (clientes.isEmpty()) {
                        Text(
                            "No hay clientes registrados. Crea una cuenta desde el inicio de sesión.",
                            color = colores.onSurfaceVariant
                        )
                    }

                    SelectorDesplegable(
                        etiqueta = "Cliente destinatario",
                        textoSeleccionado = seleccionado?.let { etiquetaCliente(it) }.orEmpty(),
                        opciones = clientes.map { etiquetaCliente(it) },
                        onSeleccion = { indice -> onUsuario(clientes[indice]["id"].orEmpty()) },
                        habilitado = habilitado && clientes.isNotEmpty()
                    )

                    CampoAdmin(concepto, onConcepto, "Concepto", habilitado)
                    CampoAdmin(empresa, onEmpresa, "Empresa o servicio", habilitado)
                    CampoAdmin(
                        valor = monto,
                        onCambia = onMonto,
                        etiqueta = "Monto en CLP",
                        habilitado = habilitado,
                        teclado = KeyboardType.Decimal,
                        ayuda = "Sin separadores de miles. Ejemplo: 45000"
                    )
                    SelectorFechaVencimiento(
                        valor = vencimiento,
                        onCambia = onVencimiento,
                        habilitado = habilitado
                    )

                    BotonPrincipal(
                        texto = if (editandoId == null) "Crear deuda" else "Guardar cambios",
                        onClick = onGuardar,
                        habilitado = habilitado && clientes.isNotEmpty(),
                        modifier = Modifier.fillMaxWidth(),
                        altura = 52.dp,
                        forma = RoundedCornerShape(14.dp),
                        colores = ButtonDefaults.buttonColors(
                            containerColor = colores.secondary,
                            contentColor = colores.onSecondary
                        ),
                        pesoTexto = FontWeight.Bold
                    )

                    BotonSecundario(
                        texto = if (editandoId == null) "Limpiar formulario" else "Cancelar edición",
                        onClick = onCancelar,
                        habilitado = habilitado,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
private fun CampoAdmin(
    valor: String,
    onCambia: (String) -> Unit,
    etiqueta: String,
    habilitado: Boolean,
    teclado: KeyboardType = KeyboardType.Text,
    ayuda: String? = null,
    placeholder: String? = null
) {
    CampoTexto(
        valor = valor,
        onValorCambia = onCambia,
        etiqueta = etiqueta,
        icono = null,
        habilitado = habilitado,
        tipoTeclado = teclado,
        ayuda = ayuda,
        placeholder = placeholder,
        forma = OutlinedTextFieldDefaults.shape,
        colores = EstilosCampo.colores(),
        accionTeclado = ImeAction.Default,
        reservarEspacioAyuda = false
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SelectorFechaVencimiento(
    valor: String,
    onCambia: (String) -> Unit,
    habilitado: Boolean
) {
    val colores = MaterialTheme.colorScheme
    var mostrarCalendario by remember { mutableStateOf(false) }

    val formato = remember {
        SimpleDateFormat("yyyy-MM-dd", Locale.US).apply {
            isLenient = false
            timeZone = TimeZone.getTimeZone("UTC")
        }
    }

    val millisInicial = remember(valor) {
        try {
            if (valor.isNotBlank()) formato.parse(valor)?.time else null
        } catch (_: Exception) {
            null
        }
    }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = millisInicial,
        yearRange = IntRange(
            Calendar.getInstance().get(Calendar.YEAR),
            Calendar.getInstance().get(Calendar.YEAR) + 10
        )
    )

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        OutlinedTextField(
            value = valor.ifBlank { "Sin fecha seleccionada" },
            onValueChange = {},
            modifier = Modifier.fillMaxWidth(),
            enabled = false,
            readOnly = true,
            label = { Text("Fecha de vencimiento") },
            trailingIcon = {
                Text("📅", modifier = Modifier.padding(end = 8.dp))
            },
            supportingText = {
                Text(
                    text = "Usa el botón de abajo para elegir en el calendario",
                    color = colores.onSurfaceVariant
                )
            },
            shape = OutlinedTextFieldDefaults.shape,
            colors = EstilosCampo.colores(),
            singleLine = true
        )

        OutlinedButton(
            onClick = { mostrarCalendario = true },
            enabled = habilitado,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = if (valor.isBlank()) "📅  Abrir calendario" else "📅  Cambiar fecha",
                fontWeight = FontWeight.SemiBold
            )
        }
    }

    if (mostrarCalendario) {
        DatePickerDialog(
            onDismissRequest = { mostrarCalendario = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val millis = datePickerState.selectedDateMillis
                        if (millis != null) {
                            onCambia(formato.format(Date(millis)))
                        }
                        mostrarCalendario = false
                    }
                ) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarCalendario = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
