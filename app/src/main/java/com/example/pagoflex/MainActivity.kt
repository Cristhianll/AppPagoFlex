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
import com.example.pagoflex.ui.screens.PagoExitosoScreen
import com.example.pagoflex.ui.theme.PagoFlexTheme
import com.example.pagoflex.viewmodel.FormularioViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: FormularioViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PagoFlexTheme {
                val uiState by viewModel.uiState.collectAsState()


                var pantallaActual by remember { mutableStateOf("inicio") }
                var verPerfil by remember{mutableStateOf(false)}
                if (!uiState.registroExitoso) {
                    FormularioScreen(viewModel = viewModel)
                } else {
                    when (pantallaActual) {
                        "detallePago" -> {
                            DetallePagoScreen(
                                onVolverAtras = { pantallaActual = "inicio" },
                                onConfirmarPago = {
                                    pantallaActual = "pagoExitoso" // Al confirmar, va a la pantalla de éxito
                                }
                            )
                        }
                        "pagoExitoso" -> {
                            PagoExitosoScreen(
                                onVolverInicio = { pantallaActual = "inicio" },
                                onVerHistorial = { pantallaActual = "historial" }
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
                                onIrADetallePago = {
                                    pantallaActual = "detallePago"
                                },
                                onIrAPagos = {
                                    pantallaActual = "detallePago"
                                },
                                onIrAHistorial = {
                                    pantallaActual = "historial"
                                },
                                onAbrirPerfil = {
                                    verPerfil = true
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}