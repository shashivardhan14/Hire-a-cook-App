package com.example.project222.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.project222.model.Cook
import com.example.project222.ui.components.CookListItem
import com.example.project222.ui.components.CustomizableTree
import com.example.project222.ui.viewmodel.UserViewModel

private val LiquidShape = GenericShape { size, _ ->
    moveTo(0f, 0f)
    lineTo(size.width, 0f)
    lineTo(size.width, size.height - 40f)
    cubicTo(
        size.width * 0.75f, size.height,
        size.width * 0.25f, size.height - 80f,
        0f, size.height - 20f
    )
    close()
}

@Composable
fun FavoritesScreen(
    userViewModel: UserViewModel = viewModel(),
    onCookClick: (Cook) -> Unit
) {
    var showFavoriteList by remember { mutableStateOf(false) }

    if (showFavoriteList) {
        FavoriteListScreen(
            userViewModel = userViewModel,
            onBack = { showFavoriteList = false },
            onCookClick = onCookClick
        )
    } else {
        val favoriteChefs by userViewModel.favoriteChefs.collectAsState()
        val allChefs by userViewModel.allChefs.collectAsState()
        
        // Map User objects from Firebase to Cook objects for the UI
        val favoriteCooks = favoriteChefs.map { user ->
            Cook(
                id = user.id,
                name = user.name,
                specialty = user.specialty ?: "Chef",
                rating = 5.0, // Default
                pricePerHour = user.pricePerHour ?: 0.0,
                imageUrl = user.profilePicUrl ?: "https://www.pngitem.com/pimgs/m/146-1468479_my-profile-icon-blank-profile-picture-circle-hd.png",
                description = user.bio ?: "",
                reviews = 0
            )
        }

        val availableCooks = allChefs.map { user ->
            Cook(
                id = user.id,
                name = user.name,
                specialty = user.specialty ?: "Chef",
                rating = 5.0,
                pricePerHour = user.pricePerHour ?: 0.0,
                imageUrl = user.profilePicUrl ?: "https://www.pngitem.com/pimgs/m/146-1468479_my-profile-icon-blank-profile-picture-circle-hd.png",
                description = user.bio ?: "",
                reviews = 0
            )
        }

        FavoritesContent(
            favoriteCooks = favoriteCooks, 
            availableCooks = availableCooks,
            onCookClick = onCookClick,
            onFavoriteClick = { showFavoriteList = true }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesContent(
    favoriteCooks: List<Cook>,
    availableCooks: List<Cook>,
    onCookClick: (Cook) -> Unit,
    onFavoriteClick: () -> Unit
) {
    val liquidBlack = Color(0xFF0A0A0A)
    val goldenrod = Color(0xFFDAA520)

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
                shape = LiquidShape,
                color = liquidBlack,
                shadowElevation = 12.dp,
                tonalElevation = 8.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            ) {
                TopAppBar(
                    modifier = Modifier.statusBarsPadding(),
                    title = {
                        Text(
                            "Culinary Roots",
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFF1A1A1A), Color.Black)
                    )
                )
        ) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(600.dp)
                ) {
                    // Use the CustomizableTree template which includes the artistic leaves
                    CustomizableTree(
                        items = favoriteCooks,
                        modifier = Modifier.fillMaxSize(),
                        leafArtColor = Color(0xFF2E7D32) // Artistic green leaves
                    ) { cook, _, _ ->
                        TreeLeafItem(
                            cook = cook,
                            onCookClick = onCookClick
                        )
                    }

                    if (favoriteCooks.isEmpty()) {
                        Text(
                            text = "No favorites added yet",
                            color = Color.Gray,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }

                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 15.dp)
                            .graphicsLayer {
                                val scale = 1f + (sway * 0.04f)
                                scaleX = scale
                                scaleY = scale
                            }
                            .clickable { onFavoriteClick() },
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
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Favorite,
                                    contentDescription = null,
                                    tint = Color.Red,
                                    modifier = Modifier.size(35.dp)
                                )
                            }
                        }
                        Text(
                            "Favorite Tree",
                            color = goldenrod,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(top = 6.dp)
                        )
                    }
                }
            }

            item {
                Text(
                    text = "Discover More Chefs",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(16.dp)
                )
            }

            items(availableCooks) { cook ->
                CookListItem(cook = cook, onClick = { onCookClick(cook) })
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
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
                modifier = Modifier.fillMaxSize().clip(CircleShape),
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

@Preview(showBackground = true)
@Composable
fun FavoritesScreenPreview() {
    MaterialTheme {
        FavoritesContent(
            favoriteCooks = listOf(
                Cook("1", "Chef Maria", "Italian", 4.9, 45.0, "", "Bio", 100)
            ),
            availableCooks = listOf(
                Cook("2", "Chef John", "French", 4.7, 55.0, "", "Bio", 50)
            ),
            onCookClick = {},
            onFavoriteClick = {}
        )
    }
}
