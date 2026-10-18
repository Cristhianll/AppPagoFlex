package com.example.pagoflex.model

data class PagoUiState(
    val deudaPendiente: Boolean = false,
    val tarjetaVisaRegistrada: Boolean = true,
    val ultimosDigitosVisa: String = "4582",
    val tarjetaPrincipal: Boolean = true,
    val notificacionesActivas: Boolean = true,
    val modoOscuroAutomatico: Boolean = false,
    val biometriaActiva: Boolean = true
)