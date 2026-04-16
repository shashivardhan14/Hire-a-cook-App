package com.example.project222.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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

@Composable
fun CookListScreen(
    userViewModel: UserViewModel = viewModel(),
    onCookClick: (Cook) -> Unit
) {
    val currentUser by userViewModel.currentUser.collectAsState()
    CookListContent(currentUser = currentUser, onCookClick = onCookClick)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CookListContent(
    currentUser: User?,
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

    // Using an empty list since sampleCooks is removed.
    val filteredCooks = emptyList<Cook>()

    val liquidBlack = Color(0xFF0A0A0A)
    val goldenrod = Color(0xFFDAA520)

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
                            .width(85.dp)
                            .height(107.dp)
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
                                modifier = Modifier.size(32.dp),
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
                                id = -1,
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

                            Text(
                                text = "Explore Chefs",
                                style = MaterialTheme.typography.titleSmall,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                items(filteredCooks) { cook ->
                    CookListItem(cook = cook, onClick = { onCookClick(cook) })
                }

                if (filteredCooks.isEmpty() && (currentUser?.specialty == null)) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No cooks found in this category", style = MaterialTheme.typography.bodyLarge)
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
                name = "Chef Maria",
                email = "maria@example.com",
                password = "",
                specialty = "Italian",
                pricePerHour = 50.0
            ),
            onCookClick = { }
        )
    }
}
