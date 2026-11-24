package com.tecsup.murocampus.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecsup.murocampus.ui.theme.AzulMuro
import com.tecsup.murocampus.ui.theme.Blanco
import com.tecsup.murocampus.ui.theme.BordeExito
import com.tecsup.murocampus.ui.theme.BordeSuave
import com.tecsup.murocampus.ui.theme.ChipAmbar
import com.tecsup.murocampus.ui.theme.ChipAzul
import com.tecsup.murocampus.ui.theme.FondoError
import com.tecsup.murocampus.ui.theme.FondoExito
import com.tecsup.murocampus.ui.theme.FondoTarjeta
import com.tecsup.murocampus.ui.theme.TextoCarga
import com.tecsup.murocampus.ui.theme.TextoChipAmbar
import com.tecsup.murocampus.ui.theme.TextoCuerpo
import com.tecsup.murocampus.ui.theme.TextoError
import com.tecsup.murocampus.ui.theme.TextoExito
import com.tecsup.murocampus.ui.theme.TextoTitulo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarraMuroCampus() {
    TopAppBar(
        title = {
            Text(
                text = "MuroCampus",
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = AzulMuro,
            titleContentColor = Blanco
        )
    )
}

@Composable
fun EtiquetaUsuario(userId: Int) {
    val esPar = userId % 2 == 0
    Text(
        text = "Usuario $userId",
        color = if (esPar) TextoChipAmbar else Blanco,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .background(if (esPar) ChipAmbar else ChipAzul, RoundedCornerShape(50))
            .padding(horizontal = 8.dp, vertical = 2.dp)
    )
}

@Composable
fun TarjetaPost(
    userId: Int,
    titulo: String,
    cuerpo: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(FondoTarjeta)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        EtiquetaUsuario(userId)
        Spacer(Modifier.height(4.dp))
        Text(
            text = titulo,
            color = TextoTitulo,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = cuerpo,
            color = TextoCuerpo,
            fontSize = 13.sp,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun Cargando(
    modifier: Modifier = Modifier,
    texto: String = "Cargando publicaciones…"
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Blanco, RoundedCornerShape(14.dp))
                .border(1.dp, BordeSuave, RoundedCornerShape(14.dp))
                .padding(vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                color = AzulMuro,
                trackColor = BordeSuave,
                strokeWidth = 4.dp,
                modifier = Modifier.size(44.dp)
            )
            Spacer(Modifier.height(16.dp))
            Text(text = texto, color = TextoCarga, fontSize = 15.sp)
        }
    }
}

@Composable
fun ErrorConReintento(
    mensaje: String,
    onReintentar: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .padding(top = 76.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = mensaje,
            color = TextoError,
            fontSize = 15.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .background(FondoError, RoundedCornerShape(10.dp))
                .padding(horizontal = 16.dp, vertical = 12.dp)
        )
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = onReintentar,
            colors = ButtonDefaults.buttonColors(
                containerColor = AzulMuro,
                contentColor = Blanco
            )
        ) {
            Text(text = "Reintentar", fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }
    }
}

@Composable
fun MensajeExito(texto: String, modifier: Modifier = Modifier) {
    Text(
        text = texto,
        color = TextoExito,
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        modifier = modifier
            .fillMaxWidth()
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
            .padding(horizontal = 16.dp, vertical = 11.dp)
    )
}
