package com.example.pagoflex

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.*
import com.example.pagoflex.ui.screens.DashboardScreen
import com.example.pagoflex.ui.screens.DetallePagoScreen
import com.example.pagoflex.ui.screens.FormularioScreen
import com.example.pagoflex.ui.screens.HistorialScreen
import com.example.pagoflex.ui.theme.PagoFlexTheme
import com.example.pagoflex.viewmodel.FormularioViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: FormularioViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PagoFlexTheme {
                val uiState by viewModel.uiState.collectAsState()

                // Variable de control de navegación: "inicio", "detallePago", "historial"
                var pantallaActual by remember { mutableStateOf("inicio") }

                if (!uiState.registroExitoso) {
                    FormularioScreen(viewModel = viewModel)
                } else {
                    when (pantallaActual) {
                        "detallePago" -> {
                            DetallePagoScreen(
                                onVolverAtras = { pantallaActual = "inicio" },
                                onConfirmarPago = { pantallaActual = "inicio" }
                            )
                        }
                        "historial" -> {
                            HistorialScreen(
                                onVolverInicio = { pantallaActual = "inicio" },
                                onIrAPagos = { pantallaActual = "detallePago" }
                            )
                        }
                        else -> {
                            DashboardScreen(
                                nombreUsuario = uiState.nombre,
                                onVolverAlLogin = {
                                    pantallaActual = "inicio"
                                    viewModel.onLimpiar()
                                },
                                onIrADetallePago = {
                                    pantallaActual = "detallePago"
                                }, {
                                    pantallaActual = "detallePago"
                                },
                                onIrHistorial = {
                                    pantallaActual = "historial"
                                }
                            )


                        }
                    }
                }
            }
        }
    }
}