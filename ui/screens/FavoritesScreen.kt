package com.example.project222.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.project222.data.local.User
import com.example.project222.model.Cook
import com.example.project222.ui.viewmodel.UserViewModel
import kotlin.math.sin

@Composable
fun FavoritesScreen(
    userViewModel: UserViewModel = viewModel(),
    onCookClick: (Cook) -> Unit
) {
    val currentUser by userViewModel.currentUser.collectAsState()
    FavoritesContent(currentUser = currentUser, onCookClick = onCookClick)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesContent(
    currentUser: User?,
    onCookClick: (Cook) -> Unit
) {
    val liquidBlack = Color(0xFF0A0A0A)
    val goldenrod = Color(0xFFDAA520)
    val brown = Color(0xFF3E2723)
    val darkBrown = Color(0xFF1B110F)
    
    // We'll eventually map currentUser.favoriteCookIds to real Cook objects here.
    // For now, it's empty as requested after removing sampleCooks.
    val favoriteCooks = emptyList<Cook>()

    val infiniteTransition = rememberInfiniteTransition(label = "tree sway")
    val sway by infiniteTransition.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sway"
    )

    Scaffold(
        topBar = {
            Surface(
                shape = RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp),
                color = liquidBlack,
                shadowElevation = 12.dp,
                tonalElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                TopAppBar(
                    title = {
                        Text(
                            "Favorite Chef Tree",
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = Color.White
                    )
                )
            }
        },
        containerColor = Color.Black
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFF1A1A1A), Color.Black)
                    )
                )
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val canvasWidth = size.width
                val canvasHeight = size.height
                val swayOffset = sway * 15f

                val trunkPath = Path().apply {
                    moveTo(canvasWidth * 0.35f, canvasHeight)
                    quadraticTo(canvasWidth * 0.5f, canvasHeight - 40f, canvasWidth * 0.45f, canvasHeight * 0.85f)
                    
                    moveTo(canvasWidth * 0.65f, canvasHeight)
                    quadraticTo(canvasWidth * 0.5f, canvasHeight - 40f, canvasWidth * 0.55f, canvasHeight * 0.85f)

                    moveTo(canvasWidth * 0.5f, canvasHeight - 10f)
                    cubicTo(
                        canvasWidth * 0.5f, canvasHeight * 0.9f,
                        canvasWidth * 0.5f + swayOffset * 0.5f, canvasHeight * 0.7f,
                        canvasWidth * 0.5f + swayOffset, canvasHeight * 0.5f
                    )
                }
                
                drawPath(
                    path = trunkPath,
                    color = brown,
                    style = Stroke(width = 40f, cap = StrokeCap.Round)
                )
                
                drawPath(
                    path = trunkPath,
                    color = darkBrown.copy(alpha = 0.6f),
                    style = Stroke(width = 12f, cap = StrokeCap.Round)
                )

                val branchPoints = listOf(
                    Offset(canvasWidth * 0.3f + swayOffset * 2f, canvasHeight * 0.15f) to 0.5f,
                    Offset(canvasWidth * 0.7f + swayOffset * 2f, canvasHeight * 0.15f) to 0.5f,
                    Offset(canvasWidth * 0.1f + swayOffset * 1.5f, canvasHeight * 0.35f) to 0.65f,
                    Offset(canvasWidth * 0.9f + swayOffset * 1.5f, canvasHeight * 0.35f) to 0.65f,
                    Offset(canvasWidth * 0.05f + swayOffset, canvasHeight * 0.65f) to 0.8f,
                    Offset(canvasWidth * 0.95f + swayOffset, canvasHeight * 0.65f) to 0.8f
                )

                branchPoints.forEach { (end, startRatio) ->
                    val startX = canvasWidth * 0.5f + swayOffset * (startRatio / 0.5f)
                    val startY = canvasHeight * startRatio
                    val branchPath = Path().apply {
                        moveTo(startX, startY)
                        quadraticTo(
                            (startX + end.x) / 2 + swayOffset, (startY + end.y) / 2,
                            end.x, end.y
                        )
                    }
                    drawPath(
                        path = branchPath,
                        color = brown,
                        style = Stroke(width = 14f, cap = StrokeCap.Round)
                    )
                }
            }

            val leafSwayX = sway * 12f
            val leafSwayY = sin(sway * Math.PI.toFloat()) * 6f

            if (favoriteCooks.isEmpty()) {
                Text(
                    text = "No favorites added yet",
                    color = Color.Gray,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                favoriteCooks.forEachIndexed { index, cook ->
                    // Simplified logic for leaf placement based on count
                    TreeLeafItem(
                        cook = cook,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .offset(x = (index * 40 - 100).dp + leafSwayX.dp, y = (index * 30 - 200).dp + leafSwayY.dp)
                            .graphicsLayer { rotationZ = sway * (index + 2)f },
                        onCookClick = onCookClick
                    )
                }
            }

            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 15.dp)
                    .graphicsLayer { 
                        val scale = 1f + (sway * 0.04f)
                        scaleX = scale
                        scaleY = scale
                    },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    modifier = Modifier
                        .size(75.dp)
                        .border(3.dp, goldenrod, CircleShape),
                    shape = CircleShape,
                    color = Color(0xFF1A1A1A),
                    shadowElevation = 15.dp
                ) {
                    // Empty heart or logo
                }
                Text(
                    "Your Culinary Roots",
                    color = goldenrod,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 6.dp)
                )
            }
        }
    }
}

@Composable
fun TreeLeafItem(
    cook: Cook,
    modifier: Modifier = Modifier,
    onCookClick: (Cook) -> Unit
) {
    val goldenrod = Color(0xFFDAA520)
    
    Column(
        modifier = modifier
            .width(85.dp)
            .clickable { onCookClick(cook) },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            modifier = Modifier
                .size(60.dp)
                .border(2.dp, goldenrod, CircleShape),
            shape = CircleShape,
            shadowElevation = 10.dp
        ) {
            AsyncImage(
                model = cook.imageUrl,
                contentDescription = cook.name,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Surface(
            color = Color.Black.copy(alpha = 0.85f),
            shape = RoundedCornerShape(10.dp),
            border = androidx.compose.foundation.BorderStroke(0.5.dp, goldenrod.copy(alpha = 0.3f))
        ) {
            Text(
                text = cook.name.split(" ").last(),
                color = Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                maxLines = 1
            )
        }
    }
}

@Preview
@Composable
fun FavoritesScreenPreview() {
    FavoritesContent(
        currentUser = User(name = "User", email = "", password = ""),
        onCookClick = {}
    )
}
