package com.tecsup.murocampus.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tecsup.murocampus.ui.theme.AzulMuro
import com.tecsup.murocampus.ui.theme.Blanco
import com.tecsup.murocampus.ui.theme.BordeCampo
import com.tecsup.murocampus.ui.theme.BordeExito
import com.tecsup.murocampus.ui.theme.FondoError
import com.tecsup.murocampus.ui.theme.FondoExito
import com.tecsup.murocampus.ui.theme.TextoError
import com.tecsup.murocampus.ui.theme.TextoExito
import com.tecsup.murocampus.ui.theme.TextoTitulo
import com.tecsup.murocampus.viewmodel.PostsViewModel
import com.tecsup.murocampus.viewmodel.UiState

@Composable
fun PantallaCrear(vm: PostsViewModel, modifier: Modifier = Modifier) {
    val estado by vm.creado.collectAsStateWithLifecycle()
    var titulo by rememberSaveable { mutableStateOf("") }
    var texto by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(estado) {
        if (estado is UiState.Exito) {
            titulo = ""
            texto = ""
        }
    }

    val enviando = estado is UiState.Cargando
    val colores = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = AzulMuro,
        unfocusedBorderColor = BordeCampo,
        focusedLabelColor = AzulMuro,
        unfocusedLabelColor = AzulMuro,
        cursorColor = AzulMuro,
        focusedTextColor = TextoTitulo,
        unfocusedTextColor = TextoTitulo
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            value = titulo,
            onValueChange = {
                titulo = it
                if (estado is UiState.Exito) vm.reiniciarCreacion()
            },
            label = { Text("Título") },
            singleLine = true,
            enabled = !enviando,
            colors = colores,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = texto,
            onValueChange = {
                texto = it
                if (estado is UiState.Exito) vm.reiniciarCreacion()
            },
            label = { Text("Contenido") },
            minLines = 4,
            enabled = !enviando,
            colors = colores,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = { vm.publicar(titulo, texto) },
            enabled = titulo.isNotBlank() && texto.isNotBlank() && !enviando,
            colors = ButtonDefaults.buttonColors(
                containerColor = AzulMuro,
                contentColor = Blanco
            )
        ) {
            Text(text = "Publicar", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }

        when (val e = estado) {
            null -> Unit
            is UiState.Cargando -> CircularProgressIndicator(
                color = AzulMuro,
                strokeWidth = 3.dp,
                modifier = Modifier.size(32.dp)
            )
            is UiState.Exito -> MensajeExito("Publicado con id ${e.datos.id}")
            is UiState.Error -> Text(
                text = e.mensaje,
                color = TextoError,
                fontSize = 15.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(FondoError, RoundedCornerShape(10.dp))
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            )
        }
    }
}

@Composable
private fun MensajeExito(texto: String) {
    Text(
        text = texto,
        color = TextoExito,
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
            .drawBehind {
                val radio = CornerRadius(10.dp.toPx())
                drawRoundRect(color = FondoExito, cornerRadius = radio)
                drawRoundRect(
                    color = BordeExito,
                    cornerRadius = radio,
                    style = Stroke(
                        width = 1.5.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(8.dp.toPx(), 5.dp.toPx()))
                    )
                )
            }
            .padding(top = 11.dp)
    )
}
