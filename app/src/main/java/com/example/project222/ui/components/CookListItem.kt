package com.example.project222.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.project222.model.Cook

@Composable
fun CookListItem(
    cook: Cook,
    onClick: () -> Unit
) {
    val liquidBlack = Color(0xFF0A0A0A)
    val greyStructure = Color(0xFF333333)
    val goldenrod = Color(0xFFDAA520)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
        shape = RoundedCornerShape(24.dp)
    ) {
        Box(
            modifier = Modifier
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(liquidBlack, Color(0xFF1A1A1A))
                    )
                )
                .drawBehind {
                    // Realistic Grey Line Pattern
                    val step = 30.dp.toPx()
                    val lineColor = Color.Gray.copy(alpha = 0.1f)
                    val strokeWidth = 1.dp.toPx()
                    
                    // Drawing diagonal lines
                    var x = -size.height
                    while (x < size.width) {
                        drawLine(
                            color = lineColor,
                            start = Offset(x, 0f),
                            end = Offset(x + size.height, size.height),
                            strokeWidth = strokeWidth
                        )
                        x += step
                    }
                }
                .border(
                    BorderStroke(1.dp, greyStructure),
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = cook.imageUrl,
                    contentDescription = "Profile picture of ${cook.name}",
                    modifier = Modifier
                        .size(85.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .border(3.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Crop
                )
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = cook.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = cook.specialty,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.LightGray
                    )
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = goldenrod,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "${cook.rating} (${cook.reviews} reviews)",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.LightGray,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                }
                
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "$${cook.pricePerHour}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = goldenrod
                    )
                    Text(
                        text = "/hr",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.LightGray
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CookListItemPreview() {
    val sampleCook = Cook(
        id = "1",
        name = "Chef Maria Garcia",
        specialty = "Italian & Mediterranean",
        rating = 4.8,
        reviews = 124,
        pricePerHour = 45.0,
        imageUrl = "https://images.unsplash.com/photo-1577219491135-ce391730fb2c",
        description = "Expert in homemade pasta and seafood."
    )

    MaterialTheme {
        CookListItem(
            cook = sampleCook,
            onClick = { /* Do nothing in preview */ }
        )
    }
}
