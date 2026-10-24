package com.example.pagoflex.data

import android.content.ContentValues
import android.content.Context
import android.database.DatabaseUtils
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.security.MessageDigest
import java.util.Locale
import com.example.pagoflex.validation.PreguntasSecretas
import com.example.pagoflex.validation.ValidacionesRut
import com.example.pagoflex.validation.ValidacionesCuenta
import com.example.pagoflex.validation.ValidacionesDeuda
import com.example.pagoflex.validation.ValidacionesTarjeta

class PagoFlexDbHelper(context: Context) :
    SQLiteOpenHelper(context, "pagoflex.db", null, 3) {

    companion object {
        const val MAX_TARJETAS = 4
        private const val RESPUESTA_DEMO_1 = "firulais"
        private const val RESPUESTA_DEMO_2 = "maria"
    }

    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.setForeignKeyConstraintsEnabled(true)
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE usuarios (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL,
                rut TEXT NOT NULL DEFAULT '',
                correo TEXT NOT NULL UNIQUE,
                clave_hash TEXT NOT NULL,
                rol TEXT NOT NULL CHECK(rol IN ('ADMIN','USUARIO')),
                pregunta1 TEXT NOT NULL DEFAULT '',
                respuesta1_hash TEXT NOT NULL DEFAULT '',
                pregunta2 TEXT NOT NULL DEFAULT '',
                respuesta2_hash TEXT NOT NULL DEFAULT '',
                foto BLOB
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE deudas (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                usuario_id INTEGER NOT NULL,
                concepto TEXT NOT NULL,
                empresa TEXT NOT NULL,
                monto REAL NOT NULL,
                vencimiento TEXT NOT NULL,
                estado TEXT NOT NULL DEFAULT 'PENDIENTE',
                FOREIGN KEY(usuario_id) REFERENCES usuarios(id)
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE pagos (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                deuda_id INTEGER NOT NULL,
                monto REAL NOT NULL,
                fecha TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,
                tarjeta TEXT NOT NULL DEFAULT '',
                FOREIGN KEY(deuda_id) REFERENCES deudas(id)
            )
            """.trimIndent()
        )

        crearTablaTarjetas(db)

        insertarUsuario(
            db, "Administrador", "11111111-1", "admin@pagoflex.cl",
            "Admin123$", "ADMIN",
            PreguntasSecretas.lista[0], RESPUESTA_DEMO_1,
            PreguntasSecretas.lista[1], RESPUESTA_DEMO_2
        )

        val emiliaId = insertarUsuario(
            db, "Emilia Demo", "12345678-5", "emilia@pagoflex.cl",
            "Emilia123$", "USUARIO",
            PreguntasSecretas.lista[0], RESPUESTA_DEMO_1,
            PreguntasSecretas.lista[1], RESPUESTA_DEMO_2
        )

        if (emiliaId != -1L) {
            val deudaInicial = ContentValues().apply {
                put("usuario_id", emiliaId)
                put("concepto", "Cuenta de electricidad")
                put("empresa", "Empresa Demo")
                put("monto", 45000.0)
                put("vencimiento", "2026-11-30")
                put("estado", "PENDIENTE")
            }
            db.insert("deudas", null, deudaInicial)
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 3) {
            db.execSQL("ALTER TABLE usuarios ADD COLUMN rut TEXT NOT NULL DEFAULT ''")
            db.execSQL("ALTER TABLE usuarios ADD COLUMN pregunta1 TEXT NOT NULL DEFAULT ''")
            db.execSQL("ALTER TABLE usuarios ADD COLUMN respuesta1_hash TEXT NOT NULL DEFAULT ''")
            db.execSQL("ALTER TABLE usuarios ADD COLUMN pregunta2 TEXT NOT NULL DEFAULT ''")
            db.execSQL("ALTER TABLE usuarios ADD COLUMN respuesta2_hash TEXT NOT NULL DEFAULT ''")
            db.execSQL("ALTER TABLE usuarios ADD COLUMN foto BLOB")
            db.execSQL("ALTER TABLE pagos ADD COLUMN tarjeta TEXT NOT NULL DEFAULT ''")
            crearTablaTarjetas(db)

            completarCuentaDemo(db, "admin@pagoflex.cl", "11111111-1")
            completarCuentaDemo(db, "emilia@pagoflex.cl", "12345678-5")
        }
    }

    private fun crearTablaTarjetas(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE tarjetas (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                usuario_id INTEGER NOT NULL,
                ultimos_digitos TEXT NOT NULL,
                titular TEXT NOT NULL,
                vencimiento TEXT NOT NULL,
                marca TEXT NOT NULL DEFAULT 'Tarjeta',
                principal INTEGER NOT NULL DEFAULT 0,
                FOREIGN KEY(usuario_id) REFERENCES usuarios(id)
            )
            """.trimIndent()
        )
    }

    private fun completarCuentaDemo(db: SQLiteDatabase, correo: String, rut: String) {
        val valores = ContentValues().apply {
            put("rut", rut)
            put("pregunta1", PreguntasSecretas.lista[0])
            put("respuesta1_hash", hash(PreguntasSecretas.normalizarRespuesta(RESPUESTA_DEMO_1)))
            put("pregunta2", PreguntasSecretas.lista[1])
            put("respuesta2_hash", hash(PreguntasSecretas.normalizarRespuesta(RESPUESTA_DEMO_2)))
        }
        db.update("usuarios", valores, "correo = ?", arrayOf(correo))
    }

    private fun insertarUsuario(
        db: SQLiteDatabase,
        nombre: String,
        rut: String,
        correo: String,
        clave: String,
        rol: String,
        pregunta1: String,
        respuesta1: String,
        pregunta2: String,
        respuesta2: String
    ): Long {
        val valores = ContentValues().apply {
            put("nombre", nombre.trim())
            put("rut", ValidacionesRut.normalizar(rut))
            put("correo", correo.trim().lowercase(Locale.ROOT))
            put("clave_hash", hash(clave))
            put("rol", rol)
            put("pregunta1", pregunta1.trim())
            put("respuesta1_hash", hash(PreguntasSecretas.normalizarRespuesta(respuesta1)))
            put("pregunta2", pregunta2.trim())
            put("respuesta2_hash", hash(PreguntasSecretas.normalizarRespuesta(respuesta2)))
        }
        return db.insert("usuarios", null, valores)
    }

    private fun existeValor(
        db: SQLiteDatabase,
        columna: String,
        valor: String,
        excluirId: Int = -1
    ): Boolean {
        db.rawQuery(
            "SELECT 1 FROM usuarios WHERE $columna = ? AND id != ? LIMIT 1",
            arrayOf(valor, excluirId.toString())
        ).use { return it.moveToFirst() }
    }



    fun iniciarSesion(correo: String, clave: String): Map<String, String>? {
        readableDatabase.rawQuery(
            """
            SELECT id, nombre, correo, rol, rut
            FROM usuarios
            WHERE correo = ? AND clave_hash = ?
            """.trimIndent(),
            arrayOf(correo.trim().lowercase(Locale.ROOT), hash(clave))
        ).use { cursor ->
            if (!cursor.moveToFirst()) return null
            return mapOf(
                "id" to cursor.getInt(0).toString(),
                "nombre" to cursor.getString(1),
                "correo" to cursor.getString(2),
                "rol" to cursor.getString(3),
                "rut" to cursor.getString(4)
            )
        }
    }


    fun registrarUsuario(
        nombre: String,
        rut: String,
        correo: String,
        clave: String,
        pregunta1: String,
        respuesta1: String,
        pregunta2: String,
        respuesta2: String
    ): String? {
        val problema = ValidacionesCuenta.nombre(nombre)
            ?: ValidacionesCuenta.rut(rut)
            ?: ValidacionesCuenta.correo(correo)
            ?: ValidacionesCuenta.contrasena(clave)
            ?: ValidacionesCuenta.preguntas(pregunta1, respuesta1, pregunta2, respuesta2)
        if (problema != null) return problema

        val db = writableDatabase
        val correoNorm = correo.trim().lowercase(Locale.ROOT)
        val rutNorm = ValidacionesRut.normalizar(rut)

        if (existeValor(db, "correo", correoNorm)) return "Ese correo ya está registrado."
        if (existeValor(db, "rut", rutNorm)) return "Ese RUT ya está registrado."

        val id = insertarUsuario(
            db, nombre, rutNorm, correoNorm, clave, "USUARIO",
            pregunta1, respuesta1, pregunta2, respuesta2
        )
        return if (id == -1L) "No se pudo crear la cuenta." else null
    }

    fun obtenerPreguntas(correo: String): List<String>? {
        readableDatabase.rawQuery(
            "SELECT pregunta1, pregunta2 FROM usuarios WHERE correo = ?",
            arrayOf(correo.trim().lowercase(Locale.ROOT))
        ).use { cursor ->
            if (!cursor.moveToFirst()) return null
            val p1 = cursor.getString(0).orEmpty()
            val p2 = cursor.getString(1).orEmpty()
            return if (p1.isBlank() || p2.isBlank()) emptyList() else listOf(p1, p2)
        }
    }

    fun verificarRespuestas(correo: String, respuesta1: String, respuesta2: String): Boolean {
        readableDatabase.rawQuery(
            "SELECT respuesta1_hash, respuesta2_hash FROM usuarios WHERE correo = ?",
            arrayOf(correo.trim().lowercase(Locale.ROOT))
        ).use { cursor ->
            if (!cursor.moveToFirst()) return false
            val h1 = cursor.getString(0).orEmpty()
            val h2 = cursor.getString(1).orEmpty()
            if (h1.isBlank() || h2.isBlank()) return false

            return h1 == hash(PreguntasSecretas.normalizarRespuesta(respuesta1)) &&
                    h2 == hash(PreguntasSecretas.normalizarRespuesta(respuesta2))
        }
    }

    fun restablecerClave(
        correo: String,
        respuesta1: String,
        respuesta2: String,
        nuevaClave: String
    ): Boolean {
        if (ValidacionesCuenta.contrasena(nuevaClave) != null) return false
        if (!verificarRespuestas(correo, respuesta1, respuesta2)) return false

        val valores = ContentValues().apply { put("clave_hash", hash(nuevaClave)) }
        return writableDatabase.update(
            "usuarios", valores, "correo = ?",
            arrayOf(correo.trim().lowercase(Locale.ROOT))
        ) == 1
    }


    fun actualizarPerfil(
        usuarioId: Int,
        nombre: String,
        rut: String,
        correo: String,
        pregunta1: String,
        respuesta1: String,
        pregunta2: String,
        respuesta2: String
    ): String? {
        if (usuarioId <= 0) return "Usuario inválido."

        val cambiaPreguntas = pregunta1.isNotBlank() || pregunta2.isNotBlank() ||
                respuesta1.isNotBlank() || respuesta2.isNotBlank()

        val problema = ValidacionesCuenta.nombre(nombre)
            ?: ValidacionesCuenta.rut(rut)
            ?: ValidacionesCuenta.correo(correo)
            ?: if (cambiaPreguntas) {
                ValidacionesCuenta.preguntas(pregunta1, respuesta1, pregunta2, respuesta2)
            } else null
        if (problema != null) return problema

        val db = writableDatabase
        val correoNorm = correo.trim().lowercase(Locale.ROOT)
        val rutNorm = ValidacionesRut.normalizar(rut)

        if (existeValor(db, "correo", correoNorm, usuarioId)) {
            return "Ese correo ya pertenece a otro usuario."
        }
        if (existeValor(db, "rut", rutNorm, usuarioId)) {
            return "Ese RUT ya pertenece a otro usuario."
        }

        val valores = ContentValues().apply {
            put("nombre", nombre.trim())
            put("rut", rutNorm)
            put("correo", correoNorm)
            if (cambiaPreguntas) {
                put("pregunta1", pregunta1.trim())
                put("respuesta1_hash", hash(PreguntasSecretas.normalizarRespuesta(respuesta1)))
                put("pregunta2", pregunta2.trim())
                put("respuesta2_hash", hash(PreguntasSecretas.normalizarRespuesta(respuesta2)))
            }
        }

        val filas = db.update("usuarios", valores, "id = ?", arrayOf(usuarioId.toString()))
        return if (filas == 1) null else "No se pudo actualizar el perfil."
    }



    fun guardarFoto(usuarioId: Int, jpg: ByteArray): Boolean {
        if (usuarioId <= 0) return false
        val valores = ContentValues().apply { put("foto", jpg) }
        return writableDatabase.update(
            "usuarios", valores, "id = ?", arrayOf(usuarioId.toString())
        ) == 1
    }

    fun obtenerFoto(usuarioId: Int): ByteArray? {
        readableDatabase.rawQuery(
            "SELECT foto FROM usuarios WHERE id = ?",
            arrayOf(usuarioId.toString())
        ).use { cursor ->
            if (!cursor.moveToFirst() || cursor.isNull(0)) return null
            return cursor.getBlob(0)
        }
    }



    fun listarUsuarios(): List<Map<String, String>> {
        val resultado = mutableListOf<Map<String, String>>()
        readableDatabase.rawQuery(
            "SELECT id, nombre, correo, rol, rut FROM usuarios ORDER BY nombre",
            null
        ).use { cursor ->
            while (cursor.moveToNext()) {
                resultado.add(
                    mapOf(
                        "id" to cursor.getInt(0).toString(),
                        "nombre" to cursor.getString(1),
                        "correo" to cursor.getString(2),
                        "rol" to cursor.getString(3),
                        "rut" to cursor.getString(4)
                    )
                )
            }
        }
        return resultado
    }



    fun listarTarjetas(usuarioId: Int): List<Map<String, String>> {
        val resultado = mutableListOf<Map<String, String>>()
        readableDatabase.rawQuery(
            """
            SELECT id, ultimos_digitos, titular, vencimiento, marca, principal
            FROM tarjetas
            WHERE usuario_id = ?
            ORDER BY principal DESC, id ASC
            """.trimIndent(),
            arrayOf(usuarioId.toString())
        ).use { cursor ->
            while (cursor.moveToNext()) {
                resultado.add(
                    mapOf(
                        "id" to cursor.getInt(0).toString(),
                        "ultimosDigitos" to cursor.getString(1),
                        "titular" to cursor.getString(2),
                        "vencimiento" to cursor.getString(3),
                        "marca" to cursor.getString(4),
                        "principal" to cursor.getInt(5).toString()
                    )
                )
            }
        }
        return resultado
    }


    fun agregarTarjeta(
        usuarioId: Int,
        ultimosDigitos: String,
        titular: String,
        vencimiento: String,
        marca: String
    ): String? {
        if (usuarioId <= 0) return "Usuario inválido."
        if (!ultimosDigitos.matches(Regex("[0-9]{4}"))) return "Número de tarjeta inválido."
        if (titular.isBlank()) return "Ingresa el titular."
        if (!ValidacionesTarjeta.vencimientoVigente(vencimiento)) return "La tarjeta está vencida o el formato es inválido."

        val db = writableDatabase
        db.beginTransaction()
        try {
            val cantidad = DatabaseUtils.queryNumEntries(
                db, "tarjetas", "usuario_id = ?", arrayOf(usuarioId.toString())
            )
            if (cantidad >= MAX_TARJETAS) {
                return "Solo puedes registrar hasta $MAX_TARJETAS tarjetas."
            }

            val repetida = DatabaseUtils.queryNumEntries(
                db, "tarjetas",
                "usuario_id = ? AND ultimos_digitos = ? AND vencimiento = ?",
                arrayOf(usuarioId.toString(), ultimosDigitos, vencimiento)
            )
            if (repetida > 0) return "Ya registraste esa tarjeta."

            val valores = ContentValues().apply {
                put("usuario_id", usuarioId)
                put("ultimos_digitos", ultimosDigitos)
                put("titular", titular.trim())
                put("vencimiento", vencimiento)
                put("marca", marca)
                put("principal", if (cantidad == 0L) 1 else 0)
            }
            if (db.insert("tarjetas", null, valores) == -1L) return "No se pudo guardar la tarjeta."

            db.setTransactionSuccessful()
            return null
        } finally {
            db.endTransaction()
        }
    }

    fun eliminarTarjeta(tarjetaId: Int, usuarioId: Int): Boolean {
        val db = writableDatabase
        db.beginTransaction()
        try {
            val borradas = db.delete(
                "tarjetas", "id = ? AND usuario_id = ?",
                arrayOf(tarjetaId.toString(), usuarioId.toString())
            )
            if (borradas != 1) return false

            val hayPrincipal = DatabaseUtils.queryNumEntries(
                db, "tarjetas", "usuario_id = ? AND principal = 1",
                arrayOf(usuarioId.toString())
            ) > 0

            if (!hayPrincipal) {
                db.execSQL(
                    "UPDATE tarjetas SET principal = 1 WHERE id = " +
                            "(SELECT MIN(id) FROM tarjetas WHERE usuario_id = ?)",
                    arrayOf<Any>(usuarioId)
                )
            }
            db.setTransactionSuccessful()
            return true
        } finally {
            db.endTransaction()
        }
    }

    fun marcarTarjetaPrincipal(tarjetaId: Int, usuarioId: Int): Boolean {
        val db = writableDatabase
        db.beginTransaction()
        try {
            db.execSQL(
                "UPDATE tarjetas SET principal = 0 WHERE usuario_id = ?",
                arrayOf<Any>(usuarioId)
            )
            val valores = ContentValues().apply { put("principal", 1) }
            val filas = db.update(
                "tarjetas", valores, "id = ? AND usuario_id = ?",
                arrayOf(tarjetaId.toString(), usuarioId.toString())
            )
            if (filas != 1) return false

            db.setTransactionSuccessful()
            return true
        } finally {
            db.endTransaction()
        }
    }



    private fun esUsuarioRegistrado(db: SQLiteDatabase, usuarioId: Int): Boolean {
        db.rawQuery(
            "SELECT id FROM usuarios WHERE id = ? AND rol = 'USUARIO'",
            arrayOf(usuarioId.toString())
        ).use { cursor -> return cursor.moveToFirst() }
    }

    fun crearDeuda(
        usuarioId: Int,
        concepto: String,
        empresa: String,
        monto: Double,
        vencimiento: String
    ): Boolean {
        if (ValidacionesDeuda.obtenerError(usuarioId, concepto, empresa, monto, vencimiento) != null) {
            return false
        }
        val db = writableDatabase
        if (!esUsuarioRegistrado(db, usuarioId)) return false

        val valores = ContentValues().apply {
            put("usuario_id", usuarioId)
            put("concepto", concepto.trim())
            put("empresa", empresa.trim())
            put("monto", monto)
            put("vencimiento", vencimiento)
            put("estado", "PENDIENTE")
        }
        return db.insert("deudas", null, valores) != -1L
    }

    fun actualizarDeuda(
        deudaId: Int,
        usuarioId: Int,
        concepto: String,
        empresa: String,
        monto: Double,
        vencimiento: String
    ): Boolean {
        if (deudaId <= 0 ||
            ValidacionesDeuda.obtenerError(usuarioId, concepto, empresa, monto, vencimiento) != null
        ) return false

        val db = writableDatabase
        if (!esUsuarioRegistrado(db, usuarioId)) return false

        val valores = ContentValues().apply {
            put("usuario_id", usuarioId)
            put("concepto", concepto.trim())
            put("empresa", empresa.trim())
            put("monto", monto)
            put("vencimiento", vencimiento)
        }
        return db.update(
            "deudas", valores,
            """
            id = ?
            AND estado = 'PENDIENTE'
            AND NOT EXISTS (SELECT 1 FROM pagos WHERE pagos.deuda_id = deudas.id)
            """.trimIndent(),
            arrayOf(deudaId.toString())
        ) == 1
    }

    fun eliminarDeuda(deudaId: Int): Boolean {
        if (deudaId <= 0) return false
        return writableDatabase.delete(
            "deudas",
            """
            id = ?
            AND estado = 'PENDIENTE'
            AND NOT EXISTS (SELECT 1 FROM pagos WHERE pagos.deuda_id = deudas.id)
            """.trimIndent(),
            arrayOf(deudaId.toString())
        ) == 1
    }

    fun listarDeudas(usuarioId: Int? = null): List<Map<String, String>> {
        val resultado = mutableListOf<Map<String, String>>()
        val filtro = if (usuarioId == null) "" else "WHERE d.usuario_id = ?"
        val argumentos = usuarioId?.let { arrayOf(it.toString()) }

        readableDatabase.rawQuery(
            """
            SELECT d.id, d.usuario_id, d.concepto, d.empresa, d.monto,
                   d.vencimiento, d.estado, u.nombre, u.correo, u.rut
            FROM deudas d
            INNER JOIN usuarios u ON u.id = d.usuario_id
            $filtro
            ORDER BY
                CASE WHEN d.estado = 'PENDIENTE' THEN 0 ELSE 1 END,
                d.vencimiento ASC,
                d.id DESC
            """.trimIndent(),
            argumentos
        ).use { cursor ->
            while (cursor.moveToNext()) {
                val nombreUsuario = cursor.getString(7)
                resultado.add(
                    mapOf(
                        "id" to cursor.getInt(0).toString(),
                        "usuarioId" to cursor.getInt(1).toString(),
                        "concepto" to cursor.getString(2),
                        "empresa" to cursor.getString(3),
                        "monto" to cursor.getDouble(4).toString(),
                        "vencimiento" to cursor.getString(5),
                        "estado" to cursor.getString(6),
                        "nombreUsuario" to nombreUsuario,
                        "correoUsuario" to cursor.getString(8),
                        "rutUsuario" to cursor.getString(9)
                    )
                )
            }
        }
        return resultado
    }



    fun pagarDeuda(deudaId: Int, usuarioId: Int, tarjetaId: Int): Boolean {
        if (deudaId <= 0 || usuarioId <= 0 || tarjetaId <= 0) return false

        val db = writableDatabase
        db.beginTransaction()

        try {
            var etiquetaTarjeta: String? = null
            db.rawQuery(
                "SELECT marca, ultimos_digitos, vencimiento FROM tarjetas WHERE id = ? AND usuario_id = ?",
                arrayOf(tarjetaId.toString(), usuarioId.toString())
            ).use { cursor ->
                if (cursor.moveToFirst() &&
                    ValidacionesTarjeta.vencimientoVigente(cursor.getString(2))
                ) {
                    etiquetaTarjeta = "${cursor.getString(0)} •••• ${cursor.getString(1)}"
                }
            }
            val tarjeta = etiquetaTarjeta ?: return false

            var monto = 0.0
            var encontrada = false
            db.rawQuery(
                """
                SELECT monto FROM deudas
                WHERE id = ? AND usuario_id = ? AND estado = 'PENDIENTE'
                  AND NOT EXISTS (SELECT 1 FROM pagos WHERE pagos.deuda_id = deudas.id)
                """.trimIndent(),
                arrayOf(deudaId.toString(), usuarioId.toString())
            ).use { cursor ->
                if (cursor.moveToFirst()) {
                    monto = cursor.getDouble(0)
                    encontrada = true
                }
            }

            if (!encontrada || !monto.isFinite() || monto <= 0) return false

            val pago = ContentValues().apply {
                put("deuda_id", deudaId)
                put("monto", monto)
                put("tarjeta", tarjeta)
            }
            if (db.insert("pagos", null, pago) == -1L) return false

            val valores = ContentValues().apply { put("estado", "PAGADA") }
            val filas = db.update(
                "deudas", valores,
                "id = ? AND usuario_id = ? AND estado = 'PENDIENTE'",
                arrayOf(deudaId.toString(), usuarioId.toString())
            )
            if (filas != 1) return false

            db.setTransactionSuccessful()
            return true
        } finally {
            db.endTransaction()
        }
    }

    fun listarPagos(usuarioId: Int): List<Map<String, String>> {
        val resultado = mutableListOf<Map<String, String>>()
        readableDatabase.rawQuery(
            """
            SELECT p.id, d.id, d.concepto, d.empresa, p.monto, p.fecha, p.tarjeta
            FROM pagos p
            INNER JOIN deudas d ON d.id = p.deuda_id
            WHERE d.usuario_id = ?
            ORDER BY p.id DESC
            """.trimIndent(),
            arrayOf(usuarioId.toString())
        ).use { cursor ->
            while (cursor.moveToNext()) {
                val pagoId = cursor.getInt(0)
                resultado.add(
                    mapOf(
                        "id" to pagoId.toString(),
                        "deudaId" to cursor.getInt(1).toString(),
                        "concepto" to cursor.getString(2),
                        "empresa" to cursor.getString(3),
                        "monto" to cursor.getDouble(4).toString(),
                        "fecha" to cursor.getString(5),
                        "tarjeta" to cursor.getString(6).orEmpty(),
                        "comprobante" to "PF-" + pagoId.toString().padStart(8, '0')
                    )
                )
            }
        }
        return resultado
    }

    private fun hash(valor: String): String {
        val bytes = MessageDigest.getInstance("SHA-256")
            .digest(valor.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }
}