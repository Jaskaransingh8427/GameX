package com.js.games

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.unit.*
import androidx.compose.runtime.State

fun Modifier.glowBorder(alpha: Float): Modifier = this.drawBehind {
    val glowColor = Color.White.copy(alpha = alpha)
    drawIntoCanvas { canvas ->
        val paint = Paint().apply {
            this.color = glowColor
            this.isAntiAlias = true
            this.asFrameworkPaint().setShadowLayer(25f, 0f, 0f, glowColor.toArgb())
        }
        canvas.drawRoundRect(
            left = 0f,
            top = 0f,
            right = size.width,
            bottom = size.height,
            radiusX = 24.dp.toPx(),
            radiusY = 24.dp.toPx(),
            paint = paint
        )
    }
}

fun Modifier.shineSweep(offsetX: Float): Modifier = drawWithContent {
    drawContent()
    val shineBrush = Brush.linearGradient(
        colors = listOf(
            Color.Transparent,
            Color.White.copy(alpha = 0.6f),
            Color.Transparent
        ),
        start = Offset(offsetX, 0f),
        end = Offset(offsetX + 200f, size.height)
    )
    drawRect(brush = shineBrush, size = size)
}

@Composable
fun infiniteRepeatableFloat(
    visible: Boolean,
    range: ClosedFloatingPointRange<Float>,
    durationMillis: Int
): State<Float> {
    val transition = rememberInfiniteTransition(label = "InfiniteEffect")
    return if (visible) {
        transition.animateFloat(
            initialValue = range.start,
            targetValue = range.endInclusive,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "AnimatedFloat"
        )
    } else {
        rememberUpdatedState(range.start)
    }
}
