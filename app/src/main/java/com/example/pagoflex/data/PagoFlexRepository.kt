package com.example.pagoflex.data

import android.content.Context
import com.example.pagoflex.data.remote.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException

typealias FilaMapa = Map<String, String>

class PagoFlexRepository(context: Context) {

    private val sesionPrefs = SesionPreferencias(context.applicationContext)

    init {
        sesionPrefs.token()?.let { RetrofitClient.token = it }
    }

    val maxTarjetas: Int = 4

    suspend fun iniciarSesion(correo: String, clave: String): FilaMapa? {
        return iniciarSesionDetalle(correo, clave).first
    }

    suspend fun iniciarSesionDetalle(correo: String, clave: String): Pair<FilaMapa?, String?> {
        return try {
            val res = RetrofitClient.usuarioApi.login(LoginRequest(correo.trim(), clave))
            RetrofitClient.token = res.token
            sesionPrefs.guardar(res.token, res.id, res.nombre, res.correo, res.rol)
            val mapa = mapOf(
                "id" to res.id.toString(),
                "nombre" to res.nombre,
                "rut" to res.rut,
                "correo" to res.correo,
                "rol" to res.rol,
                "tieneFoto" to if (res.tieneFoto) "1" else "0",
                "fotoUrl" to (res.fotoUrl ?: "")
            )
            mapa to null
        } catch (e: HttpException) {
            val bodyError = e.response()?.errorBody()?.string().orEmpty()
            android.util.Log.e("LOGIN_ERROR", "HTTP ${e.code()}: $bodyError", e)
            val detalle = when (e.code()) {
                401 -> "Correo o contraseña incorrectos."
                403 -> "Tu cuenta aún está pendiente de aprobación. Un gestor debe habilitarla para que puedas ingresar."
                423 -> "Cuenta bloqueada por múltiples intentos fallidos."
                else -> "Error HTTP ${e.code()}: $bodyError"
            }
            null to detalle
        } catch (e: Exception) {
            android.util.Log.e("LOGIN_ERROR", "Error de conexión/lectura: ${e.message}", e)
            null to "Error de conexión/lectura: ${e.message}"
        }
    }

    fun cerrarSesionLocal() {
        RetrofitClient.token = null
        sesionPrefs.limpiar()
    }

    suspend fun registrarUsuario(
        nombre: String,
        rut: String,
        correo: String,
        clave: String,
        pregunta1: String,
        respuesta1: String,
        pregunta2: String,
        respuesta2: String
    ): String? {
        return try {
            RetrofitClient.usuarioApi.registrar(
                RegistroRequest(
                    nombre = nombre.trim(),
                    rut = rut.trim(),
                    correo = correo.trim(),
                    contrasena = clave,
                    pregunta1 = pregunta1,
                    respuesta1 = respuesta1,
                    pregunta2 = pregunta2,
                    respuesta2 = respuesta2
                )
            )
            null
        } catch (e: Exception) {
            e.message ?: "No se pudo registrar el usuario."
        }
    }

    suspend fun existeCorreo(correo: String): Boolean {
        return try {
            RetrofitClient.usuarioApi.listar(correo.trim())
                .any { it.correo.equals(correo.trim(), ignoreCase = true) }
        } catch (_: Exception) {
            false
        }
    }

    suspend fun listarUsuarios(): List<FilaMapa> {
        return try {
            RetrofitClient.usuarioApi.listar().map { usuarioAMap(it) }
        } catch (_: Exception) {
            emptyList()
        }
    }

    suspend fun listarClientes(): List<FilaMapa> {
        return listarUsuarios().filter { it["rol"] == "USUARIO" && it["habilitado"] != "0" }
    }

    suspend fun listarPendientes(): List<FilaMapa> {
        return try {
            RetrofitClient.usuarioApi.listarPendientes().map { usuarioAMap(it) }
        } catch (_: Exception) {
            emptyList()
        }
    }

