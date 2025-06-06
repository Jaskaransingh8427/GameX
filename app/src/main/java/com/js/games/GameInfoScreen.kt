package com.js.games

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import com.js.games.ui.theme.PremiumGold
import com.js.games.ui.theme.RoyalGold

@Composable
fun GameInfoScreen(navController: NavController) {
    val context = LocalContext.current
    var emailExpanded by remember { mutableStateOf(false) }
    var instagramExpanded by remember { mutableStateOf(false) }
    val colorScheme = MaterialTheme.colorScheme

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CustomTopBar(
                title = "Game Info",
                showBackButton = true,
                customIconRes = R.drawable.info,
                backgroundColor = Color.Unspecified,
                titleColor = colorScheme.primary,
                enableGestures = true,
                showDefaultButtons = false,
                navController = navController
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    Brush.verticalGradient(
                        listOf(colorScheme.background, colorScheme.surfaceVariant)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(20.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher),
                    contentDescription = "App Icon",
                    modifier = Modifier
                        .size(150.dp)
                        .shadow(12.dp, CircleShape)
                )

                Text(
                    text = "Version: 1.0.0",
                    fontSize = 16.sp,
                    color = colorScheme.onBackground.copy(alpha = 0.7f),
                    modifier = Modifier.padding(top = 5.dp)
                )

                Spacer(modifier = Modifier.height(15.dp))

                GlassmorphicCard("GameX is a fun Android app featuring four mini-games in one place.  \n" +
                        "Play Memory Match, Tic-Tac-Toe, TypeAbility, and Math Master anytime.  \n" +
                        "Boost memory, logic, typing speed, and math skills with quick challenges.  \n" +
                        "A perfect mix of entertainment and brain training for all ages.")

                Spacer(modifier = Modifier.height(40.dp))

                Text(
                    text = "Contact Us",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = PremiumGold,
                    modifier = Modifier.padding(bottom = 20.dp)
                )

                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                ) {
                    PopoverButton(
                        label = "Email Us",
                        iconRes = R.drawable.e_mail,
                        expanded = emailExpanded,
                        onClick = { emailExpanded = !emailExpanded },
                        onDismiss = { emailExpanded = false },
                        items = listOf(
                            "Email Manpreet" to {
                                val intent = Intent(Intent.ACTION_SENDTO).apply {
                                    data = Uri.parse("mailto:manpreet@example.com")
                                }
                                context.startActivity(intent)
                            },
                            "Email Jaskaran" to {
                                val intent = Intent(Intent.ACTION_SENDTO).apply {
                                    data = Uri.parse("mailto:jaskaransingh51280@gmail.com")
                                }
                                context.startActivity(intent)
                            }
                        )
                    )

                    Spacer(modifier = Modifier.width(40.dp))

                    PopoverButton(
                        label = "Instagram",
                        iconRes = R.drawable.instagram,
                        expanded = instagramExpanded,
                        onClick = { instagramExpanded = !instagramExpanded },
                        onDismiss = { instagramExpanded = false },
                        items = listOf(
                            "Instagram Manpreet" to {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/mani_malri_1"))
                                context.startActivity(intent)
                            },
                            "Instagram Jaskaran" to {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/jaskaransingh4203"))
                                context.startActivity(intent)
                            }
                        )
                    )
                }

                Spacer(modifier = Modifier.height(40.dp))

                DeveloperInfo()
            }
        }
    }
}

@Composable
fun PopoverButton(
    label: String,
    iconRes: Int,
    expanded: Boolean,
    onClick: () -> Unit,
    onDismiss: () -> Unit,
    items: List<Pair<String, () -> Unit>>
) {
    val colorScheme = MaterialTheme.colorScheme

    Box {
        Box(
            modifier = Modifier
                .size(60.dp)
                .background(colorScheme.primaryContainer, CircleShape)
                .clickable { onClick() }
                .shadow(10.dp, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = label,
                tint = Color.Unspecified,
                modifier = Modifier.size(32.dp)
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = onDismiss,
            modifier = Modifier
                .background(colorScheme.surface, RoundedCornerShape(12.dp))
                .padding(8.dp)
        ) {
            Text(
                text = label.uppercase(),
                color = colorScheme.primary,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
            )

            Divider(color = colorScheme.outlineVariant.copy(alpha = 0.5f))

            items.forEach { (title, action) ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = title,
                            color = colorScheme.onSurface,
                            fontSize = 14.sp
                        )
                    },
                    onClick = {
                        onDismiss()
                        action()
                    }
                )
            }
        }
    }
}

@Composable
fun GlassmorphicCard(text: String) {
    val colorScheme = MaterialTheme.colorScheme

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        colorScheme.onSurface.copy(alpha = 0.05f),
                        colorScheme.onSurface.copy(alpha = 0.02f)
                    )
                ),
                shape = RoundedCornerShape(16.dp)
            )
            .border(1.dp, colorScheme.outlineVariant.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Text(
            text = text,
            color = colorScheme.onSurface.copy(alpha = 0.8f),
            fontSize = 14.sp
        )
    }
}

@Composable
fun AnimatedText(text: String, fontSize: TextUnit, color: Color, fontWeight: FontWeight) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val animatedAlpha by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "alpha"
    )

    Text(
        text = text,
        fontSize = fontSize,
        color = color.copy(alpha = animatedAlpha),
        fontWeight = fontWeight
    )
}

@Composable
fun DeveloperInfo() {
    Text(
        text = "Developed by Jaskaran & Manpreet",
        color = RoyalGold,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(top = 20.dp)
    )
}
