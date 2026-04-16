package com.example.project222.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey
    val id: String = "",
    val name: String,
    val email: String,
    val password: String = "",
    val profilePicUrl: String? = null,
    val specialty: String? = null,
    val bio: String? = null,
    val pricePerHour: Double? = null,
    val availability: String? = null,
    val favoriteCookIds: List<String> = emptyList()
)
