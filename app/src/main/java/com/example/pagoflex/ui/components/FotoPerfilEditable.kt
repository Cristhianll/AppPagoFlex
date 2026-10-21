package com.example.pagoflex.ui.components

import android.os.Build
import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.pagoflex.data.PagoFlexRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File

private fun prepararJpg(original: Bitmap, maxLado: Int = 512): ByteArray {
    val mayor = maxOf(original.width, original.height)
    val bitmap = if (mayor > maxLado) {
        val escala = maxLado.toFloat() / mayor
        Bitmap.createScaledBitmap(
            original,
            (original.width * escala).toInt().coerceAtLeast(1),
            (original.height * escala).toInt().coerceAtLeast(1),
            true
        )
    } else {
        original
    }
    val salida = ByteArrayOutputStream()
    if (!bitmap.compress(Bitmap.CompressFormat.JPEG, 85, salida)) {
        error("No se pudo convertir la foto a JPG.")
    }
    return salida.toByteArray()
}

private fun decodificarReducida(context: Context, uri: Uri, maxLado: Int = 1024): Bitmap? {
    val limites = BitmapFactory.Options().apply { inJustDecodeBounds = true }
    context.contentResolver.openInputStream(uri)?.use {
        BitmapFactory.decodeStream(it, null, limites)
    }
    var muestra = 1
    while (limites.outWidth / muestra > maxLado || limites.outHeight / muestra > maxLado) {
        muestra *= 2
    }
    val reales = BitmapFactory.Options().apply { inSampleSize = muestra }
    return context.contentResolver.openInputStream(uri)?.use {
        BitmapFactory.decodeStream(it, null, reales)
    }
}

