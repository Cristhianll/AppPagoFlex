package com.example.pagoflex.ui.components

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.ByteArrayOutputStream

fun leerImagenReducida(context: Context, uri: Uri, maxLado: Int = 1280): ByteArray? {
    return try {
        val limites = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        context.contentResolver.openInputStream(uri)?.use {
            BitmapFactory.decodeStream(it, null, limites)
        }
        val mayor = maxOf(limites.outWidth, limites.outHeight)
        if (mayor <= 0) return null

        var muestreo = 1
        while (mayor / muestreo > maxLado * 2) {
            muestreo *= 2
        }
        val opciones = BitmapFactory.Options().apply { inSampleSize = muestreo }
        val bitmap = context.contentResolver.openInputStream(uri)?.use {
            BitmapFactory.decodeStream(it, null, opciones)
        } ?: return null

        val ladoMayor = maxOf(bitmap.width, bitmap.height)
        val escalado = if (ladoMayor > maxLado) {
            val escala = maxLado.toFloat() / ladoMayor
            Bitmap.createScaledBitmap(
                bitmap,
                (bitmap.width * escala).toInt().coerceAtLeast(1),
                (bitmap.height * escala).toInt().coerceAtLeast(1),
                true
            )
        } else {
            bitmap
        }

        val salida = ByteArrayOutputStream()
        escalado.compress(Bitmap.CompressFormat.JPEG, 85, salida)
        salida.toByteArray()
    } catch (_: Exception) {
        null
    }
}
