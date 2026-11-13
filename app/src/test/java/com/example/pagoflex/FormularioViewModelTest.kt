package com.example.pagoflex

import com.example.pagoflex.viewmodel.FormularioViewModel
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Pruebas unitarias del FormularioViewModel (registro)
 * Patrón: Preparación → Ejecución → Verificación
 */
class FormularioViewModelTest {

    private lateinit var viewModel: FormularioViewModel

    @Before
    fun setUp() {
        // 1.- Preparación
        viewModel = FormularioViewModel()
    }

    @Test
    fun onNombreCambia_actualizaNombre() {
        // 1.- Preparación
        val nombreEsperado = "Emilia Zamora"

        // 2.- Ejecución
        viewModel.onNombreCambia(nombreEsperado)

        // 3.- Verificación
        assertEquals(nombreEsperado, viewModel.uiState.value.nombre)
        assertNull(viewModel.uiState.value.error)
    }

    @Test
    fun onRutCambia_actualizaRut() {
        // 1.- Preparación
        val rutEsperado = "12345678-5"

        // 2.- Ejecución
        viewModel.onRutCambia(rutEsperado)

        // 3.- Verificación
        assertEquals(rutEsperado, viewModel.uiState.value.rut)
    }

    @Test
    fun onCorreoCambia_actualizaCorreo() {
        // 1.- Preparación
        val correoEsperado = "nuevo@pagoflex.cl"

        // 2.- Ejecución
        viewModel.onCorreoCambia(correoEsperado)

        // 3.- Verificación
        assertEquals(correoEsperado, viewModel.uiState.value.correo)
    }

    @Test
    fun onContrasenaCambia_actualizaContrasena() {
        // 1.- Preparación
        val claveEsperada = "Clave123$"

        // 2.- Ejecución
        viewModel.onContrasenaCambia(claveEsperada)

        // 3.- Verificación
        assertEquals(claveEsperada, viewModel.uiState.value.contrasena)
        assertFalse(viewModel.uiState.value.registroExitoso)
    }

    @Test
    fun estadoInicial_camposVaciosYSinError() {
        // 1.- Preparación (ya en setUp)

        // 2.- Ejecución → solo leer estado inicial

        // 3.- Verificación
        assertEquals("", viewModel.uiState.value.nombre)
        assertEquals("", viewModel.uiState.value.rut)
        assertEquals("", viewModel.uiState.value.correo)
        assertEquals("", viewModel.uiState.value.contrasena)
        assertNull(viewModel.uiState.value.error)
        assertFalse(viewModel.uiState.value.registroExitoso)
        assertFalse(viewModel.uiState.value.cargando)
    }
}