package com.tech.maxclub.alligatorvscrocodileclassifier.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xff47e08b),
    onPrimary = Color(0xff00391c),
    primaryContainer = Color(0xff00522c),
    onPrimaryContainer = Color(0xff69fea5),
    secondary = Color(0xffb6ccb9),
    onSecondary = Color(0xff223527),
    secondaryContainer = Color(0xff384b3c),
    onSecondaryContainer = Color(0xffd2e8d4),
    tertiary = Color(0xffa2cdda),
    onTertiary = Color(0xff023640),
    tertiaryContainer = Color(0xff214c57),
    onTertiaryContainer = Color(0xffbeeaf7),
    error = Color(0xffffb4ab),
    onError = Color(0xff690005),
    errorContainer = Color(0xff93000a),
    onErrorContainer = Color(0xffffdad6),
    background = Color(0xff191c19),
    onBackground = Color(0xffe1e3de),
    surface = Color(0xff191c19),
    onSurface = Color(0xffe1e3de),
    outline = Color(0xff8b938a),
    surfaceVariant = Color(0xff414942),
    onSurfaceVariant = Color(0xffc0c9bf),
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xff006d3c),
    onPrimary = Color(0xffffffff),
    primaryContainer = Color(0xff69fea5),
    onPrimaryContainer = Color(0xff00210e),
    secondary = Color(0xff4f6353),
    onSecondary = Color(0xffffffff),
    secondaryContainer = Color(0xffd2e8d4),
    onSecondaryContainer = Color(0xff0d1f13),
    tertiary = Color(0xff3a646f),
    onTertiary = Color(0xffffffff),
    tertiaryContainer = Color(0xffbeeaf7),
    onTertiaryContainer = Color(0xff001f26),
    error = Color(0xffba1a1a),
    onError = Color(0xffffffff),
    errorContainer = Color(0xffffdad6),
    onErrorContainer = Color(0xff410002),
    background = Color(0xfffbfdf8),
    onBackground = Color(0xff191c19),
    surface = Color(0xfffbfdf8),
    onSurface = Color(0xff191c19),
    outline = Color(0xff717971),
    surfaceVariant = Color(0xffdde5db),
    onSurfaceVariant = Color(0xff414942),
)

@Composable
fun AlligatorVsCrocodileClassifierTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.secondaryContainer.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}