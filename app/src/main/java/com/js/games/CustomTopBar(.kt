package com.js.games

import android.app.Activity
import android.view.MotionEvent
import android.view.View
import androidx.annotation.DrawableRes
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun CustomTopBar(
    title: String,
    subtitle: String? = null,
    showBackButton: Boolean = false,
    @DrawableRes customIconRes: Int? = null,
    backgroundColor: Color = Color.Transparent,
    titleColor: Color = Color.White,
    enableGlassEffect: Boolean = false,
    enableGestures: Boolean = false,
    showDefaultButtons: Boolean = true,
    navController: NavController
) {
    val context = LocalContext.current
    val activity = context as? Activity
    var isVisible by remember { mutableStateOf(true) }
    var startX by remember { mutableStateOf(0f) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(
                if (enableGlassEffect) backgroundColor.copy(alpha = 0.3f) else backgroundColor,
                shape = RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp)
            )
            .then(if (enableGlassEffect) Modifier.blur(10.dp) else Modifier)
            .pointerInput(enableGestures) {
                detectTapGestures(
                    onDoubleTap = { if (enableGestures) isVisible = !isVisible }
                )
            }
    ) {
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn() + slideInVertically { -it },
            exit = fadeOut() + slideOutVertically { -it }
        ) {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = titleColor
                ),
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (customIconRes != null) {
                            Icon(
                                painter = painterResource(id = customIconRes),
                                contentDescription = "$title Icon",
                                tint = Color.Unspecified,
                                modifier = Modifier
                                    .size(45.dp)
                                    .padding(end = 8.dp)
                            )
                        }

                        Column {
                            Text(text = title, fontSize = 20.sp, color = titleColor)
                            subtitle?.let {
                                Text(
                                    text = it,
                                    fontSize = 14.sp,
                                    color = titleColor.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }
                },
                navigationIcon = {
                    if (showBackButton) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = titleColor
                            )
                        }
                    }
                },
                actions = {
                    if (showDefaultButtons) {
                        IconButton(onClick = { navController.navigate("game_info_screen") }) {
                            Icon(
                                painter = painterResource(id = R.drawable.info),
                                contentDescription = "GameX Info",
                                tint = Color.Unspecified
                            )
                        }
                        IconButton(onClick = { navController.navigate("settings_screen") }) {
                            Icon(
                                painter = painterResource(id = R.drawable.settings),
                                contentDescription = "Setting",
                                tint = Color.Unspecified
                            )
                        }
                    }
                }
            )
        }
    }

    if (enableGestures) {
        LaunchedEffect(Unit) {
            activity?.window?.decorView?.setOnTouchListener { _, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> startX = event.x
                    MotionEvent.ACTION_UP -> {
                        if (event.x - startX > 300) {
                            activity?.finish()
                        }
                    }
                }
                false
            }
        }
    }
}
