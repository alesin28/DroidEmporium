package org.alessandrosinibaldi.droidemporium.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = AdminBlue,
    onPrimary = TextOnElement,
    secondary = AccentOrange,
    onSecondary = TextOnElement,
    background = AppBackground,
    onBackground = PrimaryText,
    surface = SurfaceWhite,
    onSurface = PrimaryText,
    surfaceVariant = TableHeaderGray,
    outline = DividerGray,
    error = FailureRed,
    onError = TextOnElement,
    primaryContainer = LightBlueContainer,
    onPrimaryContainer = AdminBlue,
)

@Composable
fun DroidEmporiumTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}