package com.example.pagoflex

import com.example.pagoflex.viewmodel.DeudaViewModel
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Pruebas unitarias del DeudaViewModel (filtros y estado)
 * Patrón: Preparación → Ejecución → Verificación
 */
class DeudaViewModelTest {

    private lateinit var viewModel: DeudaViewModel

    @Before
    fun setUp() {
        // 1.- Preparación
        viewModel = DeudaViewModel()
    }

    @Test
    fun setFiltroEstado_actualizaFiltro() {
        // 1.- Preparación
        val estadoEsperado = "PENDIENTE"

        // 2.- Ejecución
        viewModel.setFiltroEstado(estadoEsperado)

        // 3.- Verificación
        assertEquals(estadoEsperado, viewModel.uiState.value.filtroEstado)
    }

    @Test
    fun setFiltroDesde_actualizaFechaDesde() {
        // 1.- Preparación
        val desdeEsperado = "2026-01-01"

        // 2.- Ejecución
        viewModel.setFiltroDesde(desdeEsperado)

        // 3.- Verificación
        assertEquals(desdeEsperado, viewModel.uiState.value.filtroDesde)
    }

    @Test
    fun setFiltroHasta_actualizaFechaHasta() {
        // 1.- Preparación
        val hastaEsperado = "2026-12-31"

        // 2.- Ejecución
        viewModel.setFiltroHasta(hastaEsperado)

        // 3.- Verificación
        assertEquals(hastaEsperado, viewModel.uiState.value.filtroHasta)
    }

    @Test
    fun setFiltroMontoMin_actualizaMontoMinimo() {
        // 1.- Preparación
        val montoEsperado = 10000.0

        // 2.- Ejecución
        viewModel.setFiltroMontoMin(montoEsperado)

        // 3.- Verificación
        assertEquals(montoEsperado, viewModel.uiState.value.filtroMontoMin)
    }

    @Test
    fun setFiltroMontoMax_actualizaMontoMaximo() {
        // 1.- Preparación
        val montoEsperado = 50000.0

        // 2.- Ejecución
        viewModel.setFiltroMontoMax(montoEsperado)

        // 3.- Verificación
        assertEquals(montoEsperado, viewModel.uiState.value.filtroMontoMax)
    }

    @Test
    fun estadoInicial_sinFiltrosNiDeudas() {
        // 1.- Preparación (setUp)

        // 2.- Ejecución → leer estado

        // 3.- Verificación
        assertTrue(viewModel.uiState.value.deudas.isEmpty())
        assertNull(viewModel.uiState.value.filtroEstado)
        assertNull(viewModel.uiState.value.filtroDesde)
        assertNull(viewModel.uiState.value.filtroHasta)
        assertNull(viewModel.uiState.value.filtroMontoMin)
        assertNull(viewModel.uiState.value.filtroMontoMax)
        assertFalse(viewModel.uiState.value.cargando)
        assertNull(viewModel.uiState.value.error)
    }
}