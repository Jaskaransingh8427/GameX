package com.js.games.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import com.js.games.navigation.AppNavigation
import com.js.games.ui.theme.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private lateinit var themeController: ThemeController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Synchronously load theme before Compose starts
        val savedTheme = runBlocking {
            ThemeDataStore.getTheme(applicationContext).first()
        }
        themeController = ThemeController(savedTheme)

        setContent {
            AppTheme(themeController) {
                var showSplash by rememberSaveable { mutableStateOf(true) }

                if (showSplash) {
                    SplashAnimatedScreen(
                        onSplashComplete = {
                            showSplash = false
                        }
                    )
                } else {
                    AppNavigation()
                }
            }
        }
    }
}