    suspend fun habilitarUsuario(usuarioId: Int): Boolean {
        return try {
            RetrofitClient.usuarioApi.habilitar(usuarioId.toLong())
            true
        } catch (_: Exception) {
            false
        }
    }

    suspend fun actualizarPerfil(
        usuarioId: Int,
        nombre: String,
        rut: String,
        correo: String
    ): FilaMapa {
        return try {
            RetrofitClient.usuarioApi.actualizar(
                usuarioId.toLong(),
                ActualizarUsuarioRequest(
                    nombre = nombre.trim(),
                    rut = rut.trim()
                )
            )
            emptyMap()
        } catch (e: Exception) {
            mapOf("general" to (e.message ?: "No se pudo actualizar el perfil."))
        }
    }

    suspend fun cambiarClave(
        usuarioId: Int,
        claveActual: String,
        claveNueva: String
    ): FilaMapa {
        return try {
            RetrofitClient.usuarioApi.cambiarContrasena(
                usuarioId.toLong(),
                CambiarContrasenaRequest(
                    contrasenaActual = claveActual,
                    contrasenaNueva = claveNueva
                )
            )
            emptyMap()
        } catch (e: Exception) {
            mapOf("general" to (e.message ?: "No se pudo cambiar la contraseña."))
        }
    }

    suspend fun obtenerPreguntas(correo: String): List<String>? {
        return try {
            val u = RetrofitClient.usuarioApi.listar(correo.trim())
                .firstOrNull { it.correo.equals(correo.trim(), ignoreCase = true) }
                ?: return null
            listOfNotNull(u.pregunta1, u.pregunta2).filter { it.isNotBlank() }
        } catch (_: Exception) {
            null
        }
    }

    suspend fun verificarRespuestas(correo: String, r1: String, r2: String): Boolean {
        return r1.isNotBlank() && r2.isNotBlank()
    }

    suspend fun restablecerClave(
        correo: String,
        r1: String,
        r2: String,
        nuevaClave: String
    ): Boolean {
        return try {
            RetrofitClient.usuarioApi.recuperar(
                RecuperarRequest(
                    correo = correo.trim(),
                    respuesta1 = r1,
                    respuesta2 = r2,
                    nuevaContrasena = nuevaClave
                )
            )
            true
        } catch (_: Exception) {
            false
        }
    }

    suspend fun guardarFoto(usuarioId: Int, fotoBytes: ByteArray): Boolean {
        return try {
            val reqFile = fotoBytes.toRequestBody("image/jpeg".toMediaTypeOrNull())
            val part = MultipartBody.Part.createFormData("foto", "perfil_$usuarioId.jpg", reqFile)
            RetrofitClient.usuarioApi.subirFoto(usuarioId.toLong(), part)
            true
        } catch (_: Exception) {
            false
        }
    }

    suspend fun obtenerFoto(usuarioId: Int): ByteArray? {
        return try {
            val url = RetrofitClient.usuarioApi.obtener(usuarioId.toLong()).fotoUrl
            if (url.isNullOrBlank()) {
                null
            } else {
                RetrofitClient.usuarioApi.descargarFoto(url.trimStart('/')).use { it.bytes() }
            }
        } catch (_: Exception) {
            null
        }
    }

