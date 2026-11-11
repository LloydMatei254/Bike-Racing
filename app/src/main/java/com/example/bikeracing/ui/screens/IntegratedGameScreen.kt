package com.example.bikeracing.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bikeracing.R
import com.example.bikeracing.ui.theme.*
import com.example.bikeracing.viewmodel.GameViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.math.roundToInt
import kotlin.random.Random

data class GameBike(
    var x: Float,
    var y: Float,
    var speed: Int,
    val isPlayer: Boolean = false
)

@Composable
fun IntegratedGameScreen(
    viewModel: GameViewModel,
    levelId: Int,
    onBack: () -> Unit
) {
    var score by remember { mutableStateOf(0) }
    var gameRunning by remember { mutableStateOf(true) }
    var showGameOver by remember { mutableStateOf(false) }
    var showIntro by remember { mutableStateOf(true) }
    
    val configuration = LocalConfiguration.current
    val screenWidthPx = with(LocalDensity.current) { configuration.screenWidthDp.dp.toPx() }
    val screenHeightPx = with(LocalDensity.current) { configuration.screenHeightDp.dp.toPx() }
    
    // Player bike state - smaller sizes to fit screen better
    var playerX by remember { mutableStateOf(screenWidthPx / 2 - 50f) }
    var playerY by remember { mutableStateOf(screenHeightPx - 250f) }
    val bikeWidth = 100f
    val bikeHeight = 140f
    
    // Road scrolling
    var road1Y by remember { mutableStateOf(0f) }
    var road2Y by remember { mutableStateOf(-screenHeightPx) }
    var roadSpeed by remember { mutableStateOf(15f) }
    
    // Enemy bikes - using same bike drawables
    val enemyBikes = remember {
        mutableStateListOf(
            GameBike(Random.nextFloat() * (screenWidthPx - bikeWidth), -140f, 10),
            GameBike(Random.nextFloat() * (screenWidthPx - bikeWidth), -280f, 12),
            GameBike(Random.nextFloat() * (screenWidthPx - bikeWidth), -420f, 14)
        )
    }
    
    // Intro animation countdown
    LaunchedEffect(showIntro) {
        if (showIntro) {
            delay(3000) // Show Nairobi scene for 3 seconds
            showIntro = false
        }
    }
    
    // Game loop
    LaunchedEffect(gameRunning, showIntro) {
        while (isActive && gameRunning && !showIntro) {
            // Scroll road
            road1Y += roadSpeed
            road2Y += roadSpeed
            
            if (road1Y >= screenHeightPx) {
                road1Y = road2Y - screenHeightPx
            }
            if (road2Y >= screenHeightPx) {
                road2Y = road1Y - screenHeightPx
            }
            
            // Move enemy bikes
            enemyBikes.forEachIndexed { index, enemy ->
                enemy.y += enemy.speed
                
                if (enemy.y > screenHeightPx) {
                    enemy.y = -140f
                    enemy.x = Random.nextFloat() * (screenWidthPx - bikeWidth)
                    score++
                    
                    // Increase difficulty
                    if (score % 5 == 0) {
                        val speedIncrement = (1 + score / 20).coerceAtMost(10)
                        enemy.speed += speedIncrement
                        roadSpeed += speedIncrement
                    }
                }
            }
            
            // Check collisions
            val padding = 20f
            val playerRect = androidx.compose.ui.geometry.Rect(
                playerX + padding,
                playerY + padding,
                playerX + bikeWidth - padding,
                playerY + bikeHeight - padding
            )
            
            for (enemy in enemyBikes) {
                val enemyRect = androidx.compose.ui.geometry.Rect(
                    enemy.x + padding,
                    enemy.y + padding,
                    enemy.x + bikeWidth - padding,
                    enemy.y + bikeHeight - padding
                )
                
                if (playerRect.overlaps(enemyRect)) {
                    gameRunning = false
                    showGameOver = true
                    break
                }
            }
            
            delay(30)
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF2a2a2a))
    ) {
        if (showIntro) {
            // Show Nairobi intro scene
            NairobiIntroScene(
                modifier = Modifier.fillMaxSize()
            )
            
            // Countdown overlay
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = DarkCard.copy(alpha = 0.9f),
                    shadowElevation = 16.dp
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "NAIROBI CITY",
                            color = NeonOrange,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 3.sp
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "GET READY!",
                            color = TextPrimary,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp
                        )
                    }
                }
            }
        } else {
            // Main game - Road backgrounds scrolling
            Image(
                painter = painterResource(id = R.drawable.road),
                contentDescription = "Road",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(with(LocalDensity.current) { screenHeightPx.toDp() })
                    .offset { IntOffset(0, road1Y.roundToInt()) },
                contentScale = ContentScale.FillBounds
            )
            
            Image(
                painter = painterResource(id = R.drawable.road),
                contentDescription = "Road",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(with(LocalDensity.current) { screenHeightPx.toDp() })
                    .offset { IntOffset(0, road2Y.roundToInt()) },
                contentScale = ContentScale.FillBounds
            )
            
            // Enemy bikes using bikeblue.png
            enemyBikes.forEach { enemy ->
                Image(
                    painter = painterResource(id = R.drawable.bikeblue),
                    contentDescription = "Enemy Bike",
                    modifier = Modifier
                        .size(100.dp, 140.dp)
                        .offset {
                            IntOffset(
                                enemy.x.roundToInt(),
                                enemy.y.roundToInt()
                            )
                        }
                )
            }
            
            // Player bike using redbike.png
            Image(
                painter = painterResource(id = R.drawable.redbike),
                contentDescription = "Player Bike",
                modifier = Modifier
                    .size(100.dp, 140.dp)
                    .offset {
                        IntOffset(
                            playerX.roundToInt(),
                            playerY.roundToInt()
                        )
                    }
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            
                            if (gameRunning && !showIntro) {
                                val newX = (playerX + dragAmount.x).coerceIn(
                                    0f,
                                    screenWidthPx - bikeWidth
                                )
                                val newY = (playerY + dragAmount.y).coerceIn(
                                    0f,
                                    screenHeightPx - bikeHeight
                                )
                                
                                playerX = newX
                                playerY = newY
                            }
                        }
                    }
            )
            
            // Score display
            Surface(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 40.dp),
                shape = RoundedCornerShape(20.dp),
                color = DarkCard.copy(alpha = 0.9f),
                shadowElevation = 8.dp
            ) {
                Text(
                    text = "SCORE: $score",
                    color = NeonOrange,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)
                )
            }
        }
        
        // Game Over Dialog
        if (showGameOver) {
            GameOverDialog(
                score = score,
                onRestart = {
                    // Reset game
                    score = 0
                    gameRunning = true
                    showGameOver = false
                    showIntro = true
                    roadSpeed = 15f
                    playerX = screenWidthPx / 2 - 50f
                    playerY = screenHeightPx - 250f
                    road1Y = 0f
                    road2Y = -screenHeightPx
                    
                    enemyBikes.clear()
                    enemyBikes.addAll(
                        listOf(
                            GameBike(Random.nextFloat() * (screenWidthPx - bikeWidth), -140f, 10),
                            GameBike(Random.nextFloat() * (screenWidthPx - bikeWidth), -280f, 12),
                            GameBike(Random.nextFloat() * (screenWidthPx - bikeWidth), -420f, 14)
                        )
                    )
                },
                onExit = {
                    onBack()
                }
            )
        }
    }
}

