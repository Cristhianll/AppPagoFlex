package com.example.pagoflex

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.example.pagoflex.data.PagoFlexRepository
import com.example.pagoflex.data.SesionPreferencias
import com.example.pagoflex.data.remote.RetrofitClient
import com.example.pagoflex.ui.navigation.PagoFlexApp
import com.example.pagoflex.ui.theme.PagoFlexTheme
import com.example.pagoflex.viewmodel.FormularioViewModel
import com.example.pagoflex.viewmodel.PagoViewModel

class MainActivity : ComponentActivity() {

    private val formularioViewModel: FormularioViewModel by viewModels()
    private val pagoViewModel: PagoViewModel by viewModels()

    private val solicitarRedLocal =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { }

    private fun pedirPermisoRedLocal() {
        if (Build.VERSION.SDK_INT < 37) return
        val permiso = "android.permission.ACCESS_LOCAL_NETWORK"
        val concedido = ContextCompat.checkSelfPermission(this, permiso) ==
                PackageManager.PERMISSION_GRANTED
        if (!concedido) solicitarRedLocal.launch(permiso)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        pedirPermisoRedLocal()

        val sesion = SesionPreferencias(this)
        sesion.token()?.let { RetrofitClient.token = it }

        val repository = PagoFlexRepository(this)

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