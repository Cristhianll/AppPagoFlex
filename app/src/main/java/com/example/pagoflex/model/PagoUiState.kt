package com.example.pagoflex.model

data class PagoUiState(
    val deudaPendiente: Boolean = true,
    val tarjetaVisaRegistrada: Boolean = true,
    val ultimosDigitosVisa: String = "4582",
    val tarjetaPrincipal: Boolean = true
)