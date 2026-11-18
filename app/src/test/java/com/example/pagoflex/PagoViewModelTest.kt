package com.example.pagoflex

import com.example.pagoflex.viewmodel.PagoUiState
import com.example.pagoflex.viewmodel.PagoViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class PagoViewModelTest {

    private lateinit var viewModel: PagoViewModel

    @Before
    fun preparar() {
        viewModel = PagoViewModel()
    }

    @Test
    fun uiState_alCrearElViewModel_noTienePagosNiTarjetas() {
        val estado = viewModel.uiState.value

        assertTrue(estado.pagos.isEmpty())
        assertTrue(estado.tarjetas.isEmpty())
    }

    @Test
    fun uiState_alCrearElViewModel_noTieneOperacionEnCursoNiResultadoPrevio() {
        val estado = viewModel.uiState.value

        assertFalse(estado.cargando)
        assertFalse(estado.pagoExitoso)
        assertNull(estado.error)
    }

    @Test
    fun alternarNotificaciones_conNotificacionesActivas_lasDesactiva() {
        viewModel.alternarNotificaciones()

        assertFalse(viewModel.uiState.value.notificacionesActivas)
    }
}