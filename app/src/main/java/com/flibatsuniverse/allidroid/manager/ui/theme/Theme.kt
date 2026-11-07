package com.flibatsuniverse.allidroid.manager.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.flibatsuniverse.allidroid.manager.domain.manager.PreferenceManager
import com.flibatsuniverse.allidroid.manager.domain.manager.Theme
import org.koin.androidx.compose.get

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AllidroidManagerTheme(
    content: @Composable () -> Unit
) {
    val prefs = get<PreferenceManager>()
    val darkTheme = when (prefs.theme) {
        Theme.SYSTEM -> isSystemInDarkTheme()
        Theme.DARK -> true
        Theme.LIGHT -> false
    }

    val colorScheme = when {
        prefs.monet && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> AllidroidBrandDarkColors
        else -> AllidroidBrandLightColors
    }

    MaterialExpressiveTheme(
        colorScheme = if (darkTheme && prefs.pitchBlack) colorScheme.toPitchBlack() else colorScheme,
        motionScheme = MotionScheme.expressive(),
        typography = Typography,
        content = content
    )
}

private fun ColorScheme.toPitchBlack() = copy(
    background = Color.Black,
    surface = Color.Black,
    surfaceDim = Color.Black,
    surfaceContainerLowest = Color.Black
)
