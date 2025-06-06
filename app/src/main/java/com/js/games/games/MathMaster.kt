package com.js.games.games

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.js.games.CustomTopBar
import com.js.games.R
import kotlin.random.Random

@Composable
fun MathMaster(navController: NavController) {
    var score by remember { mutableStateOf(0) }
    var correctAnswers by remember { mutableStateOf(0) }
    var wrongAnswers by remember { mutableStateOf(0) }
    var totalAnswers by remember { mutableStateOf(0) }
    var questionType by remember { mutableStateOf<String?>(null) }
    var question by remember { mutableStateOf<MathQuestion?>(null) }
    var isGameOver by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(true) }

    val backgroundColor = MaterialTheme.colorScheme.background
    val textColor = MaterialTheme.colorScheme.onBackground

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CustomTopBar(
                title = "Math Master",
                showBackButton = true,
                customIconRes = R.drawable.math_master,
                backgroundColor = Color.Unspecified,
                titleColor = MaterialTheme.colorScheme.primary,
                enableGestures = true,
                showDefaultButtons = true,
                navController = navController
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(padding)
        ) {
            if (showDialog) {
                GameModeDialog { selectedType ->
                    questionType = selectedType
                    question = generateQuestion(score, questionType!!)
                    showDialog = false
                }
            }

            if (isGameOver) {
                GameOverScreen(score, correctAnswers, wrongAnswers) {
                    score = 0
                    correctAnswers = 0
                    wrongAnswers = 0
                    totalAnswers = 0
                    question = generateQuestion(score, questionType!!)
                    isGameOver = false
                }
            } else {
                question?.let {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("Math Master", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = textColor)
                        Spacer(modifier = Modifier.height(20.dp))

                        Text(it.text, fontSize = 24.sp, fontWeight = FontWeight.Medium, color = textColor)
                        Spacer(modifier = Modifier.height(16.dp))

                        it.options.forEach { answer ->
                            AnswerButton(answer.toString(), answer == it.correctAnswer) {
                                if (answer == it.correctAnswer) {
                                    score += 2
                                    correctAnswers++
                                } else {
                                    score -= 1
                                    wrongAnswers++
                                }
                                totalAnswers++

                                if (score >= 200) {
                                    isGameOver = true
                                } else {
                                    question = generateQuestion(score, questionType!!)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))
                        Text("Score: $score", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = textColor)
                        Text("Accuracy: ${calculateAccuracy(correctAnswers, totalAnswers)}%", fontSize = 16.sp, color = textColor)
                    }
                }
            }
        }
    }
}

@Composable
fun GameModeDialog(onSelect: (String) -> Unit) {
    Dialog(onDismissRequest = {}) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Choose Mode", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Spacer(modifier = Modifier.height(12.dp))

                val operations = listOf("+", "-", "×", "÷")
                operations.forEach { operation ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(10.dp))
                            .clickable { onSelect(operation) }
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(operation, fontSize = 20.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onPrimary)
                    }
                }
            }
        }
    }
}

@Composable
fun AnswerButton(text: String, isCorrect: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text, fontSize = 20.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onPrimary)
    }
}

@Composable
fun GameOverScreen(score: Int, correct: Int, wrong: Int, onRestart: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Game Over!", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Final Score: $score", fontSize = 24.sp, color = MaterialTheme.colorScheme.onBackground)
        Text("Correct: $correct | Wrong: $wrong", fontSize = 18.sp, color = MaterialTheme.colorScheme.onBackground)
        Text("Accuracy: ${calculateAccuracy(correct, correct + wrong)}%", fontSize = 18.sp, color = MaterialTheme.colorScheme.onBackground)
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onRestart,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text("Restart", color = MaterialTheme.colorScheme.onPrimary)
        }
    }
}

data class MathQuestion(val text: String, val correctAnswer: Int, val options: List<Int>)

fun generateQuestion(score: Int, type: String): MathQuestion {
    val level = when {
        score < 25 -> 10
        score < 50 -> 25
        score < 100 -> 50
        else -> 100
    }

    val num1 = Random.nextInt(1, level)
    val num2 = Random.nextInt(1, level)

    val correctAnswer = when (type) {
        "+" -> num1 + num2
        "-" -> num1 - num2
        "×" -> num1 * num2
        "÷" -> if (num2 != 0) num1 / num2 else 1
        else -> 0
    }

    val options = (listOf(correctAnswer) + List(2) {
        val wrong = Random.nextInt(correctAnswer - 10, correctAnswer + 10)
        if (wrong == correctAnswer) wrong + 1 else wrong
    }).shuffled()

    return MathQuestion("$num1 $type $num2 =", correctAnswer, options)
}

fun calculateAccuracy(correct: Int, total: Int): String {
    return if (total == 0) "100" else "%.2f".format((correct.toDouble() / total.toDouble()) * 100)
}
