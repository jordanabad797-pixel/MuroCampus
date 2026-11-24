package com.tecsup.murocampus.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tecsup.murocampus.model.Comentario
import com.tecsup.murocampus.model.Post
import com.tecsup.murocampus.ui.theme.AzulMuro
import com.tecsup.murocampus.ui.theme.Blanco
import com.tecsup.murocampus.ui.theme.BordeSuave
import com.tecsup.murocampus.ui.theme.FondoTarjeta
import com.tecsup.murocampus.ui.theme.TextoCuerpo
import com.tecsup.murocampus.ui.theme.TextoTitulo
import com.tecsup.murocampus.viewmodel.PostsViewModel
import com.tecsup.murocampus.viewmodel.UiState

@Composable
fun PantallaDetalle(id: Int, vm: PostsViewModel, modifier: Modifier = Modifier) {
    val estado by vm.detalle.collectAsStateWithLifecycle()

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
