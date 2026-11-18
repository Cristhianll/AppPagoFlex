package com.example.pagoflex

import com.example.pagoflex.viewmodel.DeudaViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class DeudaViewModelTest {

    private lateinit var viewModel: DeudaViewModel

    @Before
    fun preparar() {
        viewModel = DeudaViewModel()
    }

    @Test
    fun setFiltroEstado_conEstadoPendiente_actualizaFiltroEstadoEnUiState() {
        val estadoEsperado = "PENDIENTE"

        viewModel.setFiltroEstado(estadoEsperado)

        assertEquals(estadoEsperado, viewModel.uiState.value.filtroEstado)
    }

    @Test
    fun setFiltroMontoMin_conMontoMinimo_actualizaFiltroMontoMinEnUiState() {
        val montoEsperado = 10000.0

        viewModel.setFiltroMontoMin(montoEsperado)

        assertEquals(montoEsperado, viewModel.uiState.value.filtroMontoMin)
    }

    @Test
    fun uiState_alCrearElViewModel_tieneListaDeDeudasVacia() {
        val deudas = viewModel.uiState.value.deudas

        assertTrue(deudas.isEmpty())
    }
}