package com.js.games

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key.Companion.W
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.js.games.ui.theme.LocalThemeController
import com.js.games.ui.theme.ThemeMode
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    navController: NavController,
    onThemeChanged: suspend (ThemeMode) -> Unit
) {
    val themeController = LocalThemeController.current
    val currentTheme by themeController.theme.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CustomTopBar(
                title = "Settings",
                showBackButton = true,
                customIconRes = R.drawable.settings ,
                backgroundColor = Color.Transparent,
                titleColor = MaterialTheme.colorScheme.primary,
                enableGestures = true,
                showDefaultButtons = false,
                navController = navController
            )
        }
    ) { padding ->

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Settings", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showDialog = true }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Theme", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.weight(1f))
            Text(currentTheme.name.lowercase().replaceFirstChar { it.uppercase() })
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                confirmButton = {},
                title = { Text("Choose Theme") },
                text = {
                    Column {
                        ThemeMode.values().forEach { mode ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        coroutineScope.launch {
                                            onThemeChanged(mode)
                                            showDialog = false
                                        }
                                    }
                                    .padding(vertical = 8.dp)
                            ) {
                                RadioButton(
                                    selected = currentTheme == mode,
                                    onClick = {
                                        coroutineScope.launch {
                                            onThemeChanged(mode)
                                            showDialog = false
                                        }
                                    }
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(mode.name.lowercase().replaceFirstChar { it.uppercase() })
                            }
                        }
                    }
                }
            )
        }
    }
}
}
