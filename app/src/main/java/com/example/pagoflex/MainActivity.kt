package com.example.pagoflex

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.example.pagoflex.data.PagoFlexRepository
import com.example.pagoflex.ui.navigation.PagoFlexApp
import com.example.pagoflex.ui.theme.PagoFlexTheme
import com.example.pagoflex.viewmodel.FormularioViewModel
import com.example.pagoflex.viewmodel.PagoViewModel

class MainActivity : ComponentActivity() {

    private val formularioViewModel: FormularioViewModel by viewModels()

    private val pagoViewModel: PagoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repository = PagoFlexRepository(
            applicationContext
        )

        setContent {
            PagoFlexTheme {
                PagoFlexApp(
                    repository = repository,
                    formularioViewModel = formularioViewModel,
                    pagoViewModel = pagoViewModel
                )
            }
        }
    }
}