
package com.example.pagoflex

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.pagoflex.data.PagoFlexDbHelper
import com.example.pagoflex.ui.theme.PagoFlexTheme
import java.text.NumberFormat
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = PagoFlexDbHelper(applicationContext)

        setContent {
            PagoFlexTheme {
                AppPagoFlex(db)
            }
        }
    }
}

private fun pesos(valor: String): String {
    val numero = valor.toDoubleOrNull() ?: 0.0
    return NumberFormat.getCurrencyInstance(
        Locale("es", "CL")
    ).format(numero)
}

@Composable
private fun AppPagoFlex(db: PagoFlexDbHelper) {
    var sesion by remember { mutableStateOf<Map<String, String>?>(null) }
    var pantalla by remember { mutableStateOf("login") }
    var mensaje by remember { mutableStateOf("") }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        when (pantalla) {
            "login" -> LoginScreen(
                mensaje = mensaje,
                onIngresar = { correo, clave ->
                    val usuario = db.iniciarSesion(correo, clave)
                    if (usuario == null) {
                        mensaje = "Correo o contraseña incorrectos."
                    } else {
                        sesion = usuario
                        mensaje = ""
                        pantalla = if (usuario["rol"] == "ADMIN") {
                            "admin"
                        } else {
                            "usuario"
                        }
                    }
                },
                onRegistro = { pantalla = "registro"; mensaje = "" }
            )

            "registro" -> RegistroScreen(
                mensaje = mensaje,
                onRegistrar = { nombre, correo, clave, repetir ->
                    when {
                        nombre.trim().length < 3 ->
                            mensaje = "Ingresa un nombre de al menos 3 caracteres."
                        !android.util.Patterns.EMAIL_ADDRESS
                            .matcher(correo.trim()).matches() ->
                            mensaje = "Ingresa un correo válido."
                        clave.length < 8 ||
                                !clave.any { it.isUpperCase() } ||
                                !clave.any { it.isLowerCase() } ||
                                !clave.any { it.isDigit() } ||
                                clave.all { it.isLetterOrDigit() } ->
                            mensaje = "Usa 8 caracteres, mayúscula, minúscula, número y símbolo."
                        clave != repetir ->
                            mensaje = "Las contraseñas no coinciden."
                        db.registrarUsuario(nombre, correo, clave) -> {
                            mensaje = "Cuenta creada. Ya puedes iniciar sesión."
                            pantalla = "login"
                        }
                        else -> mensaje = "No se pudo crear la cuenta. Revisa si el correo ya existe."
                    }
                },
                onVolver = { pantalla = "login"; mensaje = "" }
            )

            "admin" -> {
                val admin = sesion
                if (admin == null || admin["rol"] != "ADMIN") {
                    pantalla = "login"
                } else {
                    AdminScreen(
                        db = db,
                        nombre = admin["nombre"].orEmpty(),
                        mensaje = mensaje,
                        onMensaje = { mensaje = it },
                        onSalir = {
                            sesion = null
                            pantalla = "login"
                            mensaje = ""
                        }
                    )
                }
            }

            "usuario" -> {
                val usuario = sesion
                if (usuario == null || usuario["rol"] != "USUARIO") {
                    pantalla = "login"
                } else {
                    UsuarioScreen(
                        db = db,
                        usuarioId = usuario["id"]!!.toInt(),
                        nombre = usuario["nombre"].orEmpty(),
                        onSalir = {
                            sesion = null
                            pantalla = "login"
                            mensaje = ""
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun LoginScreen(
    mensaje: String,
    onIngresar: (String, String) -> Unit,
    onRegistro: () -> Unit
) {
    var correo by remember { mutableStateOf("usuario@pagoflex.cl") }
    var clave by remember { mutableStateOf("usuario123") }

    Column(
        modifier = Modifier.fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("PagoFlex", style = MaterialTheme.typography.headlineLarge)
        Text(
            "Tus compromisos de pago, en un solo lugar",
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = correo,
            onValueChange = { correo = it },
            label = { Text("Correo electrónico") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = clave,
            onValueChange = { clave = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        if (mensaje.isNotBlank()) {
            Text(
                mensaje,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        Spacer(Modifier.height(12.dp))
        Button(
            onClick = { onIngresar(correo, clave) },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Iniciar sesión") }

        OutlinedButton(
            onClick = onRegistro,
            modifier = Modifier.fillMaxWidth()
        ) { Text("Crear cuenta de usuario") }

        Spacer(Modifier.height(20.dp))
        Text("Cuentas de demostración", style = MaterialTheme.typography.titleMedium)
        Text("Administrador: admin@pagoflex.cl / admin123")
        Text("Usuario: usuario@pagoflex.cl / usuario123")
    }
}

@Composable
private fun RegistroScreen(
    mensaje: String,
    onRegistrar: (String, String, String, String) -> Unit,
    onVolver: () -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var clave by remember { mutableStateOf("") }
    var repetir by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Crear cuenta", style = MaterialTheme.typography.headlineMedium)
        Text("La cuenta nueva tendrá el rol de usuario.")

        OutlinedTextField(
            nombre, { nombre = it },
            label = { Text("Nombre completo") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            correo, { correo = it },
            label = { Text("Correo electrónico") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            clave, { clave = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            repetir, { repetir = it },
            label = { Text("Repetir contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        if (mensaje.isNotBlank()) {
            Text(mensaje, color = MaterialTheme.colorScheme.error)
        }

        Button(
            onClick = { onRegistrar(nombre, correo, clave, repetir) },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Registrar") }

        TextButton(onClick = onVolver) { Text("Volver al inicio de sesión") }
    }
}

@Composable
private fun AdminScreen(
    db: PagoFlexDbHelper,
    nombre: String,
    mensaje: String,
    onMensaje: (String) -> Unit,
    onSalir: () -> Unit
) {
    var concepto by remember { mutableStateOf("") }
    var empresa by remember { mutableStateOf("") }
    var monto by remember { mutableStateOf("") }
    var vencimiento by remember { mutableStateOf("2026-11-30") }
    var usuarioId by remember { mutableStateOf("") }
    var actualizar by remember { mutableIntStateOf(0) }
    var verDeudas by remember { mutableStateOf(true) }

    val usuarios = remember(actualizar) { db.listarUsuarios() }
    val deudas = remember(actualizar) { db.listarDeudas() }

    Column(
        modifier = Modifier.fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text("Panel administrador", style = MaterialTheme.typography.headlineMedium)
        Text("Hola, $nombre")
        Text("Crear y asignar compromisos de pago.")

        HorizontalDivider()

        Text("Nueva deuda", style = MaterialTheme.typography.titleLarge)

        OutlinedTextField(
            concepto, { concepto = it },
            label = { Text("Concepto de la deuda") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            empresa, { empresa = it },
            label = { Text("Empresa asociada") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            monto, { monto = it },
            label = { Text("Monto en pesos chilenos") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            vencimiento, { vencimiento = it },
            label = { Text("Vencimiento (AAAA-MM-DD)") },
            modifier = Modifier.fillMaxWidth()
        )

        Text("Selecciona el usuario destinatario:")
        usuarios.forEach { usuario ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(Modifier.weight(1f)) {
                    Text(usuario["nombre"].orEmpty())
                    Text(
                        usuario["correo"].orEmpty(),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                RadioButton(
                    selected = usuarioId == usuario["id"],
                    onClick = { usuarioId = usuario["id"].orEmpty() }
                )
            }
        }

        Button(
            onClick = {
                val cantidad = monto.trim().replace(",", ".").toDoubleOrNull()

                val fechaValida = try {
                    val formato = java.text.SimpleDateFormat(
                        "yyyy-MM-dd",
                        java.util.Locale.US
                    )
                    formato.isLenient = false

                    val fecha = formato.parse(vencimiento)
                    fecha != null &&
                            formato.format(fecha) == vencimiento
                } catch (_: Exception) {
                    false
                }

                when {
                    concepto.isBlank() || empresa.isBlank() ->
                        onMensaje("Completa el concepto y la empresa.")
                    cantidad == null || !cantidad.isFinite() || cantidad <= 0 ->
                        onMensaje("El monto debe ser mayor que cero.")
                    !fechaValida ->
                        onMensaje("La fecha debe tener formato AAAA-MM-DD.")
                    usuarioId.toIntOrNull() == null ->
                        onMensaje("Selecciona un usuario.")
                    db.crearDeuda(
                        usuarioId.toInt(),
                        concepto,
                        empresa,
                        cantidad,
                        vencimiento
                    ) -> {
                        concepto = ""
                        empresa = ""
                        monto = ""
                        onMensaje("Deuda creada y asignada correctamente.")
                        actualizar++
                    }
                    else -> onMensaje("No se pudo guardar la deuda.")
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Crear deuda") }

        if (mensaje.isNotBlank()) {
            Text(mensaje, color = MaterialTheme.colorScheme.primary)
        }

        HorizontalDivider()
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Deudas registradas", style = MaterialTheme.typography.titleLarge)
            TextButton(onClick = { verDeudas = !verDeudas }) {
                Text(if (verDeudas) "Ocultar" else "Mostrar")
            }
        }

        AnimatedVisibility(visible = verDeudas) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (deudas.isEmpty()) Text("Todavía no hay deudas.")
                deudas.forEach { deuda ->
                    Card(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(14.dp)) {
                            Text(
                                deuda["concepto"].orEmpty(),
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text("Usuario: ${deuda["nombreUsuario"]}")
                            Text("Empresa: ${deuda["empresa"]}")
                            Text("Monto: ${pesos(deuda["monto"].orEmpty())}")
                            Text("Vence: ${deuda["vencimiento"]}")
                            Text("Estado: ${deuda["estado"]}")
                        }
                    }
                }
            }
        }

        OutlinedButton(
            onClick = onSalir,
            modifier = Modifier.fillMaxWidth()
        ) { Text("Cerrar sesión") }
    }
}

@Composable
private fun UsuarioScreen(
    db: PagoFlexDbHelper,
    usuarioId: Int,
    nombre: String,
    onSalir: () -> Unit
) {
    var actualizar by remember { mutableIntStateOf(0) }
    var mostrarHistorial by remember { mutableStateOf(false) }

    val deudas = remember(usuarioId, actualizar) {
        db.listarDeudas(usuarioId)
    }
    val pagos = remember(usuarioId, actualizar) {
        db.listarPagos(usuarioId)
    }
    val pendientes = deudas.filter { it["estado"] == "PENDIENTE" }
    val total = pendientes.sumOf { it["monto"]?.toDoubleOrNull() ?: 0.0 }

    Column(
        modifier = Modifier.fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Hola, $nombre", style = MaterialTheme.typography.headlineMedium)
        Text("Tu resumen de pagos")

        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(18.dp)) {
                Text("Total pendiente")
                Text(
                    pesos(total.toString()),
                    style = MaterialTheme.typography.headlineMedium
                )
                Text("${pendientes.size} compromiso(s) pendiente(s)")
            }
        }

        Text("Mis compromisos", style = MaterialTheme.typography.titleLarge)

        if (deudas.isEmpty()) {
            Text("No tienes deudas asignadas.")
        }

        deudas.forEach { deuda ->
            Card(Modifier.fillMaxWidth()) {
                Column(
                    Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Text(
                        deuda["concepto"].orEmpty(),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text("Empresa: ${deuda["empresa"]}")
                    Text("Monto: ${pesos(deuda["monto"].orEmpty())}")
                    Text("Vencimiento: ${deuda["vencimiento"]}")
                    Text("Estado: ${deuda["estado"]}")

                    if (deuda["estado"] == "PENDIENTE") {
                        Button(
                            onClick = {
                                if (db.pagarDeuda(
                                        deuda["id"]!!.toInt(), usuarioId
                                    )) {
                                    actualizar++
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) { Text("Pagar ahora") }
                    }
                }
            }
        }

        OutlinedButton(
            onClick = { mostrarHistorial = !mostrarHistorial },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (mostrarHistorial) "Ocultar historial" else "Ver historial de pagos")
        }

        AnimatedVisibility(mostrarHistorial) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (pagos.isEmpty()) {
                    Text("Todavía no tienes pagos registrados.")
                }
                pagos.forEach { pago ->
                    Card(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(12.dp)) {
                            Text(pago["concepto"].orEmpty())
                            Text("Pagado: ${pesos(pago["monto"].orEmpty())}")
                            Text("Fecha: ${pago["fecha"]}")
                            Text("Comprobante: ${pago["comprobante"]}")
                        }
                    }
                }
            }
        }

        OutlinedButton(
            onClick = onSalir,
            modifier = Modifier.fillMaxWidth()
        ) { Text("Cerrar sesión") }
    }
}
