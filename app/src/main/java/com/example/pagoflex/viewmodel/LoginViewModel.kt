package com.example.pagoflex.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pagoflex.data.remote.LoginRequest
import com.example.pagoflex.data.remote.LoginResponse
import com.example.pagoflex.data.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginUiState(
    val correo: String = "",
    val contrasena: String = "",
    val cargando: Boolean = false,
    val error: String? = null,
    val loginResponse: LoginResponse? = null
)

class LoginViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onCorreoCambia(v: String) = _uiState.update { it.copy(correo = v, error = null) }
    fun onContrasenaCambia(v: String) = _uiState.update { it.copy(contrasena = v, error = null) }

    fun iniciarSesion() {
        val s = _uiState.value
        if (s.correo.isBlank() || s.contrasena.isBlank()) {
            _uiState.update { it.copy(error = "Completa correo y contraseña") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(cargando = true, error = null) }
            try {
                val response = RetrofitClient.usuarioApi.login(
                    LoginRequest(s.correo.trim(), s.contrasena)
                )
                // Guardar token para las siguientes peticiones
                RetrofitClient.token = response.token
                _uiState.update { it.copy(cargando = false, loginResponse = response) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(cargando = false, error = e.message ?: "Error de conexión")
                }
            }
        }
    }

    fun cerrarSesion() {
        RetrofitClient.token = null
        _uiState.value = LoginUiState()
    }
}