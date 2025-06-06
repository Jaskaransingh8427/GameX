package com.js.games

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*

sealed class ButtonEffect {
    object NoEffect : ButtonEffect()
    object BounceClick : ButtonEffect()
    object ShineSweep : ButtonEffect()
    object GlowPulse : ButtonEffect()
    object CombinedBloom : ButtonEffect()
}

// Main button
@Composable
fun GameUniversalButton(
    text: String,
    icon: ImageVector? = null,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    gradient: List<Color>? = null,
    backgroundColor: Color? = null,
    textColor: Color? = null,
    cornerRadius: Dp = 24.dp,
    fontSize: TextUnit = 18.sp,
    fontWeight: FontWeight = FontWeight.SemiBold,
    elevation: Dp = 6.dp,
    enabled: Boolean = true,
    effect: ButtonEffect = ButtonEffect.NoEffect
) {
    val interactionSource = remember { MutableInteractionSource() }
    var isPressed by remember { mutableStateOf(false) }

    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction ->
            isPressed = interaction is PressInteraction.Press
        }
    }

    val bounceScale = animateFloatAsState(
        targetValue = if (effect == ButtonEffect.BounceClick && isPressed) 0.95f else 1f,
        animationSpec = tween(150),
        label = "BounceScale"
    )

    val glowAlpha by infiniteRepeatableFloat(visible = effect == ButtonEffect.GlowPulse, range = 0.3f..0.8f, durationMillis = 1500)
    val shineOffset by infiniteRepeatableFloat(visible = effect == ButtonEffect.ShineSweep, range = -200f..800f, durationMillis = 2000)

    val combinedModifier = modifier
        .graphicsLayer {
            scaleX = bounceScale.value
            scaleY = bounceScale.value
        }
        .then(if (effect == ButtonEffect.GlowPulse || effect == ButtonEffect.CombinedBloom) Modifier.glowBorder(glowAlpha) else Modifier)
        .then(if (effect == ButtonEffect.ShineSweep || effect == ButtonEffect.CombinedBloom) Modifier.shineSweep(shineOffset) else Modifier)

    Button(
        onClick = onClick,
        modifier = combinedModifier
            .clip(RoundedCornerShape(cornerRadius))
            .defaultMinSize(minHeight = 48.dp)
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        enabled = enabled,
        shape = RoundedCornerShape(cornerRadius),
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor ?: Color.Transparent,
            contentColor = textColor ?: MaterialTheme.colorScheme.onPrimary,
            disabledContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
            disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = elevation)
    ) {
        Box(
            modifier = Modifier

                .then(
                    if (gradient != null && gradient.size >= 2) {
                        Modifier.background(
                            brush = Brush.horizontalGradient(gradient),
                            shape = RoundedCornerShape(cornerRadius)
                        )
                    } else Modifier
                )

                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = text,
                        modifier = Modifier.size(20.dp).padding(end = 6.dp)
                    )
                }
                Text(
                    text = text,
                    color = textColor ?: MaterialTheme.colorScheme.onPrimary,
                    fontSize = fontSize,
                    fontWeight = fontWeight,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
