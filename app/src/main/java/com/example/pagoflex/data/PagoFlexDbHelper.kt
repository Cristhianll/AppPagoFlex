package com.example.pagoflex.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.security.MessageDigest

class PagoFlexDbHelper(context: Context) :
    SQLiteOpenHelper(context, "pagoflex.db", null, 2) {

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
                correo TEXT NOT NULL UNIQUE,
                clave_hash TEXT NOT NULL,
                rol TEXT NOT NULL CHECK(rol IN ('ADMIN','USUARIO'))
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
                FOREIGN KEY(deuda_id) REFERENCES deudas(id)
            )
            """.trimIndent()
        )

        insertarUsuario(
            db,
            "Administrador Demo",
            "admin@pagoflex.cl",
            "Admin123$",
            "ADMIN"
        )

        val emiliaId = insertarUsuario(
            db,
            "Emilia Demo",
            "emilia@pagoflex.cl",
            "Emilia123$",
            "USUARIO"
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

    override fun onUpgrade(
        db: SQLiteDatabase,
        oldVersion: Int,
        newVersion: Int
    ) {
        // Esta implementación no modifica la estructura existente.
    }

    private fun insertarUsuario(
        db: SQLiteDatabase,
        nombre: String,
        correo: String,
        clave: String,
        rol: String
    ): Long {
        val valores = ContentValues().apply {
            put("nombre", nombre.trim())
            put("correo", correo.trim().lowercase(LocaleRoot))
            put("clave_hash", hash(clave))
            put("rol", rol)
        }

        return db.insert("usuarios", null, valores)
    }

    fun iniciarSesion(
        correo: String,
        clave: String
    ): Map<String, String>? {
        readableDatabase.rawQuery(
            """
            SELECT id, nombre, correo, rol
            FROM usuarios
            WHERE correo = ? AND clave_hash = ?
            """.trimIndent(),
            arrayOf(
                correo.trim().lowercase(LocaleRoot),
                hash(clave)
            )
        ).use { cursor ->
            if (!cursor.moveToFirst()) {
                return null
            }

            return mapOf(
                "id" to cursor.getInt(0).toString(),
                "nombre" to cursor.getString(1),
                "correo" to cursor.getString(2),
                "rol" to cursor.getString(3)
            )
        }
    }

    fun registrarUsuario(
        nombre: String,
        correo: String,
        clave: String
    ): Boolean {
        if (
            nombre.isBlank() ||
            correo.isBlank() ||
            clave.length < 6
        ) {
            return false
        }

        return insertarUsuario(
            writableDatabase,
            nombre,
            correo,
            clave,
            "USUARIO"
        ) != -1L
    }

    fun listarUsuarios(): List<Map<String, String>> {
        val resultado = mutableListOf<Map<String, String>>()

        readableDatabase.rawQuery(
            """
            SELECT id, nombre, correo, rol
            FROM usuarios
            ORDER BY nombre
            """.trimIndent(),
            null
        ).use { cursor ->
            while (cursor.moveToNext()) {
                resultado.add(
                    mapOf(
                        "id" to cursor.getInt(0).toString(),
                        "nombre" to cursor.getString(1),
                        "correo" to cursor.getString(2),
                        "rol" to cursor.getString(3)
                    )
                )
            }
        }

        return resultado
    }

    private fun esUsuarioRegistrado(
        db: SQLiteDatabase,
        usuarioId: Int
    ): Boolean {
        db.rawQuery(
            """
            SELECT id
            FROM usuarios
            WHERE id = ? AND rol = 'USUARIO'
            """.trimIndent(),
            arrayOf(usuarioId.toString())
        ).use { cursor ->
            return cursor.moveToFirst()
        }
    }

    fun crearDeuda(
        usuarioId: Int,
        concepto: String,
        empresa: String,
        monto: Double,
        vencimiento: String
    ): Boolean {
        if (
            ValidacionesDeuda.obtenerError(
                usuarioId,
                concepto,
                empresa,
                monto,
                vencimiento
            ) != null
        ) {
            return false
        }

        val db = writableDatabase

        if (!esUsuarioRegistrado(db, usuarioId)) {
            return false
        }

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
        if (
            deudaId <= 0 ||
            ValidacionesDeuda.obtenerError(
                usuarioId,
                concepto,
                empresa,
                monto,
                vencimiento
            ) != null
        ) {
            return false
        }

        val db = writableDatabase

        if (!esUsuarioRegistrado(db, usuarioId)) {
            return false
        }

        val valores = ContentValues().apply {
            put("usuario_id", usuarioId)
            put("concepto", concepto.trim())
            put("empresa", empresa.trim())
            put("monto", monto)
            put("vencimiento", vencimiento)
        }

        return db.update(
            "deudas",
            valores,
            """
            id = ?
            AND estado = 'PENDIENTE'
            AND NOT EXISTS (
                SELECT 1
                FROM pagos
                WHERE pagos.deuda_id = deudas.id
            )
            """.trimIndent(),
            arrayOf(deudaId.toString())
        ) == 1
    }

    fun eliminarDeuda(deudaId: Int): Boolean {
        if (deudaId <= 0) {
            return false
        }

        return writableDatabase.delete(
            "deudas",
            """
            id = ?
            AND estado = 'PENDIENTE'
            AND NOT EXISTS (
                SELECT 1
                FROM pagos
                WHERE pagos.deuda_id = deudas.id
            )
            """.trimIndent(),
            arrayOf(deudaId.toString())
        ) == 1
    }

    fun listarDeudas(
        usuarioId: Int? = null
    ): List<Map<String, String>> {
        val resultado = mutableListOf<Map<String, String>>()

        val filtro = if (usuarioId == null) {
            ""
        } else {
            "WHERE d.usuario_id = ?"
        }

        val argumentos = usuarioId?.let {
            arrayOf(it.toString())
        }

        readableDatabase.rawQuery(
            """
            SELECT d.id,
                   d.usuario_id,
                   d.concepto,
                   d.empresa,
                   d.monto,
                   d.vencimiento,
                   d.estado,
                   u.nombre,
                   u.correo
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
                        "usuario_id" to cursor.getInt(1).toString(),
                        "concepto" to cursor.getString(2),
                        "empresa" to cursor.getString(3),
                        "monto" to cursor.getDouble(4).toString(),
                        "vencimiento" to cursor.getString(5),
                        "estado" to cursor.getString(6),
                        "usuario" to nombreUsuario,
                        "nombreUsuario" to nombreUsuario,
                        "correoUsuario" to cursor.getString(8)
                    )
                )
            }
        }

        return resultado
    }

    fun pagarDeuda(
        deudaId: Int,
        usuarioId: Int
    ): Boolean {
        if (deudaId <= 0 || usuarioId <= 0) {
            return false
        }

        val db = writableDatabase
        db.beginTransaction()

        try {
            val monto: Double

            db.rawQuery(
                """
                SELECT monto
                FROM deudas
                WHERE id = ?
                  AND usuario_id = ?
                  AND estado = 'PENDIENTE'
                  AND NOT EXISTS (
                      SELECT 1
                      FROM pagos
                      WHERE pagos.deuda_id = deudas.id
                  )
                """.trimIndent(),
                arrayOf(
                    deudaId.toString(),
                    usuarioId.toString()
                )
            ).use { cursor ->
                if (!cursor.moveToFirst()) {
                    return false
                }

                monto = cursor.getDouble(0)
            }

            if (!monto.isFinite() || monto <= 0) {
                return false
            }

            val pago = ContentValues().apply {
                put("deuda_id", deudaId)
                put("monto", monto)
            }

            if (db.insert("pagos", null, pago) == -1L) {
                return false
            }

            val valores = ContentValues().apply {
                put("estado", "PAGADA")
            }

            val filasActualizadas = db.update(
                "deudas",
                valores,
                """
                id = ?
                AND usuario_id = ?
                AND estado = 'PENDIENTE'
                """.trimIndent(),
                arrayOf(
                    deudaId.toString(),
                    usuarioId.toString()
                )
            )

            if (filasActualizadas != 1) {
                return false
            }

            db.setTransactionSuccessful()
            return true
        } finally {
            db.endTransaction()
        }
    }

    fun listarPagos(
        usuarioId: Int
    ): List<Map<String, String>> {
        val resultado = mutableListOf<Map<String, String>>()

        readableDatabase.rawQuery(
            """
            SELECT p.id,
                   d.id,
                   d.concepto,
                   d.empresa,
                   p.monto,
                   p.fecha
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
                        "comprobante" to "PF-" +
                                pagoId.toString().padStart(8, '0')
                    )
                )
            }
        }

        return resultado
    }

    private fun hash(valor: String): String {
        val bytes = MessageDigest.getInstance("SHA-256")
            .digest(valor.toByteArray(Charsets.UTF_8))

        return bytes.joinToString("") {
            "%02x".format(it)
        }
    }

    private companion object {
        val LocaleRoot: java.util.Locale = java.util.Locale.ROOT
    }
}