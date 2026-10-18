package com.example.pagoflex.ui.components

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.Settings
import android.util.AtomicFile
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
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

@Composable
fun FotoPerfilEditable(
    usuarioId: Int,
    nombreUsuario: String
) {
    val context = LocalContext.current
    val colores = MaterialTheme.colorScheme
    val scope = rememberCoroutineScope()

    var foto by remember(usuarioId) {
        mutableStateOf<Bitmap?>(null)
    }

    var cargando by remember(usuarioId) {
        mutableStateOf(true)
    }

    var guardando by remember(usuarioId) {
        mutableStateOf(false)
    }

    var mensaje by remember(usuarioId) {
        mutableStateOf("")
    }

    var mostrarExplicacion by remember {
        mutableStateOf(false)
    }

    var mostrarPermisoDenegado by remember {
        mutableStateOf(false)
    }

    val archivoFoto = remember(usuarioId, context) {
        File(
            context.filesDir,
            "foto_perfil_usuario_$usuarioId.jpg"
        )
    }

    // Carga la foto que el usuario haya guardado anteriormente.
    LaunchedEffect(usuarioId, archivoFoto) {
        cargando = true

        try {
            foto = withContext(Dispatchers.IO) {
                if (archivoFoto.exists()) {
                    BitmapFactory.decodeFile(
                        archivoFoto.absolutePath
                    )
                } else {
                    null
                }
            }
        } catch (cancelacion: CancellationException) {
            throw cancelacion
        } catch (_: Exception) {
            mensaje = "No se pudo cargar la foto de perfil."
        } finally {
            cargando = false
        }
    }

    // Recibe una imagen pequeña desde la aplicación de cámara.
    val camaraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { imagen ->
        if (imagen == null) {
            mensaje = "No se tomó una nueva foto."
        } else {
            scope.launch {
                guardando = true

                try {
                    withContext(Dispatchers.IO) {
                        val archivoAtomico = AtomicFile(archivoFoto)
                        val salida = archivoAtomico.startWrite()

                        try {
                            val guardada = imagen.compress(
                                Bitmap.CompressFormat.JPEG,
                                90,
                                salida
                            )

                            if (!guardada) {
                                error("No se pudo comprimir la foto.")
                            }

                            archivoAtomico.finishWrite(salida)
                        } catch (error: Exception) {
                            archivoAtomico.failWrite(salida)
                            throw error
                        }
                    }

                    foto = imagen
                    mensaje = "Foto de perfil actualizada."
                } catch (cancelacion: CancellationException) {
                    throw cancelacion
                } catch (_: Exception) {
                    mensaje = "No se pudo guardar la foto. Intenta nuevamente."
                } finally {
                    guardando = false
                }
            }
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

    // Recibe la respuesta del usuario al permiso de Android.
    val permisoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { concedido ->
        if (concedido) {
            abrirCamara()
        } else {
            mostrarPermisoDenegado = true
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
                    text = nombreUsuario
                        .take(1)
                        .uppercase()
                        .ifBlank { "U" },
                    color = colores.onSecondary,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        if (cargando || guardando) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp)
            )
        }

        OutlinedButton(
            enabled = usuarioId > 0 && !cargando && !guardando,
            onClick = {
                val permisoConcedido =
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.CAMERA
                    ) == PackageManager.PERMISSION_GRANTED

                if (permisoConcedido) {
                    abrirCamara()
                } else {
                    mostrarExplicacion = true
                }
            }
        ) {
            Text(
                text = if (guardando) {
                    "Guardando foto..."
                } else {
                    "Editar foto de perfil"
                }
            )
        }

        if (mensaje.isNotBlank()) {
            Text(
                text = mensaje,
                color = colores.onSurfaceVariant,
                fontSize = 12.sp
            )
        }
    }

    if (mostrarExplicacion) {
        AlertDialog(
            onDismissRequest = {
                mostrarExplicacion = false
            },
            title = {
                Text("Permiso de cámara")
            },
            text = {
                Text(
                    text = "PagoFlex solicita acceso a la cámara para " +
                            "que puedas tomar una foto de perfil. " +
                            "La imagen se guardará en este dispositivo."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        mostrarExplicacion = false

                        permisoLauncher.launch(
                            Manifest.permission.CAMERA
                        )
                    }
                ) {
                    Text("Continuar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        mostrarExplicacion = false
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    if (mostrarPermisoDenegado) {
        AlertDialog(
            onDismissRequest = {
                mostrarPermisoDenegado = false
            },
            title = {
                Text("Cámara no autorizada")
            },
            text = {
                Text(
                    text = "No se cambió tu foto. Puedes seguir usando " +
                            "la aplicación sin autorizar la cámara.\n\n" +
                            "Si Android ya no muestra la solicitud, " +
                            "puedes revisar el permiso en los ajustes de PagoFlex."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        mostrarPermisoDenegado = false

                        try {
                            val intent = Intent(
                                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.parse(
                                    "package:${context.packageName}"
                                )
                            )

                            context.startActivity(intent)
                        } catch (_: ActivityNotFoundException) {
                            mensaje = "Abre los ajustes del teléfono y " +
                                    "revisa los permisos de PagoFlex."
                        }
                    }
                ) {
                    Text("Abrir ajustes")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        mostrarPermisoDenegado = false
                    }
                ) {
                    Text("Cerrar")
                }
            }
        )
    }
}