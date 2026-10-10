package com.example.pagoflex

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.*
import com.example.pagoflex.ui.screens.DashboardScreen
import com.example.pagoflex.ui.screens.DetallePagoScreen
import com.example.pagoflex.ui.screens.FormularioScreen
import com.example.pagoflex.ui.theme.PagoFlexTheme
import com.example.pagoflex.viewmodel.FormularioViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: FormularioViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PagoFlexTheme {
                val uiState by viewModel.uiState.collectAsState()

                // Declaramos el estado para alternar entre el Dashboard y el Detalle de Pago
                var verDetallePago by remember { mutableStateOf(false) }

                if (!uiState.registroExitoso) {
                    // Muestra el formulario si aún no se ha registrado
                    FormularioScreen(viewModel = viewModel)
                } else if (verDetallePago) {
                    // Muestra la pantalla de Detalle de Pago al presionar "Pagar ahora"
                    DetallePagoScreen(
                        onVolverAtras = { verDetallePago = false },
                        onConfirmarPago = {
                            // Al confirmar el pago, volvemos al dashboard
                            verDetallePago = false
                        }
                    )
                } else {
                    // Muestra el Dashboard principal
                    DashboardScreen(
                        nombreUsuario = uiState.nombre,
                        onVolverAlLogin = {
                            verDetallePago = false
                            viewModel.onLimpiar()
                        },
                        onIrADetallePago = {
                            verDetallePago = true
                        }
                    )
                }
            }
        }
    }
}