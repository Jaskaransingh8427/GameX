package com.js.games.games

import android.media.MediaPlayer
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.js.games.CustomTopBar
import com.js.games.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun MemoryMatch(navController: NavController) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Load sounds
    val clickSound = remember { MediaPlayer.create(context, R.raw.click) }
    val successSound = remember { MediaPlayer.create(context, R.raw.success) }
    val errorSound = remember { MediaPlayer.create(context, R.raw.error) }

    val imagesList = listOf("🍎", "🍌", "🍇", "🍉", "🍍", "🥑", "🍒", "🍓", "🍊", "🍈")
    var imagePairs by remember { mutableStateOf((imagesList + imagesList).shuffled()) }

    var revealedPositions by remember { mutableStateOf(setOf<Int>()) }
    var matchedPairs by remember { mutableStateOf(setOf<Int>()) }
    var firstSelection by remember { mutableStateOf<Int?>(null) }
    var secondSelection by remember { mutableStateOf<Int?>(null) }
    var clicksDisabled by remember { mutableStateOf(false) }
    var score by remember { mutableStateOf(0) }

    var isSelectingFirst by remember { mutableStateOf(true) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CustomTopBar(
                title = "Memory Match",
                showBackButton = true,
                customIconRes = R.drawable.memory_match ,
                backgroundColor = Color.Transparent,
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
                .padding(horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Score: $score",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 24.sp, modifier = Modifier.padding(16.dp))

            Text(
                text = if (isSelectingFirst) " Select the first image!" else " Select the second image!",
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            NumberGrid(imagePairs, revealedPositions, matchedPairs) { position ->
                if (clicksDisabled || revealedPositions.contains(position) || matchedPairs.contains(position)) return@NumberGrid
                clickSound.start()
                revealedPositions = revealedPositions + position

                if (firstSelection == null) {
                    firstSelection = position
                    isSelectingFirst = false
                } else {
                    secondSelection = position
                    clicksDisabled = true

                    coroutineScope.launch {
                        delay(200)

                        val firstImage = imagePairs[firstSelection!!]
                        val secondImage = imagePairs[secondSelection!!]

                        if (firstImage == secondImage) {
                            matchedPairs = matchedPairs + firstSelection!! + secondSelection!!
                            score += 10
                            successSound.start()
                        } else {
                            errorSound.start()
                            delay(1000)

                            imagePairs = shuffleNonMatched(imagePairs, matchedPairs)
                            revealedPositions = revealedPositions - firstSelection!! - secondSelection!!
                        }

                        firstSelection = null
                        secondSelection = null
                        clicksDisabled = false
                        isSelectingFirst = true
                    }
                }
            }

            if (matchedPairs.size == imagePairs.size) {
                Text(
                    text = "🎉 Congratulations! You won! 🎉",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 24.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

@Composable
fun NumberGrid(
    imagePairs: List<String>,
    revealedPositions: Set<Int>,
    matchedPairs: Set<Int>,
    onNumberClick: (Int) -> Unit
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val spacing = 12.dp
    val columns =4
    val cardSize = (screenWidth - (spacing * (columns + 1))) / columns

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = spacing / 2)
    ) {
        imagePairs.chunked(columns).forEachIndexed { rowIndex, rowItems ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = spacing / 2),
                horizontalArrangement = Arrangement.spacedBy(spacing)
            ) {
                rowItems.forEachIndexed { colIndex, image ->
                    val position = rowIndex * columns + colIndex
                    val isRevealed = revealedPositions.contains(position) || matchedPairs.contains(position)

                    Card(
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .clickable(enabled = !isRevealed) { onNumberClick(position) }
                            .width(cardSize)
                            .height(cardSize)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(if (isRevealed) Color.LightGray else Color.DarkGray),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (isRevealed) image else "❓",
                                fontSize = (cardSize.value).sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}

fun shuffleNonMatched(imagePairs: List<String>, matchedPairs: Set<Int>): List<String> {
    val nonMatchedImages = imagePairs.filterIndexed { index, _ -> index !in matchedPairs }.shuffled()
    val shuffledList = mutableListOf<String>()
    var nonMatchedIndex = 0

    for (i in imagePairs.indices) {
        if (i in matchedPairs) {
            shuffledList.add(imagePairs[i])
        } else {
            shuffledList.add(nonMatchedImages[nonMatchedIndex])
            nonMatchedIndex++
        }
    }
    return shuffledList
}
