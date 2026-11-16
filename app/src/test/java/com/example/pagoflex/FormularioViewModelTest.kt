package com.example.pagoflex

import com.example.pagoflex.viewmodel.FormularioViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test

class FormularioViewModelTest {

    private lateinit var viewModel: FormularioViewModel

    @Before
    fun preparar() {
        viewModel = FormularioViewModel()
    }

    @Test
    fun onRutCambia_conRutValido_actualizaRutEnUiState() {
        val rutEsperado = "12345678-5"

        viewModel.onRutCambia(rutEsperado)

        assertEquals(rutEsperado, viewModel.uiState.value.rut)
    }

    @Test
    fun onCorreoCambia_conCorreoNuevo_actualizaCorreoEnUiState() {
        val correoEsperado = "nuevo@pagoflex.cl"

        viewModel.onCorreoCambia(correoEsperado)

        assertEquals(correoEsperado, viewModel.uiState.value.correo)
    }

    @Test
    fun uiState_alCrearElViewModel_registroExitosoEsFalse() {
        val registroExitoso = viewModel.uiState.value.registroExitoso

        assertFalse(registroExitoso)
    }
}