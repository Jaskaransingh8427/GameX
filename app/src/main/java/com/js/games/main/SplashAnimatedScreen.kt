package com.js.games.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.*
import kotlinx.coroutines.delay
import  com.js.games.R

@Composable
fun SplashAnimatedScreen(onSplashComplete: () -> Unit) {
    var startAnimation by remember { mutableStateOf(false) }
    var showText by remember { mutableStateOf(false) }

    val density = LocalDensity.current.density

    // Icon entry animation from bottom to center
    val offsetY by animateDpAsState(
        targetValue = if (startAnimation) 0.dp else 400.dp,
        animationSpec = tween(durationMillis = 1000, easing = EaseOutCubic)
    )

    // Icon bounce + zoom-out effect
    val scale by animateFloatAsState(
        targetValue = if (startAnimation) 1.0f else 0.8f,
        animationSpec = keyframes {
            durationMillis = 1400
            0f at 0 with LinearOutSlowInEasing
            1.1f at 700
            0.9f at 900
            1.0f at 1200
        }
    )

    // Text fade-in and scale
    val textScale by animateFloatAsState(
        targetValue = if (showText) 1f else 0.8f,
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing)
    )
    val textAlpha by animateFloatAsState(
        targetValue = if (showText) 1f else 0f,
        animationSpec = tween(durationMillis = 800)
    )

    LaunchedEffect(Unit) {
        delay(300) // Delay before animation starts
        startAnimation = true
        delay(1200) // Wait until bounce stabilizes
        showText = true
        delay(1500) // Final hold before navigating
        onSplashComplete()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .offset(y = offsetY)
                .scale(scale),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_launcher),
                contentDescription = "App Icon",
                modifier = Modifier.size(150.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(20.dp))

            AnimatedVisibility(visible = showText) {
                Text(
                    text = "GameX",
                    color = Color.White,
                    fontSize = 40.sp,
                    modifier = Modifier
                        .scale(textScale)
                        .alpha(textAlpha),
                    style = MaterialTheme.typography.headlineMedium
                )
            }
        }
    }
}
