package com.example.pagoflex.model

data class PagoUiState(
    val deudaPendiente: Boolean = false,
    val tarjetaVisaRegistrada: Boolean = false,
    val ultimosDigitosVisa: String = "",
    val tarjetaPrincipal: Boolean = false,
    val titularTarjeta: String = "",
    val vencimientoTarjeta: String = "",
    val notificacionesActivas: Boolean = true,
    val modoOscuroAutomatico: Boolean = false,
    val biometriaActiva: Boolean = true
)