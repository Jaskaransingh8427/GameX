package com.js.games.navigation

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.*
import com.js.games.GameInfoScreen
import com.js.games.SettingsScreen
import com.js.games.games.MathMaster
import com.js.games.games.TicTacToe.TicTacToeScreen
import com.js.games.games.MemoryMatch
import com.js.games.games.SpeedTypingGameScreen
import com.js.games.main.MainScreen
import com.js.games.ui.theme.LocalThemeController
import com.js.games.ui.theme.ThemeDataStore

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "main") {
        composable("main") {
            MainScreen(navController)
        }
        composable("memory_match") {
            MemoryMatch(navController)
        }
        composable("tic_tac_toe") {
            TicTacToeScreen(navController)
        }
        composable("speed_typing") {
            SpeedTypingGameScreen(navController)
        }
        composable("game_info_screen") {
            GameInfoScreen(navController)
        }
        composable("settings_screen") {
            val themeController = LocalThemeController.current
            val context = LocalContext.current
            SettingsScreen(navController) { newTheme ->
                themeController.setTheme(newTheme)
                ThemeDataStore.saveTheme(context, newTheme) // <-- This line saves it persistently
            }
        }
        composable("math_master") {
            MathMaster(navController)
        }
    }
}
