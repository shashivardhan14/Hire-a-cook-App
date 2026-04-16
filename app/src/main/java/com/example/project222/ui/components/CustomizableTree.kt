package com.example.project222.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.sin

/**
 * A customizable tree template that places items as "leaves" on branches.
 */
@Composable
fun <T> CustomizableTree(
    items: List<T>,
    modifier: Modifier = Modifier,
    trunkColor: Color = Color(0xFF3E2723),
    branchColor: Color = Color(0xFF1B110F),
    leafArtColor: Color = Color(0xFF2E7D32),
    trunkWidth: Float = 40f,
    branchWidth: Float = 14f,
    swayIntensity: Float = 15f,
    swayDurationMillis: Int = 3000,
    leafContent: @Composable BoxScope.(item: T, index: Int, sway: Float) -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "tree sway")
    val sway by infiniteTransition.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(swayDurationMillis, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sway"
    )

    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height
            val swayOffset = sway * swayIntensity

            // Draw Trunk
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
                color = trunkColor,
                style = Stroke(width = trunkWidth, cap = StrokeCap.Round)
            )

            // Draw Branches
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
                    color = trunkColor,
                    style = Stroke(width = branchWidth, cap = StrokeCap.Round)
                )

                // Add multiple artistic leaf clusters along the branch length
                // 25% point
                val q1X = startX * 0.75f + end.x * 0.25f + swayOffset * 0.25f
                val q1Y = startY * 0.75f + end.y * 0.25f
                drawSmallLeaves(Offset(q1X, q1Y), leafArtColor, sway, isSmaller = true)

                // 50% point
                val midX = (startX + end.x) / 2 + swayOffset * 0.5f
                val midY = (startY + end.y) / 2
                drawSmallLeaves(Offset(midX, midY), leafArtColor, sway, isSmaller = true)
                
                // 75% point
                val q3X = startX * 0.25f + end.x * 0.75f + swayOffset * 0.75f
                val q3Y = startY * 0.25f + end.y * 0.75f
                drawSmallLeaves(Offset(q3X, q3Y), leafArtColor, sway, isSmaller = true)
                
                // End point
                drawSmallLeaves(end, leafArtColor, sway)
            }
        }

        // Place Main Item Leaves
        items.take(6).forEachIndexed { index, item ->
            val alignment = when (index) {
                0 -> Alignment.TopStart
                1 -> Alignment.TopEnd
                2 -> Alignment.CenterStart
                3 -> Alignment.CenterEnd
                4 -> Alignment.BottomStart
                else -> Alignment.BottomEnd
            }

            val xOffset = when (index) {
                0 -> 40.dp; 1 -> (-40).dp; 2 -> 10.dp; 3 -> (-10).dp; 4 -> 5.dp; else -> (-5).dp
            }

            val yOffset = when (index) {
                0, 1 -> 150.dp; 2, 3 -> 100.dp; 4, 5 -> (-10).dp; else -> 0.dp
            }

            val leafSwayX = sway * (swayIntensity * 0.8f)
            val leafSwayY = sin(sway * Math.PI.toFloat()).toFloat() * (swayIntensity * 0.4f)

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = alignment
            ) {
                Box(
                    modifier = Modifier
                        .offset(x = xOffset + leafSwayX.dp, y = yOffset + leafSwayY.dp)
                        .graphicsLayer { rotationZ = sway * (index + 2).toFloat() }
                ) {
                    leafContent(item, index, sway)
                }
            }
        }
    }
}

private fun DrawScope.drawSmallLeaves(offset: Offset, color: Color, sway: Float, isSmaller: Boolean = false) {
    val scale = if (isSmaller) 0.7f else 1.0f
    val leafSize = Size(15f * scale, 25f * scale)
    val leafColor = color.copy(alpha = 0.7f)
    
    // Draw 3 small leaves around the offset
    for (i in 0 until 3) {
        val angle = (i * 45f) - 45f + (sway * 10f)
        val leafPath = Path().apply {
            moveTo(offset.x, offset.y)
            quadraticTo(
                offset.x - leafSize.width, offset.y - leafSize.height / 2,
                offset.x, offset.y - leafSize.height
            )
            quadraticTo(
                offset.x + leafSize.width, offset.y - leafSize.height / 2,
                offset.x, offset.y
            )
        }
        
        rotate(degrees = angle, pivot = offset) {
            drawPath(leafPath, leafColor, style = Fill)
            drawPath(leafPath, color.copy(alpha = 0.9f), style = Stroke(width = 1f))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CustomizableTreePreview() {
    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF1A1A1A))
                .padding(20.dp)
        ) {
            CustomizableTree(
                items = listOf("Leaf 1", "Leaf 2", "Leaf 3", "Leaf 4", "Leaf 5", "Leaf 6"),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(500.dp)
            ) { item, _, _ ->
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .background(Color(0xFFDAA520), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = item.takeLast(1),
                        color = Color.White,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}
