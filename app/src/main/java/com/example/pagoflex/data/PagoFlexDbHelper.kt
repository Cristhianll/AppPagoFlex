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

        insertarUsuario(
            db,
            "Emilia Demo",
            "emilia@pagoflex.cl",
            "Emilia123$",
            "USUARIO"
        )

        val deudaInicial = ContentValues().apply {
            put("usuario_id", 2)
            put("concepto", "Cuenta de electricidad")
            put("empresa", "Empresa Demo")
            put("monto", 45000.0)
            put("vencimiento", "2026-11-30")
            put("estado", "PENDIENTE")
        }

        db.insert("deudas", null, deudaInicial)
    }

    override fun onUpgrade(
        db: SQLiteDatabase,
        oldVersion: Int,
        newVersion: Int
    ) {
        // Agregar migraciones aquí si se modifica la estructura de la BD.
    }

    private fun insertarUsuario(
        db: SQLiteDatabase,
        nombre: String,
        correo: String,
        clave: String,
        rol: String
    ) {
        val valores = ContentValues().apply {
            put("nombre", nombre)
            put("correo", correo.trim().lowercase())
            put("clave_hash", hash(clave))
            put("rol", rol)
        }

        db.insert("usuarios", null, valores)
    }

    fun iniciarSesion(
        correo: String,
        clave: String
    ): Map<String, String>? {
        val sql =
            """
            SELECT id, nombre, correo, rol
            FROM usuarios
            WHERE correo = ? AND clave_hash = ?
            """.trimIndent()

        readableDatabase.rawQuery(
            sql,
            arrayOf(correo.trim().lowercase(), hash(clave))
        ).use { cursor ->
            if (!cursor.moveToFirst()) return null

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
        val valores = ContentValues().apply {
            put("nombre", nombre.trim())
            put("correo", correo.trim().lowercase())
            put("clave_hash", hash(clave))
            put("rol", "USUARIO")
        }

        return writableDatabase.insert(
            "usuarios",
            null,
            valores
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

    fun crearDeuda(
        usuarioId: Int,
        concepto: String,
        empresa: String,
        monto: Double,
        vencimiento: String
    ): Boolean {
        val valores = ContentValues().apply {
            put("usuario_id", usuarioId)
            put("concepto", concepto.trim())
            put("empresa", empresa.trim())
            put("monto", monto)
            put("vencimiento", vencimiento)
            put("estado", "PENDIENTE")
        }

        return writableDatabase.insert(
            "deudas",
            null,
            valores
        ) != -1L
    }

    fun listarDeudas(
        usuarioId: Int? = null
    ): List<Map<String, String>> {
        val resultado = mutableListOf<Map<String, String>>()

        val sql: String
        val argumentos: Array<String>?

        if (usuarioId == null) {
            sql =
                """
                SELECT d.id, d.concepto, d.empresa, d.monto,
                       d.vencimiento, d.estado, u.nombre
                FROM deudas d
                INNER JOIN usuarios u ON u.id = d.usuario_id
                ORDER BY d.id DESC
                """.trimIndent()
            argumentos = null
        } else {
            sql =
                """
                SELECT d.id, d.concepto, d.empresa, d.monto,
                       d.vencimiento, d.estado, u.nombre
                FROM deudas d
                INNER JOIN usuarios u ON u.id = d.usuario_id
                WHERE d.usuario_id = ?
                ORDER BY d.id DESC
                """.trimIndent()
            argumentos = arrayOf(usuarioId.toString())
        }

        readableDatabase.rawQuery(sql, argumentos).use { cursor ->
            while (cursor.moveToNext()) {
                resultado.add(
                    mapOf(
                        "id" to cursor.getInt(0).toString(),
                        "concepto" to cursor.getString(1),
                        "empresa" to cursor.getString(2),
                        "monto" to cursor.getDouble(3).toString(),
                        "vencimiento" to cursor.getString(4),
                        "estado" to cursor.getString(5),
                        "usuario" to cursor.getString(6)
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
                """.trimIndent(),
                arrayOf(deudaId.toString(), usuarioId.toString())
            ).use { cursor ->
                if (!cursor.moveToFirst()) return false
                monto = cursor.getDouble(0)
            }

            val pago = ContentValues().apply {
                put("deuda_id", deudaId)
                put("monto", monto)
            }

            if (db.insert("pagos", null, pago) == -1L) {
                return false
            }

            val actualizacion = ContentValues().apply {
                put("estado", "PAGADA")
            }

            db.update(
                "deudas",
                actualizacion,
                "id = ? AND usuario_id = ? AND estado = 'PENDIENTE'",
                arrayOf(deudaId.toString(), usuarioId.toString())
            )

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
            SELECT p.id, d.concepto, p.monto, p.fecha
            FROM pagos p
            INNER JOIN deudas d ON d.id = p.deuda_id
            WHERE d.usuario_id = ?
            ORDER BY p.id DESC
            """.trimIndent(),
            arrayOf(usuarioId.toString())
        ).use { cursor ->
            while (cursor.moveToNext()) {
                resultado.add(
                    mapOf(
                        "id" to cursor.getInt(0).toString(),
                        "concepto" to cursor.getString(1),
                        "monto" to cursor.getDouble(2).toString(),
                        "fecha" to cursor.getString(3)
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
