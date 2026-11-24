package com.tecsup.murocampus

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tecsup.murocampus.data.PostRepositoryRemote
import com.tecsup.murocampus.data.remote.RetrofitClient
import com.tecsup.murocampus.ui.BarraMuroCampus
import com.tecsup.murocampus.ui.PantallaLista
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
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = FondoMuro,
                    topBar = { BarraMuroCampus() }
                ) { innerPadding ->
                    PantallaLista(
                        vm = vm,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
