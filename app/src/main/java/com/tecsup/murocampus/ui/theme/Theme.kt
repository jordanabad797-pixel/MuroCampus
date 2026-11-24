package com.tecsup.murocampus.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val EsquemaMuro = lightColorScheme(
    primary = AzulMuro,
    onPrimary = Blanco,
    background = FondoMuro,
    onBackground = TextoTitulo,
    surface = FondoMuro,
    onSurface = TextoTitulo,
    surfaceVariant = FondoTarjeta,
    onSurfaceVariant = TextoCuerpo,
    error = TextoError,
    errorContainer = FondoError,
    onErrorContainer = TextoError,
    outline = BordeCampo
)

@Composable
fun MuroCampusTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = EsquemaMuro,
        typography = Typography,
        content = content
    )
}
