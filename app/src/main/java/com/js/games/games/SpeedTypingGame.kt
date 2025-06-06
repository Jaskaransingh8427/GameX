package com.js.games.games

import android.content.Context
import android.speech.tts.TextToSpeech
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.*
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import com.js.games.*
import com.js.games.R
import kotlinx.coroutines.*
import java.io.IOException
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*
import java.util.concurrent.atomic.AtomicLong

@Composable
fun SpeedTypingGameScreen(navController: NavController) {
    val context = LocalContext.current
    var ttsInitialized by remember { mutableStateOf(false) }
    var mode by remember { mutableStateOf<String?>(null) }
    var words by remember { mutableStateOf(emptyList<String>()) }
    var currentWord by remember { mutableStateOf("") }
    var userInput by remember { mutableStateOf("") }
    var score by remember { mutableStateOf(0) }
    var timer by remember { mutableStateOf(0) }
    var gameOver by remember { mutableStateOf(false) }
    var accuracy by remember { mutableStateOf(BigDecimal(100)) }
    var totalTyped by remember { mutableStateOf(0) }
    var correctTyped by remember { mutableStateOf(0) }
    var wpm by remember { mutableStateOf(BigDecimal.ZERO) }
    var cpm by remember { mutableStateOf(BigDecimal.ZERO) }
    var showDialog by remember { mutableStateOf(true) }
    var startTime by remember { mutableStateOf(AtomicLong(0)) }

    val tts = remember(context) {
        TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                ttsInitialized = true
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            tts.stop()
            tts.shutdown()
        }
    }

    val coroutineScope = rememberCoroutineScope()

    // Timer logic
    LaunchedEffect(timer, gameOver) {
        while (timer > 0 && !gameOver) {
            delay(1000)
            timer--
        }
        if (timer == 0) gameOver = true
    }

    if (showDialog) {
        DifficultySelectionDialog { selectedMode ->
            mode = selectedMode
            words = loadWords(context, selectedMode)
            currentWord = words.random()
            timer = when (selectedMode) {
                "Basic" -> 30
                "Medium" -> 45
                "Advanced" -> 60
                else -> 30
            }
            showDialog = false
            gameOver = false
            userInput = ""
            score = 0
            correctTyped = 0
            totalTyped = 0
            startTime.set(System.nanoTime())
        }
    }

    var lastWord by remember { mutableStateOf("") }

    fun getNewWord(): String {
        var newWord: String
        do {
            newWord = words.random()
        } while (newWord == lastWord && words.size > 1)
        lastWord = newWord
        return newWord
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CustomTopBar(
                title = "TypeAbility",
                showBackButton = true,
                customIconRes = R.drawable.memory_match,
                backgroundColor = Color.Unspecified,
                titleColor = MaterialTheme.colorScheme.primary,
                enableGestures = true,
                showDefaultButtons = true,
                navController = navController
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AnimatedVisibility(visible = !gameOver, enter = fadeIn(), exit = fadeOut()) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Mode: $mode", style = MaterialTheme.typography.headlineMedium)
                    Text("Time Left: ${timer}s", style = MaterialTheme.typography.headlineMedium)
                    Text("Score: $score", style = MaterialTheme.typography.headlineMedium)

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        MetricCard("WPM", wpm.setScale(2, RoundingMode.HALF_UP).toString())
                        MetricCard("CPM", cpm.setScale(2, RoundingMode.HALF_UP).toString())
                        MetricCard("Accuracy", "${accuracy.setScale(2, RoundingMode.HALF_UP)}%")
                    }

                    Text(
                        text = currentWord,
                        style = TextStyle(fontSize = 32.sp, color = MaterialTheme.colorScheme.primary),
                        modifier = Modifier.shadow(8.dp, shape = RoundedCornerShape(8.dp))
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = userInput,
                        onValueChange = { input ->
                            if (startTime.get() == 0L) startTime.set(System.nanoTime())
                            coroutineScope.launch {
                                userInput = input
                                totalTyped++
                                if (input.trim() == currentWord.trim()) {
                                    score++
                                    correctTyped++
                                    currentWord = getNewWord()
                                    userInput = ""
                                    if (ttsInitialized) {
                                        tts.language = Locale.US
                                        tts.speak(currentWord, TextToSpeech.QUEUE_FLUSH, null, null)
                                    }
                                    val elapsedTime = BigDecimal(System.nanoTime() - startTime.get())
                                        .divide(BigDecimal(60_000_000_000L), 10, RoundingMode.HALF_UP)
                                    wpm = if (elapsedTime > BigDecimal.ZERO)
                                        BigDecimal(correctTyped).divide(BigDecimal(5), 10, RoundingMode.HALF_UP)
                                            .divide(elapsedTime, 10, RoundingMode.HALF_UP)
                                    else BigDecimal.ZERO
                                    cpm = if (elapsedTime > BigDecimal.ZERO)
                                        BigDecimal(totalTyped).divide(elapsedTime, 10, RoundingMode.HALF_UP)
                                    else BigDecimal.ZERO
                                }
                                accuracy = if (totalTyped > 0)
                                    BigDecimal(correctTyped * 100).divide(BigDecimal(totalTyped), 10, RoundingMode.HALF_UP)
                                else BigDecimal(100)
                            }
                        },
                        label = { Text("Type here...") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = false,
                        textStyle = TextStyle(fontSize = 24.sp)
                    )
                }
            }

            AnimatedVisibility(visible = gameOver, enter = fadeIn(), exit = fadeOut()) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Game Over!", style = MaterialTheme.typography.headlineLarge)
                    Text("Final Score: $score", style = MaterialTheme.typography.headlineMedium)

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        MetricCard("WPM", wpm.setScale(2, RoundingMode.HALF_UP).toString())
                        MetricCard("CPM", cpm.setScale(2, RoundingMode.HALF_UP).toString())
                        MetricCard("Accuracy", "${accuracy.setScale(2, RoundingMode.HALF_UP)}%")
                    }

                    Button(onClick = {
                        score = 0
                        timer = when (mode) {
                            "Basic" -> 30
                            "Medium" -> 45
                            "Advanced" -> 60
                            else -> 30
                        }
                        gameOver = false
                        userInput = ""
                        correctTyped = 0
                        totalTyped = 0
                        startTime.set(System.nanoTime())
                        currentWord = getNewWord()
                        if (ttsInitialized) {
                            tts.language = Locale.US
                            tts.speak(currentWord, TextToSpeech.QUEUE_FLUSH, null, null)
                        }
                    }) {
                        Text("Restart")
                    }
                }
            }
        }
    }
}

