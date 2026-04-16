package com.example.project222.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SlowMotionVideo
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

sealed class BottomNavItem(val route: String, val icon: ImageVector, val label: String) {
    object Home : BottomNavItem("home", Icons.Default.Home, "Home")
    object Reels : BottomNavItem("reels", Icons.Default.SlowMotionVideo, "Reels")
    object Favorites : BottomNavItem("favorites", Icons.Default.Favorite, "Favorites")
    object Profile : BottomNavItem("profile", Icons.Default.Person, "Profile")
}

@Composable
fun BubbleBottomBar(
    selectedRoute: String,
    onItemSelected: (BottomNavItem) -> Unit
) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Reels,
        BottomNavItem.Favorites,
        BottomNavItem.Profile
    )

    val liquidBlack = Color(0xFF0A0A0A)
    val greyStructure = Color(0xFF333333)
    val goldenrod = Color(0xFFDAA520)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 24.dp)
            .navigationBarsPadding()
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(62.dp).width(66.dp),

            // Highly curved top edge for a modern "dock" feel
            shape = RoundedCornerShape(
                topStart = 40.dp, 
                topEnd = 40.dp, 
                bottomStart = 25.dp, 
                bottomEnd = 25.dp
            ),
            color = liquidBlack,
            border = BorderStroke(1.dp, greyStructure),
            tonalElevation = 12.dp,
            shadowElevation = 8.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEach { item ->
                    BubbleBottomNavItem(
                        item = item,
                        isSelected = selectedRoute == item.route,
                        activeColor = goldenrod,
                        onClick = { onItemSelected(item) }
                    )
                }
            }
        }
    }
}

@Composable
fun BubbleBottomNavItem(
    item: BottomNavItem,
    isSelected: Boolean,
    activeColor: Color,
    onClick: () -> Unit
) {
    val transition = updateTransition(isSelected, label = "selected transition")
    
    val backgroundScale by transition.animateFloat(
        transitionSpec = {
            if (false isTransitioningTo true) {
                spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
            } else {
                spring(stiffness = Spring.StiffnessVeryLow)
            }
        },
        label = "background scale"
    ) { if (it) 1f else 0.8f }

    val backgroundColor by transition.animateColor(label = "background color") {
        if (it) activeColor.copy(alpha = 0.15f) else Color.Transparent
    }

    val contentColor by transition.animateColor(label = "content color") {
        if (it) activeColor else Color.Gray
    }

    Box(
        modifier = Modifier
            .height(48.dp)
            .clip(CircleShape)
            .background(backgroundColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.label,
                tint = contentColor,
                modifier = Modifier
                    .size(24.dp)
                    .scale(if (isSelected) 1.1f else 1f)
            )
            
            AnimatedVisibility(
                visible = isSelected,
                enter = fadeIn() + expandHorizontally(expandFrom = Alignment.Start),
                exit = fadeOut() + shrinkHorizontally(shrinkTowards = Alignment.Start)
            ) {
                Row {
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = item.label,
                        color = contentColor,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                }
            }
        }
        
        // Water/Liquid flow effect indicator at the bottom
        if (isSelected) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 6.dp)
                    .width(14.dp)
                    .height(3.dp)
                    .clip(CircleShape)
                    .background(activeColor)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BubbleBottomNavItemPreview() {
    val goldenrod = Color(0xFFDAA520)
    Column(
        modifier = Modifier
            .padding(16.dp)
            .background(Color(0xFF0A0A0A)),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        BubbleBottomNavItem(
            item = BottomNavItem.Home,
            isSelected = true,
            activeColor = goldenrod,
            onClick = {}
        )
        BubbleBottomNavItem(
            item = BottomNavItem.Reels,
            isSelected = false,
            activeColor = goldenrod,
            onClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun BubbleBottomBarPreview() {
    BubbleBottomBar(
        selectedRoute = BottomNavItem.Home.route,
        onItemSelected = {}
    )
}
