package com.example.pagoflex

import com.example.pagoflex.viewmodel.LoginViewModel
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class LoginViewModelTest {

    private lateinit var viewModel: LoginViewModel

    @Before
    fun preparar() {
        viewModel = LoginViewModel()
    }

    @Test
    fun onCorreoCambia_conCorreoValido_actualizaCorreoEnUiState() {
        val correoEsperado = "emilia@pagoflex.cl"

        viewModel.onCorreoCambia(correoEsperado)

        assertEquals(correoEsperado, viewModel.uiState.value.correo)
    }

    @Test
    fun onContrasenaCambia_conContrasenaValida_actualizaContrasenaEnUiState() {
        val claveEsperada = "Emilia123$"

        viewModel.onContrasenaCambia(claveEsperada)

        assertEquals(claveEsperada, viewModel.uiState.value.contrasena)
    }

    @Test
    fun iniciarSesion_conCamposVacios_muestraMensajeDeError() {
        viewModel.iniciarSesion()

        assertEquals("Completa correo y contraseña", viewModel.uiState.value.error)
    }
}