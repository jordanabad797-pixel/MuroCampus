package com.tecsup.murocampus.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tecsup.murocampus.model.Post
import com.tecsup.murocampus.ui.theme.AzulMuro
import com.tecsup.murocampus.ui.theme.Blanco
import com.tecsup.murocampus.ui.theme.BordeCampo
import com.tecsup.murocampus.ui.theme.BordeSuave
import com.tecsup.murocampus.ui.theme.FondoError
import com.tecsup.murocampus.ui.theme.TextoCuerpo
import com.tecsup.murocampus.ui.theme.TextoError
import com.tecsup.murocampus.ui.theme.TextoTitulo
import com.tecsup.murocampus.viewmodel.PostsViewModel
import com.tecsup.murocampus.viewmodel.UiState

@Composable
fun PantallaEditar(
    id: Int,
    tituloActual: String,
    vm: PostsViewModel,
    modifier: Modifier = Modifier
) {
    val estado by vm.editado.collectAsStateWithLifecycle()
    var titulo by rememberSaveable(id) { mutableStateOf(tituloActual) }
    val enviando = estado is UiState.Cargando

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Editar publicación $id",
            color = AzulMuro,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = titulo,
            onValueChange = {
                titulo = it
                if (estado is UiState.Exito) vm.reiniciarEdicion()
            },
            label = { Text("Título") },
            minLines = 2,
            enabled = !enviando,
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AzulMuro,
                unfocusedBorderColor = BordeCampo,
                focusedLabelColor = AzulMuro,
                unfocusedLabelColor = AzulMuro,
                cursorColor = AzulMuro,
                focusedTextColor = TextoTitulo,
                unfocusedTextColor = TextoTitulo
            ),
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = { vm.editarTitulo(id, titulo) },
            enabled = titulo.isNotBlank() && titulo.trim() != tituloActual && !enviando,
            colors = ButtonDefaults.buttonColors(
                containerColor = AzulMuro,
                contentColor = Blanco
            )
        ) {
            Text(text = "Guardar cambios", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }

        when (val e = estado) {
            null -> Unit
            is UiState.Cargando -> CircularProgressIndicator(
                color = AzulMuro,
                strokeWidth = 3.dp,
                modifier = Modifier.size(32.dp)
            )
            is UiState.Exito -> {
                MensajeExito("Publicación ${e.datos.id} actualizada con PATCH")
                RespuestaServidor(e.datos)
            }
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
private fun RespuestaServidor(post: Post) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Blanco, RoundedCornerShape(14.dp))
            .border(1.dp, BordeSuave, RoundedCornerShape(14.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = "Respuesta del servidor",
            color = AzulMuro,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )
        Text(text = "id: ${post.id} · userId: ${post.userId}", color = TextoCuerpo, fontSize = 14.sp)
        Text(text = "title: ${post.title}", color = TextoTitulo, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        Text(text = "body: ${post.body}", color = TextoCuerpo, fontSize = 13.sp, maxLines = 3)
    }
}