@Suppress("DEPRECATION")
@Composable
fun FotoPerfilEditable(
    usuarioId: Int,
    nombreUsuario: String,
    repository: PagoFlexRepository
) {
    val context = LocalContext.current
    val colores = MaterialTheme.colorScheme
    val scope = rememberCoroutineScope()

    var foto by remember(usuarioId) { mutableStateOf<Bitmap?>(null) }
    var cargando by remember(usuarioId) { mutableStateOf(true) }
    var guardando by remember(usuarioId) { mutableStateOf(false) }
    var mensaje by remember(usuarioId) { mutableStateOf("") }
    var mostrarOpciones by remember { mutableStateOf(false) }
    var mostrarExplicacion by remember { mutableStateOf(false) }
    var mostrarPermisoDenegado by remember { mutableStateOf(false) }
    var mostrarExplicacionGaleria by remember { mutableStateOf(false) }

    // Carga desde la BD (y migra la foto antigua guardada en el teléfono, si existe)
    LaunchedEffect(usuarioId) {
        cargando = true
        try {
            foto = withContext(Dispatchers.IO) {
                val legado = File(context.filesDir, "foto_perfil_usuario_$usuarioId.jpg")
                if (legado.exists()) {
                    if (repository.obtenerFoto(usuarioId) == null) {
                        repository.guardarFoto(usuarioId, legado.readBytes())
                    }
                    legado.delete()
                }
                repository.obtenerFoto(usuarioId)?.let {
                    BitmapFactory.decodeByteArray(it, 0, it.size)
                }
            }
        } catch (c: CancellationException) {
            throw c
        } catch (_: Exception) {
            mensaje = "No se pudo cargar la foto de perfil."
        } finally {
            cargando = false
        }
    }

    fun procesarFoto(obtener: suspend () -> Bitmap?) {
        scope.launch {
            guardando = true
            mensaje = ""
            try {
                val resultado = withContext(Dispatchers.IO) {
                    val bitmap = obtener() ?: return@withContext null
                    val guardada = repository.guardarFoto(usuarioId, prepararJpg(bitmap))
                    if (guardada) bitmap else null
                }
                if (resultado != null) {
                    foto = resultado
                    mensaje = "Foto de perfil actualizada."
                } else {
                    mensaje = "No se pudo guardar la foto. Intenta nuevamente."
                }
            } catch (c: CancellationException) {
                throw c
            } catch (_: Exception) {
                mensaje = "No se pudo guardar la foto. Intenta nuevamente."
            } finally {
                guardando = false
            }
        }
    }

    val camaraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { imagen ->
        if (imagen == null) {
            mensaje = "No se tomó una nueva foto."
        } else {
            procesarFoto { imagen }
        }
    }

    fun abrirCamara() {
        try {
            mensaje = ""
            camaraLauncher.launch(null)
        } catch (_: ActivityNotFoundException) {
            mensaje = "No hay una aplicación de cámara disponible."
        } catch (_: SecurityException) {
            mensaje = "No se pudo acceder a la cámara. Revisa el permiso."
        }
    }

    val permisoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { concedido ->
        if (concedido) abrirCamara() else mostrarPermisoDenegado = true
    }

    val galeriaLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            procesarFoto { decodificarReducida(context, uri) }
        }
    }
    fun abrirGaleria() {
        try {
            mensaje = ""
            galeriaLauncher.launch("image/*")
        } catch (_: ActivityNotFoundException) {
            mensaje = "No hay una aplicación disponible para elegir imágenes."
        } catch (_: SecurityException) {
            mensaje = "No se pudo acceder a la imagen. Revisa los permisos."
        }
    }

    fun tienePermisoGaleria(): Boolean {
        fun concedido(permiso: String): Boolean {
            return ContextCompat.checkSelfPermission(
                context,
                permiso
            ) == PackageManager.PERMISSION_GRANTED
        }

        return when {
            Build.VERSION.SDK_INT >= 34 -> {
                concedido(Manifest.permission.READ_MEDIA_IMAGES) ||
                        concedido(
                            Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
                        )
            }

            Build.VERSION.SDK_INT >= 33 -> {
                concedido(Manifest.permission.READ_MEDIA_IMAGES)
            }

            else -> {
                concedido(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
    }

    val permisoGaleriaLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { _ ->
        if (tienePermisoGaleria()) {
            abrirGaleria()
        } else {
            mensaje =
                "No autorizaste el acceso a las fotos. No se cambió tu foto. " +
                        "Puedes revisar los permisos en los ajustes de PagoFlex."
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(88.dp)
                .clip(CircleShape)
                .background(colores.secondary),
            contentAlignment = Alignment.Center
        ) {
            val imagenActual = foto
            if (imagenActual != null) {
                Image(
                    bitmap = imagenActual.asImageBitmap(),
                    contentDescription = "Foto de perfil de $nombreUsuario",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Text(
                    text = nombreUsuario.take(1).uppercase().ifBlank { "U" },
                    color = colores.onSecondary,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        if (cargando || guardando) {
            CircularProgressIndicator(modifier = Modifier.size(24.dp))
        }

        BotonSecundario(
            texto = if (guardando) "Guardando foto..." else "Editar foto de perfil",
            habilitado = usuarioId > 0 && !cargando && !guardando,
            onClick = { mostrarOpciones = true },
            altura = null,
            forma = ButtonDefaults.outlinedShape,
            colores = ButtonDefaults.outlinedButtonColors(),
            borde = ButtonDefaults.outlinedButtonBorder,
            relleno = ButtonDefaults.ContentPadding
        )

        if (mensaje.isNotBlank()) {
            Text(text = mensaje, color = colores.onSurfaceVariant, fontSize = 12.sp)
        }
    }

    if (mostrarOpciones) {
        AlertDialog(
            onDismissRequest = { mostrarOpciones = false },
            title = { Text("Foto de perfil") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("La foto se convierte a JPG y se guarda en la base de datos de PagoFlex.")
                    TextButton(
                        onClick = {
                            mostrarOpciones = false
                            val permiso = ContextCompat.checkSelfPermission(
                                context, Manifest.permission.CAMERA
                            ) == PackageManager.PERMISSION_GRANTED
                            if (permiso) abrirCamara() else mostrarExplicacion = true
                        }
                    ) { Text("Tomar foto") }
                    TextButton(
                        onClick = {
                            mostrarOpciones = false

                            if (tienePermisoGaleria()) {
                                abrirGaleria()
                            } else {
                                mostrarExplicacionGaleria = true
                            }
                        }
                    ) {
                        Text("Elegir de la galería")
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { mostrarOpciones = false }) { Text("Cancelar") }
            }
        )
    }

    if (mostrarExplicacion) {
        AlertDialog(
            onDismissRequest = { mostrarExplicacion = false },
            title = { Text("Permiso de cámara") },
            text = {
                Text(
                    "PagoFlex solicita acceso a la cámara para que puedas tomar una " +
                            "foto de perfil. La imagen se guardará en tu cuenta de PagoFlex."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        mostrarExplicacion = false
                        permisoLauncher.launch(Manifest.permission.CAMERA)
                    }
                ) { Text("Continuar") }
            },
            dismissButton = {
                TextButton(onClick = { mostrarExplicacion = false }) { Text("Cancelar") }
            }
        )
    }

    if (mostrarPermisoDenegado) {
        AlertDialog(
            onDismissRequest = { mostrarPermisoDenegado = false },
            title = { Text("Cámara no autorizada") },
            text = {
                Text(
                    "No se cambió tu foto. Puedes elegir una imagen de la galería o " +
                            "revisar el permiso en los ajustes de PagoFlex."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        mostrarPermisoDenegado = false
                        try {
                            context.startActivity(
                                Intent(
                                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                    Uri.parse("package:${context.packageName}")
                                )
                            )
                        } catch (_: ActivityNotFoundException) {
                            mensaje = "Abre los ajustes del teléfono y revisa los permisos."
                        }
                    }
                ) { Text("Abrir ajustes") }
            },
            dismissButton = {
                TextButton(onClick = { mostrarPermisoDenegado = false }) { Text("Cerrar") }
            }
        )
    }
    if (mostrarExplicacionGaleria) {
        AlertDialog(
            onDismissRequest = {
                mostrarExplicacionGaleria = false
            },
            title = {
                Text("Permiso de fotos")
            },
            text = {
                Text(
                    "PagoFlex solicita permiso de acceso a tus fotos para " +
                            "actualizar tu imagen de perfil. En versiones compatibles " +
                            "puedes permitir el acceso solo a fotos seleccionadas. " +
                            "La foto que elijas se guardará en tu cuenta de PagoFlex."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        mostrarExplicacionGaleria = false

                        val permisos = when {
                            Build.VERSION.SDK_INT >= 34 -> arrayOf(
                                Manifest.permission.READ_MEDIA_IMAGES,
                                Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
                            )

                            Build.VERSION.SDK_INT >= 33 -> arrayOf(
                                Manifest.permission.READ_MEDIA_IMAGES
                            )

                            else -> arrayOf(
                                Manifest.permission.READ_EXTERNAL_STORAGE
                            )
                        }

                        permisoGaleriaLauncher.launch(permisos)
                    }
                ) {
                    Text("Continuar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        mostrarExplicacionGaleria = false
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}