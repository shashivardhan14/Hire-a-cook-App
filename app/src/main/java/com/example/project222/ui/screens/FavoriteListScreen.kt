package com.example.project222.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.project222.model.Cook
import com.example.project222.ui.components.CookListItem
import com.example.project222.ui.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteListScreen(
    userViewModel: UserViewModel,
    onBack: () -> Unit,
    onCookClick: (Cook) -> Unit
) {
    val favoriteChefs by userViewModel.favoriteChefs.collectAsState()
    
    val favoriteCooks = favoriteChefs.map { user ->
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

    val goldenrod = Color(0xFFDAA520)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Favorite Chefs", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        containerColor = Color.Black
    ) { paddingValues ->
        if (favoriteCooks.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("No favorites yet", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Header section with "Leaves" style profiles
                item {
                    Column(modifier = Modifier.padding(vertical = 16.dp)) {
                        Text(
                            text = "Favorites Gallery",
                            style = MaterialTheme.typography.titleMedium,
                            color = goldenrod,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            fontWeight = FontWeight.Bold
                        )
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(favoriteCooks) { cook ->
                                FavoriteLeafItem(cook = cook, onClick = { onCookClick(cook) })
                            }
                        }
                    }
                }

                item {
                    Text(
                        text = "Detailed List",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        fontWeight = FontWeight.Bold
                    )
                }

                items(favoriteCooks) { cook ->
                    CookListItem(cook = cook, onClick = { onCookClick(cook) })
                }
                
                item {
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
fun FavoriteLeafItem(
    cook: Cook,
    onClick: () -> Unit
) {
    val goldenrod = Color(0xFFDAA520)
    
    Column(
        modifier = Modifier
            .width(80.dp)
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            modifier = Modifier
                .size(65.dp)
                .border(2.dp, goldenrod, CircleShape),
            shape = CircleShape,
            shadowElevation = 8.dp,
            color = Color(0xFF1A1A1A)
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
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = cook.name.split(" ").first(),
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1
        )
    }
}
