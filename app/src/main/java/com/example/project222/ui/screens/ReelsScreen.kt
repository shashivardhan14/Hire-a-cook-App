package com.example.project222.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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

data class Reel(
    val id: Int,
    val videoUrl: String,
    val authorName: String,
    val description: String,
    val audioTitle: String,
    val likes: String,
    val comments: String
)

@Composable
fun ReelsScreen(
    userViewModel: UserViewModel = viewModel(),
    onCookClick: (Cook) -> Unit
) {
    val searchResults by userViewModel.searchResults.collectAsState()
    
    ReelsContent(
        searchResults = searchResults,
        onSearchQueryChange = { userViewModel.searchUsers(it) },
        onCookClick = onCookClick
    )
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ReelsContent(
    searchResults: List<User>,
    onSearchQueryChange: (String) -> Unit,
    onCookClick: (Cook) -> Unit
) {
    var searchText by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }

    val reels = listOf(
        Reel(
            1, 
            "https://images.unsplash.com/photo-1556910103-1c02745aae4d", 
            "Chef Maria", 
            "Making authentic Italian Carbonara! #cooking #italian", 
            "Original Audio - Chef Maria",
            "12.5k", 
            "1.2k"
        ),
        Reel(
            2, 
            "https://images.unsplash.com/photo-1512058560366-1a58e25f267a", 
            "Chef Arjun", 
            "Quick Butter Chicken hack. You won't believe it! #indianfood", 
            "Indian Beats - Mashup",
            "45k", 
            "3.4k"
        )
    )

    val pagerState = rememberPagerState(pageCount = { reels.size })

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        VerticalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            ReelItem(reels[page])
        }

        // Top Search Bar for Users
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            SearchBar(
                query = searchText,
                onQueryChange = { 
                    searchText = it
                    onSearchQueryChange(it)
                },
                onSearch = { isSearchActive = false },
                active = isSearchActive,
                onActiveChange = { isSearchActive = it },
                placeholder = { Text("Search chefs...", color = Color.LightGray) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.White) },
                trailingIcon = {
                    if (isSearchActive) {
                        IconButton(onClick = { 
                            if (searchText.isNotEmpty()) {
                                searchText = ""
                                onSearchQueryChange("")
                            } else {
                                isSearchActive = false 
                            }
                        }) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                        }
                    }
                },
                colors = SearchBarDefaults.colors(
                    containerColor = if (isSearchActive) Color.Black else Color.White.copy(alpha = 0.2f),
                    inputFieldColors = TextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = Color(0xFFDAA520)
                    )
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Real Search Results from Firebase
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        Text(
                            text = if (searchText.isEmpty()) "Suggested Chefs" else "Search Results",
                            color = Color(0xFFDAA520),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                    
                    items(searchResults) { user ->
                        ListItem(
                            headlineContent = { Text(user.name, color = Color.White) },
                            supportingContent = { Text(user.email, color = Color.Gray) },
                            leadingContent = {
                                AsyncImage(
                                    model = user.profilePicUrl ?: "https://www.pngitem.com/pimgs/m/146-1468479_my-profile-icon-blank-profile-picture-circle-hd.png",
                                    contentDescription = null,
                                    modifier = Modifier.size(40.dp).clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            },
                            colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                            modifier = Modifier.clickable { 
                                val cook = Cook(
                                    id = user.id,
                                    name = user.name,
                                    specialty = user.specialty ?: "Chef",
                                    rating = 5.0,
                                    pricePerHour = user.pricePerHour ?: 0.0,
                                    imageUrl = user.profilePicUrl ?: "https://www.pngitem.com/pimgs/m/146-1468479_my-profile-icon-blank-profile-picture-circle-hd.png",
                                    description = user.bio ?: "",
                                    reviews = 0
                                )
                                onCookClick(cook)
                                isSearchActive = false
                            }
                        )
                    }
                    
                    if (searchText.isNotEmpty() && searchResults.isEmpty()) {
                        item {
                            Text(
                                "No chefs found for \"$searchText\"",
                                color = Color.Gray,
                                modifier = Modifier.padding(top = 16.dp).fillMaxWidth(),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ReelItem(reel: Reel) {
    Box(modifier = Modifier.fillMaxSize()) {
        AsyncImage(
            model = reel.videoUrl,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Overlay Gradient
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f)),
                        startY = 500f
                    )
                )
        )

        // Right side buttons
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 80.dp, end = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            ReelActionButton(Icons.Default.Favorite, reel.likes, Color.White)
            ReelActionButton(Icons.AutoMirrored.Filled.Comment, reel.comments, Color.White)
            ReelActionButton(Icons.Default.Share, "Share", Color.White)
            
            // Rotating disk placeholder
            Surface(
                modifier = Modifier.size(45.dp),
                shape = CircleShape,
                color = Color.DarkGray,
                border = androidx.compose.foundation.BorderStroke(2.dp, Color.White)
            ) {
                Icon(Icons.Default.MusicNote, contentDescription = null, modifier = Modifier.padding(10.dp))
            }
        }

        // Bottom Info
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 16.dp, bottom = 80.dp, end = 80.dp)
        ) {
            Text(
                text = "@${reel.authorName}",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = reel.description,
                color = Color.White,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.MusicNote, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = reel.audioTitle,
                    color = Color.White,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun ReelActionButton(icon: ImageVector, label: String, tint: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        IconButton(onClick = { /* Action */ }) {
            Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(32.dp))
        }
        Text(text = label, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}

@Preview(showBackground = true)
@Composable
fun ReelsScreenPreview() {
    MaterialTheme {
        ReelsContent(
            searchResults = listOf(
                User(name = "Chef Maria", email = "maria@example.com", password = ""),
                User(name = "Chef Arjun", email = "arjun@example.com", password = "")
            ),
            onSearchQueryChange = {},
            onCookClick = {}
        )
    }
}
