package com.example.project222

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.project222.model.Cook
import com.example.project222.ui.components.BottomNavItem
import com.example.project222.ui.components.BubbleBottomBar
import com.example.project222.ui.screens.*
import com.example.project222.ui.theme.Project222Theme
import com.example.project222.ui.viewmodel.UserViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Project222Theme {
                val userViewModel: UserViewModel = viewModel()
                val currentUser by userViewModel.currentUser.collectAsState()
                
                var showRegistration by remember { mutableStateOf(false) }
                var selectedRoute by remember { mutableStateOf(BottomNavItem.Home.route) }
                var selectedCook by remember { mutableStateOf<Cook?>(null) }
                var showBooking by remember { mutableStateOf(false) }
                
                if (currentUser == null) {
                    if (showRegistration) {
                        RegistrationScreen(
                            viewModel = userViewModel,
                            onRegisterSuccess = { showRegistration = false },
                            onNavigateToLogin = { showRegistration = false }
                        )
                    } else {
                        LoginScreen(
                            viewModel = userViewModel,
                            onLoginSuccess = { /* currentUser will update via flow */ },
                            onNavigateToRegister = { showRegistration = true }
                        )
                    }
                } else {
                    Scaffold(
                        bottomBar = {
                            if (selectedCook == null && !showBooking) {
                                BubbleBottomBar(
                                    selectedRoute = selectedRoute,
                                    onItemSelected = { selectedRoute = it.route }
                                )
                            }
                        }
                    ) { paddingValues ->
                        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
                            if (showBooking && selectedCook != null) {
                                ChefBookingScreen(
                                    cook = selectedCook!!,
                                    onBack = { showBooking = false }
                                )
                            } else if (selectedCook != null) {
                                val isFavorite = currentUser?.favoriteCookIds?.contains(selectedCook!!.id) ?: false
                                CookDetailScreen(
                                    cook = selectedCook!!,
                                    isFavorite = isFavorite,
                                    onToggleFavorite = { userViewModel.toggleFavorite(selectedCook!!.id) },
                                    onBack = { selectedCook = null },
                                    onBookNow = { showBooking = true }
                                )
                            } else {
                                when (selectedRoute) {
                                    BottomNavItem.Home.route -> {
                                        CookListScreen(
                                            userViewModel = userViewModel,
                                            onCookClick = { selectedCook = it }
                                        )
                                    }
                                    BottomNavItem.Reels.route -> {
                                        ReelsScreen(
                                            userViewModel = userViewModel,
                                            onCookClick = { selectedCook = it }
                                        )
                                    }
                                    BottomNavItem.Favorites.route -> {
                                        FavoritesScreen(
                                            userViewModel = userViewModel,
                                            onCookClick = { selectedCook = it }
                                        )
                                    }
                                    BottomNavItem.Profile.route -> {
                                        ProfileScreen(
                                            userViewModel = userViewModel,
                                            onCookClick = { selectedCook = it },
                                            onLogout = { /* Handled in VM */ }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CenteredText(text: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = text)
    }
}
