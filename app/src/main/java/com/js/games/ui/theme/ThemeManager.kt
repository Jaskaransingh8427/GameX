package com.js.games.ui.theme

import androidx.compose.runtime.compositionLocalOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ThemeController(initial: ThemeMode) {
    private val _theme = MutableStateFlow(initial)
    val theme: StateFlow<ThemeMode> = _theme

    fun setTheme(mode: ThemeMode) {
        _theme.value = mode
    }
}

val LocalThemeController = compositionLocalOf<ThemeController> {
    error("ThemeController not provided")
}
