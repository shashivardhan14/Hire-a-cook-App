package com.example.project222.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.project222.data.local.User
import com.example.project222.model.Cook
import com.example.project222.ui.viewmodel.UserViewModel

enum class ProfileTab(val icon: ImageVector) {
    SETTINGS(Icons.Default.Settings),
    NOTIFICATIONS(Icons.Default.Notifications),
    VIDEO(Icons.Default.VideoCall),
    SHARE(Icons.Default.Share)
}

@Composable
fun ProfileScreen(
    userViewModel: UserViewModel = viewModel(),
    onCookClick: (Cook) -> Unit = {},
    onLogout: () -> Unit = {}
) {
    var showCookingProfile by remember { mutableStateOf(false) }
    var showFavoriteList by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableStateOf(ProfileTab.SETTINGS) }

    if (showCookingProfile) {
        CookingProfileScreen(
            userViewModel = userViewModel,
            onBack = { showCookingProfile = false }
        )
    } else if (showFavoriteList) {
        FavoriteListScreen(
            userViewModel = userViewModel,
            onBack = { showFavoriteList = false },
            onCookClick = onCookClick
        )
    } else {
        val currentUser by userViewModel.currentUser.collectAsState()
        val isUploading by userViewModel.isUploading.collectAsState()
        val error by userViewModel.error.collectAsState()
        
        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri: Uri? ->
            uri?.let { userViewModel.uploadProfilePicture(it) }
        }

        LaunchedEffect(error) {
            error?.let {
                println("Upload Error: $it")
            }
        }

        ProfileContent(
            currentUser = currentUser,
            isUploading = isUploading,
            selectedTab = selectedTab,
            onTabSelected = { selectedTab = it },
            onEditProfileClick = { showCookingProfile = true },
            onFavoritesClick = { showFavoriteList = true },
            onProfilePictureClick = { launcher.launch("image/*") },
            onLogoutClick = { 
                userViewModel.logout()
                onLogout()
            }
        )
    }
}

@Composable
fun ProfileContent(
    currentUser: User?,
    isUploading: Boolean,
    selectedTab: ProfileTab,
    onTabSelected: (ProfileTab) -> Unit,
    onEditProfileClick: () -> Unit,
    onFavoritesClick: () -> Unit,
    onProfilePictureClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    val liquidBlack = Color(0xFF0A0A0A)
    val goldenrod = Color(0xFFDAA520)
    val greyStructure = Color(0xFF333333)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Header / Profile Info
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(liquidBlack, Color.Black)
                    ),
                    shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
                )
                .padding(top = 64.dp, bottom = 32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .border(BorderStroke(2.dp, goldenrod), CircleShape)
                        .padding(4.dp)
                        .clickable { onProfilePictureClick() }
                ) {
                    if (isUploading) {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center),
                            color = goldenrod
                        )
                    } else {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(currentUser?.profilePicUrl ?: "https://www.pngitem.com/pimgs/m/146-1468479_my-profile-icon-blank-profile-picture-circle-hd.png")
                                .crossfade(true)
                                .build(),
                            contentDescription = "Profile Picture",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .size(32.dp)
                                .background(goldenrod, CircleShape)
                                .border(BorderStroke(2.dp, Color.Black), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Profile Picture",
                                tint = Color.Black,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = currentUser?.name ?: "Guest User",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    text = currentUser?.email ?: "guest@example.com",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(0.8f),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ProfileStat(label = "Bookings", value = "0")
                    ProfileStat(label = "Favorites", value = (currentUser?.favoriteCookIds?.size ?: 0).toString())
                    ProfileStat(label = "Reviews", value = "0")
                }

                Spacer(modifier = Modifier.height(24.dp))

                BubbleTabLayout(selectedTab = selectedTab, onTabSelected = onTabSelected)
            }
        }

        Box(modifier = Modifier.weight(1f)) {
            AnimatedContent(
                targetState = selectedTab,
                transitionSpec = {
                    fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(300))
                },
                label = "tabContent"
            ) { targetTab ->
                when (targetTab) {
                    ProfileTab.SETTINGS -> {
                        SettingsList(
                            goldenrod = goldenrod,
                            greyStructure = greyStructure,
                            onEditProfileClick = onEditProfileClick,
                            onFavoritesClick = onFavoritesClick,
                            onLogoutClick = onLogoutClick
                        )
                    }
                    ProfileTab.NOTIFICATIONS -> {
                        NotificationList(goldenrod = goldenrod)
                    }
                    ProfileTab.VIDEO -> {
                        VideoContent(goldenrod = goldenrod)
                    }
                    ProfileTab.SHARE -> {
                        ShareContent(goldenrod = goldenrod)
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsList(
    goldenrod: Color,
    greyStructure: Color,
    onEditProfileClick: () -> Unit,
    onFavoritesClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            SectionTitle("Account Settings")
        }
        item {
            ProfileOptionItem(
                icon = Icons.Default.Person,
                title = "Edit Profile",
                subtitle = "Change your account information",
                goldenrod = goldenrod,
                greyStructure = greyStructure,
                onClick = onEditProfileClick
            )
        }
        item {
            ProfileOptionItem(
                icon = Icons.Default.Payment,
                title = "Payment Methods",
                subtitle = "Manage your saved cards",
                goldenrod = goldenrod,
                greyStructure = greyStructure
            )
        }
        item {
            SectionTitle("Activity")
        }
        item {
            ProfileOptionItem(
                icon = Icons.Default.History,
                title = "Booking History",
                subtitle = "View your past chef hires",
                goldenrod = goldenrod,
                greyStructure = greyStructure
            )
        }
        item {
            ProfileOptionItem(
                icon = Icons.Default.Favorite,
                title = "My Favorites",
                subtitle = "Chefs you love the most",
                goldenrod = goldenrod,
                greyStructure = greyStructure,
                onClick = onFavoritesClick
            )
        }
        item {
            SectionTitle("Support")
        }
        item {
            ProfileOptionItem(
                icon = Icons.Default.Help,
                title = "Help & Support",
                subtitle = "Get assistance and FAQs",
                goldenrod = goldenrod,
                greyStructure = greyStructure
            )
        }
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onLogoutClick() },
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF1A1A1A),
                border = BorderStroke(1.dp, Color.Red.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Logout,
                        contentDescription = null,
                        tint = Color.Red,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Logout",
                        color = Color.Red,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

data class NotificationData(val id: Int, val title: String, val message: String, val time: String, val icon: ImageVector)

@Composable
fun NotificationList(goldenrod: Color) {
    val notifications = listOf(
        NotificationData(1, "New Message", "Chef Maria sent you a message.", "2m ago", Icons.Default.Message),
        NotificationData(2, "Booking Confirmed", "Your booking with Chef Arjun is confirmed.", "1h ago", Icons.Default.CheckCircle),
        NotificationData(3, "Payment Successful", "Payment for order #1234 was successful.", "3h ago", Icons.Default.Payments),
        NotificationData(4, "Special Offer", "Get 20% off on your next booking!", "1d ago", Icons.Default.LocalOffer)
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        item {
            Text(
                text = "Notifications",
                style = MaterialTheme.typography.titleLarge,
                color = goldenrod,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        items(notifications) { notification ->
            NotificationItem(notification, goldenrod)
        }
    }
}

@Composable
fun NotificationItem(notification: NotificationData, goldenrod: Color) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFF0F0F0F),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(goldenrod.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = notification.icon,
                    contentDescription = null,
                    tint = goldenrod,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = notification.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(text = notification.message, color = Color.Gray, fontSize = 13.sp)
                Text(text = notification.time, color = goldenrod.copy(alpha = 0.6f), fontSize = 11.sp, modifier = Modifier.padding(top = 4.dp))
            }
        }
    }
}

