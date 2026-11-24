package com.tecsup.murocampus.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.tecsup.murocampus.model.Comentario
import com.tecsup.murocampus.model.Post
import com.tecsup.murocampus.ui.theme.AzulMuro
import com.tecsup.murocampus.ui.theme.Blanco
import com.tecsup.murocampus.ui.theme.BordeSuave
import com.tecsup.murocampus.ui.theme.FondoError
import com.tecsup.murocampus.ui.theme.FondoTarjeta
import com.tecsup.murocampus.ui.theme.TextoCuerpo
import com.tecsup.murocampus.ui.theme.TextoError
import com.tecsup.murocampus.ui.theme.TextoTitulo
import com.tecsup.murocampus.viewmodel.PostsViewModel
import com.tecsup.murocampus.viewmodel.UiState

@Composable
fun PantallaDetalle(id: Int, vm: PostsViewModel, modifier: Modifier = Modifier) {
    val estado by vm.detalle.collectAsStateWithLifecycle()
    val borrado by vm.borrado.collectAsStateWithLifecycle()
    var confirmar by rememberSaveable { mutableStateOf(false) }

    when (val e = estado) {
        is UiState.Cargando -> Cargando(modifier, texto = "Cargando publicación…")
        is UiState.Error -> ErrorConReintento(
            mensaje = e.mensaje,
            onReintentar = { vm.abrir(id) },
            modifier = modifier
        )
        is UiState.Exito -> LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { TarjetaDetalle(e.datos.post) }
            item {
                AccionEliminar(
                    estado = borrado,
                    onEliminar = { confirmar = true }
                )
            }
            item {
                Text(
                    text = "Comentarios (${e.datos.comentarios.size})",
                    color = AzulMuro,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            items(e.datos.comentarios, key = { it.id }) { comentario ->
                TarjetaComentario(comentario)
            }
        }
    }

    if (confirmar) {
        AlertDialog(
            onDismissRequest = { confirmar = false },
            title = { Text("Eliminar publicación") },
            text = { Text("¿Seguro que quieres eliminar esta publicación?") },
            confirmButton = {
                TextButton(onClick = {
                    confirmar = false
                    vm.borrar(id)
                }) {
                    Text(text = "Eliminar", color = TextoError, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmar = false }) {
                    Text(text = "Cancelar", color = AzulMuro)
                }
            },
            containerColor = Blanco
        )
    }
}

@Composable
private fun AccionEliminar(estado: UiState<Int>?, onEliminar: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (estado is UiState.Cargando) {
                CircularProgressIndicator(
                    color = TextoError,
                    strokeWidth = 3.dp,
                    modifier = Modifier
                        .padding(end = 12.dp)
                        .size(24.dp)
                )
            }
            Button(
                onClick = onEliminar,
                enabled = estado !is UiState.Cargando,
                colors = ButtonDefaults.buttonColors(
                    containerColor = TextoError,
                    contentColor = Blanco
                )
            ) {
                Text(text = "Eliminar", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
        }
        if (estado is UiState.Error) {
            Text(
                text = estado.mensaje,
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
private fun TarjetaDetalle(post: Post) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Blanco, RoundedCornerShape(14.dp))
            .border(1.dp, BordeSuave, RoundedCornerShape(14.dp))
            .padding(16.dp)
    ) {
        Text(
            text = post.title,
            color = TextoTitulo,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(10.dp))
        Text(
            text = post.body,
            color = TextoCuerpo,
            fontSize = 14.sp
        )
    }
}

@Composable
private fun TarjetaComentario(comentario: Comentario) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(FondoTarjeta, RoundedCornerShape(10.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Text(
            text = comentario.email,
            color = AzulMuro,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = comentario.body,
            color = TextoCuerpo,
            fontSize = 13.sp
        )
    }
}
