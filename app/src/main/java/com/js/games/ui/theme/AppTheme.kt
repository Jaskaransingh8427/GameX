package com.js.games.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.*

@Composable
fun AppTheme(
    themeController: ThemeController,
    content: @Composable () -> Unit
) {
    val mode by themeController.theme.collectAsState()
    val isDark = when (mode) {
        ThemeMode.DARK -> true
        ThemeMode.LIGHT -> false
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

    CompositionLocalProvider(LocalThemeController provides themeController) {
        MaterialTheme(
            colorScheme = if (isDark) darkColorScheme() else lightColorScheme(),
            typography = Typography(),
            content = content
        )
    }
}
