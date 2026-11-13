package com.example.pagoflex

import com.example.pagoflex.viewmodel.LoginViewModel
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Pruebas unitarias del LoginViewModel
 * Patrón: Preparación → Ejecución → Verificación
 */
class LoginViewModelTest {

    private lateinit var viewModel: LoginViewModel

    // @Before → se ejecuta automáticamente antes de cada test
    @Before
    fun setUp() {
        // 1.- Preparación
        viewModel = LoginViewModel()
    }

    @Test
    fun onCorreoCambia_actualizaCorreoEnUiState() {
        // 1.- Preparación
        val correoEsperado = "emilia@pagoflex.cl"

        // 2.- Ejecución
        viewModel.onCorreoCambia(correoEsperado)

        // 3.- Verificación
        assertEquals(correoEsperado, viewModel.uiState.value.correo)
        assertNull(viewModel.uiState.value.error)
    }

    @Test
    fun onContrasenaCambia_actualizaContrasenaEnUiState() {
        // 1.- Preparación
        val claveEsperada = "Emilia123$"

        // 2.- Ejecución
        viewModel.onContrasenaCambia(claveEsperada)

        // 3.- Verificación
        assertEquals(claveEsperada, viewModel.uiState.value.contrasena)
    }

    @Test
    fun iniciarSesion_conCamposVacios_muestraError() {
        // 1.- Preparación (campos vacíos por defecto)

        // 2.- Ejecución
        viewModel.iniciarSesion()

        // 3.- Verificación
        assertEquals("Completa correo y contraseña", viewModel.uiState.value.error)
        assertFalse(viewModel.uiState.value.cargando)
        assertNull(viewModel.uiState.value.loginResponse)
    }

    @Test
    fun iniciarSesion_soloCorreo_muestraError() {
        // 1.- Preparación
        viewModel.onCorreoCambia("emilia@pagoflex.cl")
        // contraseña sigue vacía

        // 2.- Ejecución
        viewModel.iniciarSesion()

        // 3.- Verificación
        assertEquals("Completa correo y contraseña", viewModel.uiState.value.error)
        assertNull(viewModel.uiState.value.loginResponse)
    }

    @Test
    fun cerrarSesion_limpiaEstado() {
        // 1.- Preparación
        viewModel.onCorreoCambia("emilia@pagoflex.cl")
        viewModel.onContrasenaCambia("Emilia123$")

        // 2.- Ejecución
        viewModel.cerrarSesion()

        // 3.- Verificación
        assertEquals("", viewModel.uiState.value.correo)
        assertEquals("", viewModel.uiState.value.contrasena)
        assertNull(viewModel.uiState.value.loginResponse)
        assertNull(viewModel.uiState.value.error)
    }
}