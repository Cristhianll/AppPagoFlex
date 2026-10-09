package com.example.pagoflex.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duocuc.pagoflex.ui.components.TarjetaSeccion
import com.example.pagoflex.ui.components.BotonPrincipal
import com.example.pagoflex.ui.components.BotonSecundario
import com.example.pagoflex.ui.components.CampoContrasena
import com.example.pagoflex.ui.components.CampoTexto
import com.example.pagoflex.ui.components.CasillaConTexto
import com.example.pagoflex.ui.components.GrupoRadio
import com.example.pagoflex.ui.components.ImagenEncabezado
import com.example.pagoflex.ui.components.InterruptorConTexto
import com.example.pagoflex.R
import com.example.pagoflex.model.FormularioUiState
import com.example.pagoflex.model.NivelExperiencia
import com.example.pagoflex.ui.theme.Dimens
import com.example.pagoflex.ui.theme.Exito
import com.example.pagoflex.ui.theme.ExitoFondo
import com.example.pagoflex.viewmodel.FormularioViewModel

@Composable
fun FormularioScreen(
    viewModel: FormularioViewModel = viewModel()// se crea una vez y permanece
){
    //variable para manipular el stateFlow y cada vez que cambie redibuja la interfaz grafica "UI"
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    FormularioContent(
        uiState = uiState,
        onNombreCambia = viewModel::onNombreCambia ,
        onCorreoCambia = viewModel::onCorreoCambia,
        onContrasenaCambia = viewModel::onContrasenaCambia,
        onAlternarVisibilidad = viewModel::onAlternarVisibilidad,
        onNivelSeleccionado = viewModel::onNivelSeleccionado,
        onNoticiasCambia = viewModel::onNoticiasCambia,
        onTerminoCambia = viewModel::onTerminosCambia,
        onRegistrar = viewModel::onRegistrar,
        onLimpiar = viewModel::onLimpiar // el ":" llama el evento del viewmodel
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioContent(
    uiState: FormularioUiState,
    onNombreCambia : (String) -> Unit,
    onCorreoCambia : (String) -> Unit,
    onContrasenaCambia : (String) -> Unit,
    onAlternarVisibilidad: () -> Unit,
    onNivelSeleccionado: (NivelExperiencia) -> Unit,
    onNoticiasCambia : (Boolean)-> Unit,
    onTerminoCambia: (Boolean) -> Unit,
    onRegistrar: ()-> Unit,
    onLimpiar: () -> Unit
){
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.titulo_formulario)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            ImagenEncabezado(
                imagen = R.drawable.banner_registro,
                descripcion = stringResource(R.string.cd_banner),
                titulo = stringResource(R.string.banner_titulo),
                subtitulo = stringResource(R.string.banne_subtitulo)
            )
            Column(
                modifier = Modifier.padding(Dimens.espacioPantalla),
                verticalArrangement = Arrangement.spacedBy(Dimens.espacioMedio)
            ) {
                //card 1 - datos personales
                TarjetaSeccion(titulo = stringResource(R.string.seccion_datos)) {
                    CampoTexto(
                        valor = uiState.nombre,
                        onValorCambia = onNombreCambia,
                        etiqueta = stringResource(R.string.campo_nombre),
                        icono = R.drawable.ic_persona,
                        error = uiState.errorNombre
                    )
                    CampoTexto(
                        valor = uiState.correo,
                        onValorCambia = onCorreoCambia,
                        etiqueta = stringResource(R.string.campo_correo),
                        icono = R.drawable.ic_correo,
                        error = uiState.errorCorreo,
                        tipoTeclado = KeyboardType.Email // aqui se añade el arroba directamente al teclado

                    )
                    CampoContrasena(
                        valor = uiState.contrasena,
                        onValorCambia = onContrasenaCambia,
                        etiqueta = stringResource(R.string.campo_contrasena),
                        visible = uiState.contrasenaVisible,
                        onAlternarVisible = onAlternarVisibilidad,
                        error = uiState.errorContrasena
                    )
                }
                // Card 2- Preferencias
                TarjetaSeccion(titulo = stringResource(R.string.seccion_preferencias)) {
                    Text(
                        text = stringResource(R.string.etiqueta_nivel),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    GrupoRadio(
                        opciones = NivelExperiencia.entries,
                        seleccionada = uiState.nivel,
                        onSeleccionar = onNivelSeleccionado,
                        textoDe = { it.etiqueta }
                    )
                    InterruptorConTexto(
                        texto = stringResource(R.string.opcion_noticia),
                        activado = uiState.recibirNoticias,
                        onCambio = onNoticiasCambia
                    )
                }
                //Terminos y condiciones
                Column {
                    CasillaConTexto(
                        texto = stringResource(R.string.opcion_terminos),
                        marcado = uiState.aceptaTerminos,
                        onCambio = onTerminoCambia
                    )
                    uiState.errorTerminos?.let { mensaje ->
                        Text(
                            text = mensaje,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(Dimens.espacioChico)
                        )
                    }
                }
                //botones
                Row(horizontalArrangement = Arrangement.spacedBy(Dimens.espacioChico)) {
                    BotonSecundario(
                        texto = stringResource(R.string.btn_limpiar),
                        onClick = onLimpiar,
                        modifier = Modifier.weight(1f)
                    )
                    BotonPrincipal(
                        texto = stringResource(R.string.btn_registrar),
                        onClick = onRegistrar,
                        icono = R.drawable.ic_check,
                        modifier = Modifier.weight(1f)
                    )

                }
                //mensaje de exito
                if(uiState.registroExitoso){
                    MensajeExito(nombre = uiState.nombre)
                }
                Spacer(Modifier.height(Dimens.espacioGrande))


            }
        }

    }

}

@Composable
private fun MensajeExito(nombre:String){
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = ExitoFondo, contentColor = Exito)
    ) {
        Row(
            modifier = Modifier.padding(Dimens.espacioMedio),
            verticalAlignment = Alignment.CenterVertically
        ){
            Icon(
                painter = painterResource(R.drawable.ic_check),
                contentDescription = null,
                tint = Exito,
                modifier = Modifier.size(Dimens.espacioMedio)
            )
            Text(
                text = stringResource(R.string.registro_exitoso,nombre),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(start = Dimens.espacioChico)
            )
        }
    }

}