package com.tecsup.murocampus.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.background
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tecsup.murocampus.ui.theme.AzulMuro
import com.tecsup.murocampus.ui.theme.Blanco
import com.tecsup.murocampus.ui.theme.BordeCampo
import com.tecsup.murocampus.ui.theme.BordeSuave
import com.tecsup.murocampus.ui.theme.FondoError
import com.tecsup.murocampus.ui.theme.TextoCarga
import com.tecsup.murocampus.ui.theme.TextoError
import com.tecsup.murocampus.ui.theme.TextoTitulo
import com.tecsup.murocampus.viewmodel.PostsViewModel
import com.tecsup.murocampus.viewmodel.UiState

@Composable
fun PantallaLista(
    vm: PostsViewModel,
    onAbrir: (Int) -> Unit,
    modifier: Modifier = Modifier,
    estadoLista: LazyListState = rememberLazyListState()
) {
    val estado by vm.visibles.collectAsStateWithLifecycle()
    val usuarioFiltro by vm.usuarioFiltro.collectAsStateWithLifecycle()
    val busqueda by vm.busqueda.collectAsStateWithLifecycle()
    val hayMas by vm.hayMas.collectAsStateWithLifecycle()
    val cargandoMas by vm.cargandoMas.collectAsStateWithLifecycle()
    val errorMas by vm.errorMas.collectAsStateWithLifecycle()

    Column(modifier = modifier.fillMaxSize()) {
        CampoBusqueda(
            texto = busqueda,
            onCambiar = {
                estadoLista.requestScrollToItem(0)
                vm.buscar(it)
            }
        )
        FilaFiltros(
            seleccionado = usuarioFiltro,
            onElegir = { userId ->
                if (userId != usuarioFiltro) {
                    estadoLista.requestScrollToItem(0)
                    vm.filtrar(userId)
                }
            }
        )

        val contenido = Modifier
            .fillMaxWidth()
            .weight(1f)

        when (val e = estado) {
            is UiState.Cargando -> Cargando(contenido)
            is UiState.Error -> ErrorConReintento(
                mensaje = e.mensaje,
                onReintentar = { vm.cargar() },
                modifier = contenido
            )
            is UiState.Exito -> if (e.datos.isEmpty()) {
                Box(modifier = contenido, contentAlignment = Alignment.Center) {
                    Text(
                        text = if (busqueda.isBlank()) "Sin publicaciones" else "Sin resultados para \"${busqueda.trim()}\"",
                        color = TextoCarga,
                        fontSize = 15.sp
                    )
                }
            } else {
                LazyColumn(
                    modifier = contenido,
                    state = estadoLista,
                    contentPadding = PaddingValues(start = 14.dp, end = 14.dp, top = 4.dp, bottom = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(e.datos, key = { it.id }) { post ->
                        TarjetaPost(
                            userId = post.userId,
                            titulo = post.title,
                            cuerpo = post.body.replace("\n", " "),
                            onClick = { onAbrir(post.id) }
                        )
                    }
                    if (busqueda.isBlank() && (hayMas || errorMas != null)) {
                        item(key = "cargar_mas") {
                            PieCargarMas(
                                cargando = cargandoMas,
                                error = errorMas,
                                onCargarMas = { vm.cargarMas() }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PieCargarMas(cargando: Boolean, error: String?, onCargarMas: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        if (error != null) {
            Text(
                text = error,
                color = TextoError,
                fontSize = 15.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(FondoError, RoundedCornerShape(10.dp))
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            )
        }
        if (cargando) {
            CircularProgressIndicator(
                color = AzulMuro,
                trackColor = BordeSuave,
                strokeWidth = 3.dp,
                modifier = Modifier.size(32.dp)
            )
        } else {
            Button(
                onClick = onCargarMas,
                colors = ButtonDefaults.buttonColors(
                    containerColor = AzulMuro,
                    contentColor = Blanco
                )
            ) {
                Text(
                    text = if (error != null) "Reintentar" else "Cargar más",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
        }
    }
}

@Composable
private fun CampoBusqueda(texto: String, onCambiar: (String) -> Unit) {
    OutlinedTextField(
        value = texto,
        onValueChange = onCambiar,
        placeholder = { Text("Buscar por título") },
        singleLine = true,
        trailingIcon = {
            if (texto.isNotEmpty()) {
                TextButton(onClick = { onCambiar("") }) {
                    Text(text = "✕", color = AzulMuro, fontSize = 16.sp)
                }
            }
        },
        shape = RoundedCornerShape(10.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = AzulMuro,
            unfocusedBorderColor = BordeCampo,
            cursorColor = AzulMuro,
            focusedTextColor = TextoTitulo,
            unfocusedTextColor = TextoTitulo,
            focusedContainerColor = Blanco,
            unfocusedContainerColor = Blanco
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 14.dp, end = 14.dp, top = 12.dp)
    )
}

@Composable
private fun FilaFiltros(seleccionado: Int?, onElegir: (Int?) -> Unit) {
    val opciones: List<Int?> = listOf(null) + (1..10).toList()
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        contentPadding = PaddingValues(horizontal = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(opciones) { userId ->
            val activo = userId == seleccionado
            FilterChip(
                selected = activo,
                onClick = { onElegir(userId) },
                label = {
                    Text(
                        text = if (userId == null) "Todos" else "Usuario $userId",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                },
                shape = RoundedCornerShape(10.dp),
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = Blanco,
                    labelColor = AzulMuro,
                    selectedContainerColor = AzulMuro,
                    selectedLabelColor = Blanco
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = activo,
                    borderColor = BordeSuave,
                    selectedBorderColor = AzulMuro
                )
            )
        }
    }
}