@Composable
fun MetricCard(label: String, value: String) {
    Surface(
        modifier = Modifier
            .padding(4.dp)
            .width(100.dp)
            .height(80.dp),
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 4.dp,
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(label, style = MaterialTheme.typography.labelMedium)
            Text(value, style = MaterialTheme.typography.headlineSmall)
        }
    }
}

@Composable
fun DifficultySelectionDialog(onModeSelected: (String) -> Unit) {
    AlertDialog(
        onDismissRequest = { },
        title = { Text("Choose Difficulty") },
        text = {
            Column {
                GameUniversalButton(
                    text = "Basic (Single Words)",
                    onClick = { onModeSelected("Basic") },
                    effect = ButtonEffect.ShineSweep,
                    modifier = Modifier.width(250.dp).height(50.dp)
                )
                GameUniversalButton(
                    text = "Medium (Three Words)",
                    onClick = { onModeSelected("Medium") },
                    effect = ButtonEffect.ShineSweep,
                    modifier = Modifier.width(250.dp).height(50.dp)
                )
                GameUniversalButton(
                    text = "Advanced (Complete Sentences)",
                    onClick = { onModeSelected("Advanced") },
                    effect = ButtonEffect.ShineSweep,
                    modifier = Modifier.width(250.dp).height(50.dp)
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        confirmButton = {}
    )
}

fun loadWords(context: Context, mode: String): List<String> {
    val fileName = when (mode) {
        "Basic" -> "basic.txt"
        "Medium" -> "medium.txt"
        "Advanced" -> "advanced.txt"
        else -> "basic.txt"
    }
    return try {
        context.assets.open(fileName).bufferedReader().use { it.readLines() }
    } catch (e: IOException) {
        listOf("error", "word", "typing", "test", "android")
    }
}
