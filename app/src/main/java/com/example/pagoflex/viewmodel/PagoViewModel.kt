package com.example.pagoflex.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.pagoflex.model.PagoUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class PagoViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val preferencias = application.getSharedPreferences(
        "pagoflex_tarjetas_demo",
        android.content.Context.MODE_PRIVATE
    )

    private val _uiState = MutableStateFlow(PagoUiState())
    val uiState: StateFlow<PagoUiState> = _uiState.asStateFlow()

    private var usuarioActualId: Int? = null

    fun cargarUsuario(usuarioId: Int) {
        if (usuarioId <= 0) {
            limpiarSesion()
            return
        }

        usuarioActualId = usuarioId

        val prefijo = "usuario_${usuarioId}_"

        val digitosGuardados = preferencias.getString(
            "${prefijo}ultimos_digitos",
            ""
        ).orEmpty()

        val registrada = digitosGuardados.matches(
            Regex("[0-9]{4}")
        )

        _uiState.value = PagoUiState(
            tarjetaVisaRegistrada = registrada,
            ultimosDigitosVisa = if (registrada) {
                digitosGuardados
            } else {
                ""
            },
            tarjetaPrincipal = registrada,
            titularTarjeta = if (registrada) {
                preferencias.getString(
                    "${prefijo}titular",
                    ""
                ).orEmpty()
            } else {
                ""
            },
            vencimientoTarjeta = if (registrada) {
                preferencias.getString(
                    "${prefijo}vencimiento",
                    ""
                ).orEmpty()
            } else {
                ""
            }
        )
    }

    fun vincularTarjeta(
        ultimosDigitos: String,
        titular: String,
        vencimiento: String
    ): Boolean {
        val usuarioId = usuarioActualId ?: return false

        if (
            !ultimosDigitos.matches(Regex("[0-9]{4}")) ||
            titular.isBlank() ||
            vencimiento.isBlank()
        ) {
            return false
        }

        val prefijo = "usuario_${usuarioId}_"

        preferencias.edit()
            .putString(
                "${prefijo}ultimos_digitos",
                ultimosDigitos
            )
            .putString(
                "${prefijo}titular",
                titular.trim()
            )
            .putString(
                "${prefijo}vencimiento",
                vencimiento
            )
            .apply()

        _uiState.update {
            it.copy(
                tarjetaVisaRegistrada = true,
                ultimosDigitosVisa = ultimosDigitos,
                tarjetaPrincipal = true,
                titularTarjeta = titular.trim(),
                vencimientoTarjeta = vencimiento
            )
        }

        return true
    }

    fun limpiarSesion() {
        usuarioActualId = null
        _uiState.value = PagoUiState()
    }

    fun confirmarPago() {
        _uiState.update {
            it.copy(deudaPendiente = false)
        }
    }

    fun reiniciarPago() {
        _uiState.update {
            it.copy(deudaPendiente = true)
        }
    }

    fun alternarNotificaciones() {
        _uiState.update {
            it.copy(
                notificacionesActivas = !it.notificacionesActivas
            )
        }
    }

    fun alternarModoOscuro() {
        _uiState.update {
            it.copy(
                modoOscuroAutomatico = !it.modoOscuroAutomatico
            )
        }
    }

    fun alternarBiometria() {
        _uiState.update {
            it.copy(
                biometriaActiva = !it.biometriaActiva
            )
        }
    }
}