@Composable
fun VideoContent(goldenrod: Color) {
    Column(modifier = Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Icon(Icons.Default.VideoCall, contentDescription = null, tint = goldenrod, modifier = Modifier.size(64.dp))
        Spacer(modifier = Modifier.height(16.dp))
        Text("Upload Recipes", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Text("Share your cooking skills with the community.", color = Color.Gray, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = {}, colors = ButtonDefaults.buttonColors(containerColor = goldenrod)) {
            Text("Upload Video", color = Color.Black)
        }
    }
}

@Composable
fun ShareContent(goldenrod: Color) {
    Column(modifier = Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Icon(Icons.Default.Share, contentDescription = null, tint = goldenrod, modifier = Modifier.size(64.dp))
        Spacer(modifier = Modifier.height(16.dp))
        Text("Share with Friends", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Text("Invite your friends to try Project222!", color = Color.Gray, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = {}, colors = ButtonDefaults.buttonColors(containerColor = goldenrod)) {
            Text("Invite Now", color = Color.Black)
        }
    }
}

@Composable
fun BubbleTabLayout(
    selectedTab: ProfileTab,
    onTabSelected: (ProfileTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val goldenrod = Color(0xFFDAA520)
    
    var startAnim by remember { mutableStateOf(false) }
    val offsetY by animateDpAsState(
        targetValue = if (startAnim) 0.dp else (-30).dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "offsetY"
    )
    val opacity by animateFloatAsState(
        targetValue = if (startAnim) 1f else 0f,
        animationSpec = tween(800),
        label = "opacity"
    )
    
    LaunchedEffect(Unit) {
        startAnim = true
    }
    
    Row(
        modifier = modifier
            .offset(y = offsetY)
            .graphicsLayer { alpha = opacity }
            .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(32.dp))
            .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)), RoundedCornerShape(32.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ProfileTab.entries.forEach { tab ->
            val isSelected = selectedTab == tab
            Surface(
                modifier = Modifier
                    .size(40.dp)
                    .clickable { onTabSelected(tab) },
                shape = CircleShape,
                color = if (isSelected) goldenrod else goldenrod.copy(alpha = 0.1f),
                border = BorderStroke(1.dp, if (isSelected) Color.White else goldenrod.copy(alpha = 0.2f))
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = tab.icon,
                        contentDescription = null,
                        tint = if (isSelected) Color.Black else goldenrod,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ProfileStat(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold
        )
        Text(
            text = label,
            color = Color.Gray,
            fontSize = 12.sp
        )
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        color = Color(0xFFDAA520).copy(alpha = 0.7f),
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
fun ProfileOptionItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    goldenrod: Color,
    greyStructure: Color,
    onClick: () -> Unit = {}
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFF0F0F0F),
        border = BorderStroke(1.dp, greyStructure)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(goldenrod.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = goldenrod,
                    modifier = Modifier.size(20.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = subtitle,
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }
            
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = Color.Gray
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    val dummyUser = User(
        id = "1",
        name = "John Doe",
        email = "john.doe@example.com",
        favoriteCookIds = listOf("1", "2", "3")
    )
    MaterialTheme {
        ProfileContent(
            currentUser = dummyUser,
            isUploading = false,
            selectedTab = ProfileTab.SETTINGS,
            onTabSelected = {},
            onEditProfileClick = {},
            onFavoritesClick = {},
            onProfilePictureClick = {},
            onLogoutClick = {}
        )
    }
}
