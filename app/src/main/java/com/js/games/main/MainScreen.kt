package com.js.games.main

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.js.games.R
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.js.games.CustomTopBar
import com.js.games.ui.theme.DeepGold
import com.js.games.ui.theme.RoyalGold


@Composable
fun RoundButton(
    iconRes: Int,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(150.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
                .clickable(onClick = onClick)
                .semantics {
                    contentDescription = label
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                modifier = Modifier.size(200.dp),
                tint = Color.Unspecified
            )
        }
        Text(
            text = label,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
fun MainScreen(navController: NavController) {
    Scaffold(
        topBar = {
            CustomTopBar(
                title = "GameX",
                customIconRes = R.drawable.ic_launcher,
                backgroundColor = Color.Transparent,
                titleColor = MaterialTheme.colorScheme.primary,
                enableGestures = true,
                showDefaultButtons = true,
                navController = navController
            )
        },
        modifier = Modifier.fillMaxSize()
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Welcome to GameX!",
                color = RoyalGold,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            // Grid Layout with alternating left-right pattern
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    RoundButton(
                        iconRes = R.drawable.memory_match,
                        label = "Memory Match",
                        onClick = { navController.navigate("memory_match") }
                    )
                    RoundButton(
                        iconRes = R.drawable.tic_tac_toe,
                        label = "Tic-Tac-Toe",
                        onClick = { navController.navigate("tic_tac_toe") }
                    )
                }

                /*
                val context = LocalContext.current

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    RoundButton(
                        iconRes = R.drawable.info,
                        label = "Voice Maze",
                        onClick = {
                            val intent = Intent(context, VoiceMazeActivity::class.java)
                            context.startActivity(intent)
                        }
                    )
                }
                */

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    RoundButton(
                        iconRes = R.drawable.type_ability,
                        label = "TypeAbility",
                        onClick = { navController.navigate("speed_typing") }
                    )
                    RoundButton(
                        iconRes = R.drawable.math_master,
                        label = "Math Master",
                        onClick = { navController.navigate("math_master") }
                    )
                }
            }
        }
    }
}
