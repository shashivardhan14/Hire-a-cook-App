package com.example.project222.model

data class Cook(
    val id: String,
    val name: String,
    val specialty: String,
    val rating: Double,
    val pricePerHour: Double,
    val imageUrl: String,
    val description: String,
    val reviews: Int
)
