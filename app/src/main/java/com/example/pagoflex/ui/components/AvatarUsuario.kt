package com.example.pagoflex.ui.components

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pagoflex.data.PagoFlexRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun AvatarUsuario(
    usuarioId: Int,
    nombre: String,
    repository: PagoFlexRepository,
    tamano: Dp = 36.dp,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val colores = MaterialTheme.colorScheme
    val foto by FotoPerfilState.fotoActual.collectAsState()

    LaunchedEffect(usuarioId) {
        if (FotoPerfilState.necesitaCargar(usuarioId)) {
            try {
                val cargada = withContext(Dispatchers.IO) {
                    val remota = try {
                        repository.obtenerFoto(usuarioId)
                    } catch (_: Exception) {
                        null
                    }

                    if (remota != null && remota.isNotEmpty()) {
                        FotoPerfilState.archivoLocal(context, usuarioId).writeBytes(remota)
                        BitmapFactory.decodeByteArray(remota, 0, remota.size)
                    } else {
                        FotoPerfilState.cargarDesdeDisco(context, usuarioId)
                    }
                }
                if (cargada != null) {
                    FotoPerfilState.actualizar(usuarioId, cargada)
                }
            } catch (c: CancellationException) {
                throw c
            } catch (_: Exception) {
                // Conserva la inicial si falla
            }
        }
    }

    Box(
        modifier = modifier
            .size(tamano)
            .clip(CircleShape)
            .background(colores.secondary),
        contentAlignment = Alignment.Center
    ) {
        val imagen = foto
        if (imagen != null) {
            Image(
                bitmap = imagen.asImageBitmap(),
                contentDescription = "Foto de perfil de $nombre",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Text(
                text = nombre.trim().take(1).uppercase().ifBlank { "U" },
                color = colores.onSecondary,
                fontWeight = FontWeight.Bold,
                fontSize = (tamano.value * 0.42f).sp
            )
        }
    }
}