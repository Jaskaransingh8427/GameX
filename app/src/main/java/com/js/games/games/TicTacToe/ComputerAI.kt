package com.js.games.games.TicTacToe

class ComputerAI {
    fun getBestMove(board: List<MutableList<String>>): Pair<Int, Int> {
        var bestScore = Int.MIN_VALUE
        var bestMove = -1 to -1

        for (row in board.indices) {
            for (col in board[row].indices) {
                if (board[row][col].isEmpty()) {
                    board[row][col] = "O"
                    val score = minimax(board, false)
                    board[row][col] = ""

                    if (score > bestScore) {
                        bestScore = score
                        bestMove = row to col
                    }
                }
            }
        }
        return bestMove
    }

    private fun minimax(board: List<MutableList<String>>, isMaximizing: Boolean): Int {
        val winner = checkWinner(board)
        if (winner == "O") return 10
        if (winner == "X") return -10
        if (board.all { row -> row.all { it.isNotEmpty() } }) return 0

        return if (isMaximizing) {
            var bestScore = Int.MIN_VALUE
            for (row in board.indices) {
                for (col in board[row].indices) {
                    if (board[row][col].isEmpty()) {
                        board[row][col] = "O"
                        bestScore = maxOf(bestScore, minimax(board, false))
                        board[row][col] = ""
                    }
                }
            }
            bestScore
        } else {
            var bestScore = Int.MAX_VALUE
            for (row in board.indices) {
                for (col in board[row].indices) {
                    if (board[row][col].isEmpty()) {
                        board[row][col] = "X"
                        bestScore = minOf(bestScore, minimax(board, true))
                        board[row][col] = ""
                    }
                }
            }
            bestScore
        }
    }

    private fun checkWinner(board: List<MutableList<String>>): String? {
        val size = board.size
        val lines = mutableListOf<List<Pair<Int, Int>>>()

        for (i in 0 until size) {
            lines.add(List(size) { j -> i to j })
            lines.add(List(size) { j -> j to i })
        }
        lines.add(List(size) { i -> i to i })
        lines.add(List(size) { i -> i to (size - 1 - i) })

        for (line in lines) {
            if (line.all { board[it.first][it.second] == "O" }) return "O"
            if (line.all { board[it.first][it.second] == "X" }) return "X"
        }
        return null
    }
}
