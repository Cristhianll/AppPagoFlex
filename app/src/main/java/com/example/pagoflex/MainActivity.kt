package com.example.pagoflex
import com.example.pagoflex.ui.screens.DashboardScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pagoflex.ui.screens.FormularioScreen
import com.example.pagoflex.ui.theme.PagoFlexTheme
import com.example.pagoflex.viewmodel.FormularioViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            PagoFlexTheme {
                // Instanciamos el ViewModel de manera compartida para la actividad
                val viewModel: FormularioViewModel = viewModel()
                val uiState by viewModel.uiState.collectAsState()

                // Evaluamos si el registro fue exitoso para cambiar de pantalla
                if (uiState.registroExitoso) {
                    DashboardScreen(
                        nombreUsuario = uiState.nombre,
                        onVolverAlLogin = { viewModel.onLimpiar() } // Vuelve al formulario si cierran sesión
                    )
                } else {
                    FormularioScreen(viewModel = viewModel)
                }
            }
        }
    }
}