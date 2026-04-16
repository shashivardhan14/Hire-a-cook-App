package com.example.project222.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.project222.data.local.User
import com.example.project222.model.Cook
import com.example.project222.ui.components.CookListItem
import com.example.project222.ui.viewmodel.UserViewModel

data class CategoryItem(val name: String, val icon: ImageVector)

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
fun CookListScreen(
    userViewModel: UserViewModel = viewModel(),
    onCookClick: (Cook) -> Unit
) {
    val currentUser by userViewModel.currentUser.collectAsState()
    val allChefs by userViewModel.allChefs.collectAsState()
    
    // Convert User objects from Firebase to Cook objects for the UI
    val cooks = allChefs.map { user ->
        Cook(
            id = user.id,
            name = user.name,
            specialty = user.specialty ?: "Chef",
            rating = 5.0, // Default for new users
            pricePerHour = user.pricePerHour ?: 0.0,
            imageUrl = user.profilePicUrl ?: "https://www.pngitem.com/pimgs/m/146-1468479_my-profile-icon-blank-profile-picture-circle-hd.png",
            description = user.bio ?: "",
            reviews = 0
        )
    }

    CookListContent(
        currentUser = currentUser, 
        availableCooks = cooks,
        onCookClick = onCookClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CookListContent(
    currentUser: User?,
    availableCooks: List<Cook>,
    onCookClick: (Cook) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var selectedCategory by remember { mutableStateOf("All") }

    val tabs = listOf("All", "Top Rated", "Affordable")

    val categories = listOf(
        CategoryItem("All", Icons.AutoMirrored.Filled.List),
        CategoryItem("Indian", Icons.Default.RestaurantMenu),
        CategoryItem("American", Icons.Default.LunchDining),
        CategoryItem("Japanese", Icons.Default.RamenDining),
        CategoryItem("Chinese", Icons.Default.RiceBowl),
        CategoryItem("Italian", Icons.Default.Restaurant),
        CategoryItem("Mexican", Icons.Default.DinnerDining)
    )

    val filteredCooks = remember(availableCooks, searchQuery, selectedTabIndex, selectedCategory) {
        var list = availableCooks

        // Tab Filtering
        list = when (selectedTabIndex) {
            1 -> list.filter { it.rating >= 4.5 }
            2 -> list.filter { it.pricePerHour <= 40.0 }
            else -> list
        }

        // Category Filtering
        if (selectedCategory != "All") {
            list = list.filter {
                it.specialty.contains(selectedCategory, ignoreCase = true)
            }
        }

        // Search Filtering
        if (searchQuery.isNotEmpty()) {
            list = list.filter {
                it.name.contains(searchQuery, ignoreCase = true) ||
                it.specialty.contains(searchQuery, ignoreCase = true)
            }
        }
        
        list
    }

    val liquidBlack = Color(0xFF0A0A0A)
    val goldenrod = Color(0xFFDAA520)

    Scaffold(
        topBar = {
            Surface(
                shape = LiquidShape,
                color = liquidBlack,
                shadowElevation = 12.dp,
                tonalElevation = 8.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
            ) {
                TopAppBar(
                    modifier = Modifier.statusBarsPadding(),
                    title = {
                        Column {
                            Text("Hire a Cook", fontWeight = FontWeight.Bold, color = Color.White)
                            Text(
                                "Find the best chef for your kitchen",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.LightGray
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = Color.White
                    )
                )
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            SingleChoiceSegmentedButtonRow(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .align(Alignment.CenterHorizontally)
            ) {
                tabs.forEachIndexed { index, title ->
                    SegmentedButton(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        shape = SegmentedButtonDefaults.itemShape(index = index, count = tabs.size),
                        icon = {}
                    ) {
                        Text(title)
                    }
                }
            }

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("Search by name or specialty") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                shape = MaterialTheme.shapes.medium
            )

            Text(
                text = "Popular Categories",
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                fontWeight = FontWeight.SemiBold
            )

            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                items(categories) { category ->
                    val isSelected = selectedCategory == category.name

                    Card(
                        modifier = Modifier
                            .width(76.dp)
                            .height(85.dp)
                            .clickable { selectedCategory = category.name },
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected)
                                goldenrod
                            else
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        )
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = category.icon,
                                contentDescription = null,
                                modifier = Modifier.size(22.dp),
                                tint = if (isSelected)
                                    Color.White
                                else
                                    MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = category.name,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected)
                                    Color.White
                                else
                                    MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                // Add User's own chef profile if available
                currentUser?.let { user ->
                    val userSpecialty = user.specialty
                    val userPrice = user.pricePerHour
                    if (userSpecialty != null && userPrice != null) {
                        item {
                            Text(
                                text = "Your Chef Profile",
                                style = MaterialTheme.typography.titleSmall,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                fontWeight = FontWeight.SemiBold,
                                color = goldenrod
                            )
                            val userCook = Cook(
                                id = "-1", // Special ID for user
                                name = user.name,
                                specialty = userSpecialty,
                                rating = 5.0,
                                pricePerHour = userPrice,
                                imageUrl = user.profilePicUrl ?: "https://www.pngitem.com/pimgs/m/146-1468479_my-profile-icon-blank-profile-picture-circle-hd.png",
                                description = user.bio ?: "",
                                reviews = 0
                            )
                            CookListItem(cook = userCook, onClick = { onCookClick(userCook) })

                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                color = Color.Gray.copy(alpha = 0.2f)
                            )
                        }
                    }
                }

                item {
                    Text(
                        text = "Explore Chefs",
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        fontWeight = FontWeight.SemiBold
                    )
                }

                items(filteredCooks) { cook ->
                    // Don't show current user in the explore section if they are already at the top
                    if (cook.id != "-1" && cook.id != currentUser?.id) {
                        CookListItem(cook = cook, onClick = { onCookClick(cook) })
                    }
                }

                if (filteredCooks.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No chefs found", style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CookListScreenPreview() {
    MaterialTheme {
        CookListContent(
            currentUser = User(
                id = "1",
                name = "Chef Maria",
                email = "maria@example.com",
                profilePicUrl = null,
                specialty = "Italian",
                bio = null,
                pricePerHour = 50.0,
                availability = null,
                favoriteCookIds = emptyList()
            ),
            availableCooks = listOf(
                Cook(
                    id = "2", 
                    name = "Chef John", 
                    specialty = "American", 
                    rating = 4.8, 
                    pricePerHour = 35.0, 
                    imageUrl = "", 
                    description = "Desc", 
                    reviews = 10
                )
            ),
            onCookClick = { }
        )
    }
}
