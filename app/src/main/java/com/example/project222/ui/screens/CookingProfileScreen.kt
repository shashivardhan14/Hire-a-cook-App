package com.example.project222.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.project222.data.local.User
import com.example.project222.ui.viewmodel.UserViewModel

@Composable
fun CookingProfileScreen(
    userViewModel: UserViewModel = viewModel(),
    onBack: () -> Unit
) {
    val currentUser by userViewModel.currentUser.collectAsState()
    val isUploading by userViewModel.isUploading.collectAsState()

    CookingProfileContent(
        currentUser = currentUser,
        isUploading = isUploading,
        onBack = onBack,
        onUploadProfilePicture = { userViewModel.uploadProfilePicture(it) },
        onSaveProfile = { name, specialty, price, bio, availability ->
            val priceValue = price.toDoubleOrNull() ?: 0.0
            userViewModel.updateChefProfile(name, specialty, priceValue, bio, availability) {
                onBack()
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CookingProfileContent(
    currentUser: User?,
    isUploading: Boolean,
    onBack: () -> Unit,
    onUploadProfilePicture: (Uri) -> Unit,
    onSaveProfile: (name: String, specialty: String, price: String, bio: String, availability: String) -> Unit
) {
    var name by remember(currentUser) { mutableStateOf(currentUser?.name ?: "") }
    var specialty by remember(currentUser) { mutableStateOf(currentUser?.specialty ?: "") }
    var bio by remember(currentUser) { mutableStateOf(currentUser?.bio ?: "") }
    var pricePerHour by remember(currentUser) { mutableStateOf(currentUser?.pricePerHour?.toString() ?: "") }
    var availability by remember(currentUser) { mutableStateOf(currentUser?.availability ?: "") }

    val goldenrod = Color(0xFFDAA520)

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { onUploadProfilePicture(it) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chef Profile", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { onSaveProfile(name, specialty, pricePerHour, bio, availability) }) {
                        Icon(Icons.Default.Save, contentDescription = "Save", tint = goldenrod)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile Image Section
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .clickable { launcher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (isUploading) {
                    CircularProgressIndicator(color = goldenrod)
                } else {
                    AsyncImage(
                        model = currentUser?.profilePicUrl ?: "https://www.pngitem.com/pimgs/m/146-1468479_my-profile-icon-blank-profile-picture-circle-hd.png",
                        contentDescription = "Profile Picture",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .border(2.dp, goldenrod, CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(40.dp)
                            .background(goldenrod, CircleShape)
                            .border(2.dp, Color.Black, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Change Photo",
                            tint = Color.Black,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Form Section
            Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                ChefTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = "Chef Display Name",
                    placeholder = "Enter your professional name"
                )

                ChefTextField(
                    value = specialty,
                    onValueChange = { specialty = it },
                    label = "Cuisine Specialties",
                    placeholder = "e.g. Italian, Japanese, Vegan"
                )

                ChefTextField(
                    value = pricePerHour,
                    onValueChange = { pricePerHour = it },
                    label = "Hourly Rate ($)",
                    placeholder = "e.g. 45"
                )

                ChefTextField(
                    value = availability,
                    onValueChange = { availability = it },
                    label = "Availability Time",
                    placeholder = "e.g. Mon-Fri, 9AM-5PM"
                )

                ChefTextField(
                    value = bio,
                    onValueChange = { bio = it },
                    label = "Professional Bio",
                    placeholder = "Tell clients about your culinary background...",
                    modifier = Modifier.height(150.dp),
                    singleLine = false
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = { onSaveProfile(name, specialty, pricePerHour, bio, availability) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = goldenrod),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Update Chef Profile", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            }
        }
    }
}

@Composable
fun ChefTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true
) {
    Column {
        Text(
            text = label,
            color = Color.Gray,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = modifier.fillMaxWidth(),
            placeholder = { Text(placeholder, color = Color.DarkGray) },
            singleLine = singleLine,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = Color(0xFFDAA520),
                unfocusedBorderColor = Color(0xFF333333),
                cursorColor = Color(0xFFDAA520)
            ),
            shape = RoundedCornerShape(12.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CookingProfileScreenPreview() {
    MaterialTheme {
        Surface(color = Color.Black) {
            CookingProfileContent(
                currentUser = User(name = "Chef Maria", email = "maria@example.com", password = ""),
                isUploading = false,
                onBack = {},
                onUploadProfilePicture = {},
                onSaveProfile = { _, _, _, _, _ -> }
            )
        }
    }
}
