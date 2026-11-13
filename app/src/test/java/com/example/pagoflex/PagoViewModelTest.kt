package com.example.pagoflex

import com.example.pagoflex.viewmodel.PagoViewModel
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Pruebas unitarias del PagoViewModel
 * Patrón: Preparación → Ejecución → Verificación
 */
class PagoViewModelTest {

    private lateinit var viewModel: PagoViewModel

    @Before
    fun setUp() {
        // 1.- Preparación
        viewModel = PagoViewModel()
    }

    @Test
    fun estadoInicial_listasVaciasSinError() {
        // 1.- Preparación (setUp)

        // 2.- Ejecución → leer estado inicial

        // 3.- Verificación
        assertTrue(viewModel.uiState.value.pagos.isEmpty())
        assertTrue(viewModel.uiState.value.tarjetas.isEmpty())
        assertFalse(viewModel.uiState.value.cargando)
        assertFalse(viewModel.uiState.value.pagoExitoso)
        assertNull(viewModel.uiState.value.error)
    }

    @Test
    fun estadoInicial_pagoExitosoEsFalse() {
        // 1.- Preparación
        // 2.- Ejecución
        val resultado = viewModel.uiState.value.pagoExitoso

        // 3.- Verificación
        assertFalse(resultado)
    }

    @Test
    fun estadoInicial_cargandoEsFalse() {
        // 1.- Preparación
        // 2.- Ejecución
        val cargando = viewModel.uiState.value.cargando

        // 3.- Verificación
        assertFalse(cargando)
    }

    @Test
    fun estadoInicial_errorEsNull() {
        // 1.- Preparación
        // 2.- Ejecución
        val error = viewModel.uiState.value.error

        // 3.- Verificación
        assertNull(error)
    }
}