
package com.example.pagoflex.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.security.MessageDigest

class PagoFlexDbHelper(context: Context) :
    SQLiteOpenHelper(context, "pagoflex.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE usuarios (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL,
                correo TEXT NOT NULL UNIQUE,
                clave_hash TEXT NOT NULL,
                rol TEXT NOT NULL CHECK(rol IN ('ADMIN', 'USUARIO'))
            )
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE deudas (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                usuario_id INTEGER NOT NULL,
                concepto TEXT NOT NULL,
                empresa TEXT NOT NULL,
                monto REAL NOT NULL CHECK(monto > 0),
                vencimiento TEXT NOT NULL,
                estado TEXT NOT NULL DEFAULT 'PENDIENTE',
                creada_en TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY(usuario_id) REFERENCES usuarios(id)
            )
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE pagos (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                deuda_id INTEGER NOT NULL,
                monto REAL NOT NULL,
                fecha TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,
                comprobante TEXT NOT NULL,
                FOREIGN KEY(deuda_id) REFERENCES deudas(id)
            )
        """.trimIndent())

        insertarUsuario(
            db, "Administrador Demo",
            "admin@pagoflex.cl", "Admin123$", "ADMIN"
        )
        insertarUsuario(
            db, "Usuario Demo",
            "emilia@pagoflex.cl", "Emilia123$", "USUARIO"
        )

        // Deuda ficticia inicial para probar el panel del usuario.
        val deuda = ContentValues().apply {
            put("usuario_id", 2)
            put("concepto", "Cuenta de electricidad")
            put("empresa", "Empresa Demo")
            put("monto", 45000.0)
            put("vencimiento", "2026-11-30")
            put("estado", "PENDIENTE")
        }
        db.insert("deudas", null, deuda)
    }

    private fun insertarUsuario(
        db: SQLiteDatabase,
        nombre: String,
        correo: String,
        clave: String,
        rol: String
    ) {
        val values = ContentValues().apply {
            put("nombre", nombre)
            put("correo", correo.lowercase())
            put("clave_hash", hash(clave))
            put("rol", rol)
        }
        db.insertOrThrow("usuarios", null, values)
    }

    override fun onUpgrade(
        db: SQLiteDatabase,
        oldVersion: Int,
        newVersion: Int
    ) {
        // En futuras versiones, agrega migraciones sin borrar los datos.
    }

    private fun hash(texto: String): String {
        val bytes = MessageDigest.getInstance("SHA-256")
            .digest(texto.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }

    fun iniciarSesion(correo: String, clave: String): Map<String, String>? {
        val db = readableDatabase
        val cursor = db.rawQuery(
            """
            SELECT id, nombre, correo, rol
            FROM usuarios
            WHERE correo = ? AND clave_hash = ?
            """.trimIndent(),
            arrayOf(correo.trim().lowercase(), hash(clave))
        )

        return cursor.use {
            if (!it.moveToFirst()) return@use null
            mapOf(
                "id" to it.getInt(0).toString(),
                "nombre" to it.getString(1),
                "correo" to it.getString(2),
                "rol" to it.getString(3)
            )
        }
    }

    fun registrarUsuario(
        nombre: String,
        correo: String,
        clave: String
    ): Boolean {
        return try {
            val values = ContentValues().apply {
                put("nombre", nombre.trim())
                put("correo", correo.trim().lowercase())
                put("clave_hash", hash(clave))
                put("rol", "USUARIO")
            }
            writableDatabase.insertOrThrow("usuarios", null, values)
            true
        } catch (_: Exception) {
            false
        }
    }

    fun listarUsuarios(): List<Map<String, String>> {
        val resultado = mutableListOf<Map<String, String>>()
        readableDatabase.rawQuery(
            "SELECT id, nombre, correo FROM usuarios WHERE rol = 'USUARIO' ORDER BY nombre",
            null
        ).use { c ->
            while (c.moveToNext()) {
                resultado.add(
                    mapOf(
                        "id" to c.getInt(0).toString(),
                        "nombre" to c.getString(1),
                        "correo" to c.getString(2)
                    )
                )
            }
        }
        return resultado
    }

    fun crearDeuda(
        usuarioId: Int,
        concepto: String,
        empresa: String,
        monto: Double,
        vencimiento: String
    ): Boolean {
        if (concepto.isBlank() || empresa.isBlank() ||
            monto <= 0 || vencimiento.isBlank()
        ) return false

        val values = ContentValues().apply {
            put("usuario_id", usuarioId)
            put("concepto", concepto.trim())
            put("empresa", empresa.trim())
            put("monto", monto)
            put("vencimiento", vencimiento)
            put("estado", "PENDIENTE")
        }
        return writableDatabase.insert("deudas", null, values) != -1L
    }

    fun listarDeudas(usuarioId: Int? = null): List<Map<String, String>> {
        val resultado = mutableListOf<Map<String, String>>()
        val sql = """
            SELECT d.id, d.usuario_id, u.nombre, u.correo,
                   d.concepto, d.empresa, d.monto,
                   d.vencimiento, d.estado
            FROM deudas d
            INNER JOIN usuarios u ON u.id = d.usuario_id
            ${if (usuarioId != null) "WHERE d.usuario_id = ?" else ""}
            ORDER BY d.id DESC
        """.trimIndent()

        readableDatabase.rawQuery(
            sql,
            if (usuarioId != null) arrayOf(usuarioId.toString()) else null
        ).use { c ->
            while (c.moveToNext()) {
                resultado.add(
                    mapOf(
                        "id" to c.getInt(0).toString(),
                        "usuarioId" to c.getInt(1).toString(),
                        "nombreUsuario" to c.getString(2),
                        "correo" to c.getString(3),
                        "concepto" to c.getString(4),
                        "empresa" to c.getString(5),
                        "monto" to c.getDouble(6).toString(),
                        "vencimiento" to c.getString(7),
                        "estado" to c.getString(8)
                    )
                )
            }
        }
        return resultado
    }

    fun pagarDeuda(deudaId: Int, usuarioId: Int): Boolean {
        val db = writableDatabase
        db.beginTransaction()
        try {

            val monto = db.rawQuery(
                """
    SELECT monto FROM deudas
    WHERE id = ? AND usuario_id = ? AND estado = 'PENDIENTE'
    """.trimIndent(),
                arrayOf(deudaId.toString(), usuarioId.toString())
            ).use { c ->
                if (!c.moveToFirst()) return false
                c.getDouble(0)
            }



            val pago = ContentValues().apply {
                put("deuda_id", deudaId)
                put("monto", monto)
                put("comprobante", "PF-$deudaId-${System.currentTimeMillis()}")
            }
            if (db.insert("pagos", null, pago) == -1L) return false

            val actualizacion = ContentValues().apply {
                put("estado", "PAGADA")
            }
            val filas = db.update(
                "deudas", actualizacion,
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
            SELECT p.id, d.concepto, p.monto, p.fecha, p.comprobante
            FROM pagos p
            INNER JOIN deudas d ON d.id = p.deuda_id
            WHERE d.usuario_id = ?
            ORDER BY p.id DESC
            """.trimIndent(),
            arrayOf(usuarioId.toString())
        ).use { c ->
            while (c.moveToNext()) {
                resultado.add(
                    mapOf(
                        "id" to c.getInt(0).toString(),
                        "concepto" to c.getString(1),
                        "monto" to c.getDouble(2).toString(),
                        "fecha" to c.getString(3),
                        "comprobante" to c.getString(4)
                    )
                )
            }
        }
        return resultado
    }
}