    suspend fun listarDeudas(usuarioId: Int? = null): List<FilaMapa> {
        return try {
            if (usuarioId != null) {
                RetrofitClient.deudaApi.listar(usuarioId = usuarioId.toLong()).map { deudaAMap(it, paraUsuario = true) }
            } else {
                val clientes = RetrofitClient.usuarioApi.listar().filter { it.rol == "USUARIO" }
                clientes.flatMap { cliente ->
                    try {
                        RetrofitClient.deudaApi.listar(usuarioId = cliente.id).map { deuda ->
                            deudaAMap(deuda, cliente.nombre, cliente.rut)
                        }
                    } catch (_: Exception) {
                        emptyList()
                    }
                }
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    suspend fun crearDeuda(
        usuarioId: Int,
        concepto: String,
        empresa: String,
        monto: Double,
        vencimiento: String,
        cuotas: Int = 1
    ): Boolean {
        return try {
            RetrofitClient.deudaApi.crear(
                DeudaRequest(
                    usuarioId = usuarioId.toLong(),
                    concepto = concepto,
                    empresa = empresa,
                    monto = monto,
                    vencimiento = vencimiento,
                    cantidadCuotas = cuotas
                )
            )
            true
        } catch (_: Exception) {
            false
        }
    }

    suspend fun actualizarDeuda(
        deudaId: Int,
        usuarioId: Int,
        concepto: String,
        empresa: String,
        monto: Double,
        vencimiento: String,
        cuotas: Int = 1
    ): Boolean {
        return try {
            RetrofitClient.deudaApi.actualizar(
                deudaId.toLong(),
                DeudaRequest(
                    usuarioId = usuarioId.toLong(),
                    concepto = concepto,
                    empresa = empresa,
                    monto = monto,
                    vencimiento = vencimiento,
                    cantidadCuotas = cuotas
                )
            )
            true
        } catch (_: Exception) {
            false
        }
    }

    suspend fun eliminarDeuda(deudaId: Int): Boolean {
        return try {
            RetrofitClient.deudaApi.eliminar(deudaId.toLong())
            true
        } catch (_: Exception) {
            false
        }
    }

    suspend fun pagarDeuda(deudaId: Int, usuarioId: Int, tarjetaId: Int): Boolean {
        return try {
            val deuda = RetrofitClient.deudaApi.obtener(deudaId.toLong())
            if ((deuda.cantidadCuotas ?: 1) >= 2) {
                val cuota = RetrofitClient.cuotaApi.siguiente(deudaId.toLong())
                RetrofitClient.pagoApi.registrar(
                    PagoRequest(
                        deudaId = deudaId.toLong(),
                        usuarioId = usuarioId.toLong(),
                        monto = cuota.monto,
                        metodo = "TARJETA",
                        tarjetaId = tarjetaId.toLong()
                    )
                )
                RetrofitClient.cuotaApi.pagar(cuota.id)
            } else {
                RetrofitClient.pagoApi.registrar(
                    PagoRequest(
                        deudaId = deudaId.toLong(),
                        usuarioId = usuarioId.toLong(),
                        monto = deuda.monto,
                        metodo = "TARJETA",
                        tarjetaId = tarjetaId.toLong()
                    )
                )
                RetrofitClient.deudaApi.marcarPagada(deudaId.toLong())
            }
            true
        } catch (_: Exception) {
            false
        }
    }

    suspend fun listarPagos(usuarioId: Int): List<FilaMapa> {
        return try {
            val deudas: List<DeudaResponse> = try {
                RetrofitClient.deudaApi.listar(usuarioId = usuarioId.toLong())
            } catch (_: Exception) {
                emptyList()
            }
            val tarjetas: List<TarjetaResponse> = try {
                RetrofitClient.tarjetaApi.listar(usuarioId.toLong())
            } catch (_: Exception) {
                emptyList()
            }
            RetrofitClient.pagoApi.listar(usuarioId.toLong()).map { pago ->
                val deuda = deudas.firstOrNull { it.id == pago.deudaId }
                val tarjeta = tarjetas.firstOrNull { it.id == pago.tarjetaId }
                pagoAMap(pago) + mapOf(
                    "concepto" to (deuda?.concepto ?: ""),
                    "empresa" to (deuda?.empresa ?: ""),
                    "tarjeta" to (if (tarjeta == null) "" else "${tarjeta.marca ?: "Tarjeta"} •••• ${tarjeta.ultimosDigitos}")
                )
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    suspend fun listarTarjetasUsuario(usuarioId: Int): List<FilaMapa> {
        return try {
            RetrofitClient.tarjetaApi.listar(usuarioId.toLong()).map { tarjetaAMap(it) }
        } catch (_: Exception) {
            emptyList()
        }
    }

    suspend fun agregarTarjeta(
        usuarioId: Int,
        ultimosDigitos: String,
        titular: String,
        vencimiento: String,
        marca: String
    ): Boolean {
        return try {
            RetrofitClient.tarjetaApi.agregar(
                TarjetaRequest(
                    usuarioId = usuarioId.toLong(),
                    ultimosDigitos = ultimosDigitos,
                    titular = titular,
                    vencimiento = vencimiento,
                    marca = marca,
                    principal = false
                )
            )
            true
        } catch (_: Exception) {
            false
        }
    }

    suspend fun marcarTarjetaPrincipal(tarjetaId: Int, usuarioId: Int): Boolean {
        return try {
            RetrofitClient.tarjetaApi.marcarPrincipal(tarjetaId.toLong())
            true
        } catch (_: Exception) {
            false
        }
    }

    suspend fun eliminarTarjeta(tarjetaId: Int, usuarioId: Int): Boolean {
        return try {
            RetrofitClient.tarjetaApi.eliminar(tarjetaId.toLong())
            true
        } catch (_: Exception) {
            false
        }
    }

    private fun usuarioAMap(u: UsuarioResponse): FilaMapa = mapOf(
        "id" to u.id.toString(),
        "nombre" to u.nombre,
        "rut" to u.rut,
        "correo" to u.correo,
        "rol" to u.rol,
        "tieneFoto" to if (u.tieneFoto) "1" else "0",
        "fotoUrl" to (u.fotoUrl ?: ""),
        "habilitado" to if (u.habilitado) "1" else "0"
    )

    private fun deudaAMap(
        d: DeudaResponse,
        nombreUsuario: String = "",
        rutUsuario: String = "",
        paraUsuario: Boolean = false
    ): FilaMapa {
        val montoActual = d.montoCuota ?: d.monto
        val vencimientoActual = d.vencimientoActual ?: d.vencimiento
        val pagado = d.montoPagado ?: 0.0
        val pendiente = (d.monto - pagado).coerceAtLeast(0.0)
        return mapOf(
            "id" to d.id.toString(),
            "usuarioId" to d.usuarioId.toString(),
            "nombreUsuario" to nombreUsuario,
            "rutUsuario" to rutUsuario,
            "concepto" to d.concepto,
            "empresa" to d.empresa,
            "monto" to (if (paraUsuario) montoActual else d.monto).toString(),
            "vencimiento" to (if (paraUsuario) vencimientoActual else d.vencimiento),
            "estado" to d.estado,
            "montoTotal" to d.monto.toString(),
            "montoPagado" to pagado.toString(),
            "montoPendiente" to pendiente.toString(),
            "cantidadCuotas" to (d.cantidadCuotas ?: 1).toString(),
            "cuotasPagadas" to (d.cuotasPagadas ?: 0).toString(),
            "cuotaActual" to (d.cuotaActual?.toString() ?: ""),
            "vencimientoActual" to vencimientoActual
        )
    }

    private fun pagoAMap(p: PagoResponse): FilaMapa = mapOf(
        "id" to p.id.toString(),
        "deudaId" to p.deudaId.toString(),
        "usuarioId" to p.usuarioId.toString(),
        "monto" to p.monto.toString(),
        "fecha" to (p.fecha ?: ""),
        "metodo" to (p.metodo ?: "TARJETA"),
        "tarjetaId" to (p.tarjetaId?.toString() ?: "")
    )

    private fun tarjetaAMap(t: TarjetaResponse): FilaMapa = mapOf(
        "id" to t.id.toString(),
        "usuarioId" to t.usuarioId.toString(),
        "ultimosDigitos" to t.ultimosDigitos,
        "titular" to t.titular,
        "vencimiento" to t.vencimiento,
        "marca" to (t.marca ?: "Tarjeta"),
        "principal" to if (t.principal) "1" else "0"
    )
}