@Composable
private fun NairobiIntroScene(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                androidx.compose.ui.graphics.Brush.verticalGradient(
                    listOf(
                        Color(0xFF1a1a2e),
                        Color(0xFF16213e),
                        Color(0xFF0f3460)
                    )
                )
            )
    ) {
        // City skyline silhouette
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            
            // Draw simple building silhouettes
            for (i in 0..10) {
                val buildingX = i * w / 11f
                val buildingHeight = (h * 0.3f) + Random.nextFloat() * h * 0.3f
                val buildingWidth = w / 12f
                
                drawRect(
                    color = Color(0xFF0f0f0f).copy(alpha = 0.7f),
                    topLeft = Offset(buildingX, h - buildingHeight),
                    size = androidx.compose.ui.geometry.Size(buildingWidth, buildingHeight)
                )
                
                // Windows
                for (row in 0..5) {
                    for (col in 0..2) {
                        if (Random.nextFloat() > 0.3f) {
                            drawRect(
                                color = Color(0xFFffb300).copy(alpha = 0.6f),
                                topLeft = Offset(
                                    buildingX + col * buildingWidth / 4f + 5f,
                                    h - buildingHeight + row * 30f + 10f
                                ),
                                size = androidx.compose.ui.geometry.Size(15f, 20f)
                            )
                        }
                    }
                }
            }
        }
        
        // Nairobi text overlay
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                "🏙️",
                fontSize = 64.sp
            )
            Spacer(Modifier.height(16.dp))
            Text(
                "NAIROBI",
                color = NeonOrange,
                fontSize = 48.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 8.sp
            )
            Text(
                "CITY RACING",
                color = TextPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 4.sp
            )
        }
    }
}

@Composable
private fun GameOverDialog(
    score: Int,
    onRestart: () -> Unit,
    onExit: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { },
        containerColor = DarkCard,
        title = {
            Text(
                "GAME OVER!",
                color = RacingRed,
                fontWeight = FontWeight.Black,
                fontSize = 32.sp,
                letterSpacing = 3.sp,
                modifier = Modifier.fillMaxWidth(),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "FINAL SCORE",
                    color = TextSecondary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    "$score",
                    color = NeonOrange,
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Black
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onRestart,
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeonOrange
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(
                    "PLAY AGAIN",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onExit,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = TextPrimary
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(
                    "EXIT",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
            }
        }
    )
}
