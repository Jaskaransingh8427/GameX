package com.js.games.games.TicTacToe

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.js.games.ButtonEffect
import com.js.games.CustomTopBar
import com.js.games.GameUniversalButton
import com.js.games.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun TicTacToeScreen(navController: NavController) {
    var board by remember { mutableStateOf(List(3) { MutableList(3) { "" } }) }
    var currentPlayer by remember { mutableStateOf("X") }
    var winner by remember { mutableStateOf<String?>(null) }
    var isComputing by remember { mutableStateOf(false) }
    val ai = remember { ComputerAI() }
    val scope = rememberCoroutineScope()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CustomTopBar(
                title = "Tic-Tac-Toe",
                showBackButton = true,
                customIconRes = R.drawable.tic_tac_toe,
                backgroundColor = Color.Transparent,
                titleColor = MaterialTheme.colorScheme.primary,
                enableGestures = true,
                showDefaultButtons = true,
                navController = navController
            )
        }
    ) { padding ->

        fun checkWinner(board: List<List<String>>): String? {
            val size = board.size
            val lines = mutableListOf<List<Pair<Int, Int>>>()

            for (i in 0 until size) {
                lines.add(List(size) { j -> i to j }) // Rows
                lines.add(List(size) { j -> j to i }) // Columns
            }
            lines.add(List(size) { i -> i to i }) // Main diagonal
            lines.add(List(size) { i -> i to (size - 1 - i) }) // Anti-diagonal

            for (line in lines) {
                if (line.all { board[it.first][it.second] == "X" }) return "X"
                if (line.all { board[it.first][it.second] == "O" }) return "O"
            }
            return null
        }

        fun resetGame() {
            board = List(3) { MutableList(3) { "" } }
            currentPlayer = "X"
            winner = null
            isComputing = false
        }

        fun makeMove(row: Int, col: Int) {
            if (board[row][col].isEmpty() && winner == null && !isComputing) {
                board = board.toMutableList().also { it[row][col] = "X" }
                winner = checkWinner(board)

                if (winner == null) {
                    isComputing = true
                    scope.launch {
                        delay(500) // Simulate AI thinking
                        val (aiRow, aiCol) = ai.getBestMove(board)
                        if (aiRow != -1) {
                            board = board.toMutableList().also { it[aiRow][aiCol] = "O" }
                            winner = checkWinner(board)
                        }
                        isComputing = false
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                //.background(Color.Black)
                .padding(padding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            board.forEachIndexed { rowIndex, row ->
                Row {
                    row.forEachIndexed { colIndex, cell ->
                        Button(
                            onClick = { makeMove(rowIndex, colIndex) },
                            enabled = cell.isEmpty() && winner == null && !isComputing,
                            modifier = Modifier
                                .size(100.dp)
                                .padding(4.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = when (cell) {
                                    "X" -> Color.Green
                                    "O" -> Color.Blue
                                    else -> Color.LightGray
                                },
                                disabledContainerColor = when (cell) {
                                    "X" -> Color.Green
                                    "O" -> Color.Blue
                                    else -> Color.Gray
                                }
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(text = cell, fontSize = 24.sp, color = Color.White)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            winner?.let {
                Text(
                    "Winner: $it!",
                    fontSize = 24.sp,
                    color = if (it == "X") Color.Green else Color.Red
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            GameUniversalButton(
                text = "Reset Game",
                onClick = { resetGame() },
                effect = ButtonEffect.ShineSweep,
                modifier = Modifier
                    .width(250.dp)
                    .height(50.dp)
            )

        }
    }
}
