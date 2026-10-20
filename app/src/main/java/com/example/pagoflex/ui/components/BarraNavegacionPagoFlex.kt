package com.example.pagoflex.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

enum class SeccionPagoFlex {
    INICIO,
    PAGOS,
    HISTORIAL
}

@Composable
fun BarraNavegacionPagoFlex(
    seccionActual: SeccionPagoFlex,
    onIrAInicio: () -> Unit,
    onIrAPagos: () -> Unit,
    onIrAHistorial: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colores = MaterialTheme.colorScheme

    val coloresItems = NavigationBarItemDefaults.colors(
        selectedIconColor = colores.onSecondaryContainer,
        selectedTextColor = colores.onSurface,
        indicatorColor = colores.secondaryContainer,
        unselectedIconColor = colores.onSurfaceVariant,
        unselectedTextColor = colores.onSurfaceVariant
    )

    NavigationBar(
        modifier = modifier,
        containerColor = colores.surface,
        contentColor = colores.onSurface
    ) {
        NavigationBarItem(
            selected = seccionActual == SeccionPagoFlex.INICIO,
            onClick = {
                if (seccionActual != SeccionPagoFlex.INICIO) {
                    onIrAInicio()
                }
            },
            icon = {
                Text("🏠")
            },
            label = {
                Text("Inicio")
            },
            colors = coloresItems
        )

        NavigationBarItem(
            selected = seccionActual == SeccionPagoFlex.PAGOS,
            onClick = {
                if (seccionActual != SeccionPagoFlex.PAGOS) {
                    onIrAPagos()
                }
            },
            icon = {
                Text("📄")
            },
            label = {
                Text("Pagos")
            },
            colors = coloresItems
        )

        NavigationBarItem(
            selected = seccionActual == SeccionPagoFlex.HISTORIAL,
            onClick = {
                if (seccionActual != SeccionPagoFlex.HISTORIAL) {
                    onIrAHistorial()
                }
            },
            icon = {
                Text("🕒")
            },
            label = {
                Text("Historial")
            },
            colors = coloresItems
        )
    }
}