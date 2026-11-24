package com.tecsup.murocampus

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tecsup.murocampus.data.PostRepositoryRemote
import com.tecsup.murocampus.data.remote.RetrofitClient
import com.tecsup.murocampus.ui.BarraMuroCampus
import com.tecsup.murocampus.ui.PantallaCrear
import com.tecsup.murocampus.ui.PantallaDetalle
import com.tecsup.murocampus.ui.PantallaLista
import com.tecsup.murocampus.ui.theme.AzulMuro
import com.tecsup.murocampus.ui.theme.Blanco
import com.tecsup.murocampus.ui.theme.FondoMuro
import com.tecsup.murocampus.ui.theme.MuroCampusTheme
import com.tecsup.murocampus.viewmodel.PostsViewModel
import com.tecsup.murocampus.viewmodel.PostsViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT))
        setContent {
            MuroCampusTheme {
                val vm: PostsViewModel = viewModel(
                    factory = PostsViewModelFactory(PostRepositoryRemote(RetrofitClient.api))
                )
                var idSeleccionado by rememberSaveable { mutableStateOf<Int?>(null) }
                var creando by rememberSaveable { mutableStateOf(false) }
                val estadoLista = rememberLazyListState()

                BackHandler(enabled = idSeleccionado != null || creando) {
                    idSeleccionado = null
                    creando = false
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = FondoMuro,
                    topBar = { BarraMuroCampus() },
                    floatingActionButton = {
                        if (idSeleccionado == null && !creando) {
                            FloatingActionButton(
                                onClick = {
                                    vm.reiniciarCreacion()
                                    creando = true
                                },
                                containerColor = AzulMuro,
                                contentColor = Blanco
                            ) {
                                Text(text = "+", fontSize = 28.sp)
                            }
                        }
                    }
                ) { innerPadding ->
                    val id = idSeleccionado
                    when {
                        creando -> PantallaCrear(
                            vm = vm,
                            modifier = Modifier.padding(innerPadding)
                        )
                        id != null -> PantallaDetalle(
                            id = id,
                            vm = vm,
                            modifier = Modifier.padding(innerPadding)
                        )
                        else -> PantallaLista(
                            vm = vm,
                            onAbrir = { nuevoId ->
                                vm.abrir(nuevoId)
                                idSeleccionado = nuevoId
                            },
                            modifier = Modifier.padding(innerPadding),
                            estadoLista = estadoLista
                        )
                    }
                }
            }
        }
    }
}
