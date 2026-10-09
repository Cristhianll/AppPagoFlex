package com.example.pagoflex

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.pagoflex.ui.screens.FormularioScreen
import com.example.pagoflex.ui.theme.PagoFlexTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Permite dibujar la interfaz hasta los bordes de la pantalla.
        enableEdgeToEdge()

        setContent {
            // Selecciona la paleta clara u oscura según el dispositivo.
            PagoFlexTheme {
                FormularioScreen()
            }
        }
    }
}