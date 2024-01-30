package com.example.bfpj3.ui.data

import com.google.firebase.Timestamp

data class Destination(
    val name: String,
    val ownerOrganization: String,
    val location: String,
    val description: String,
    val reviewList: List<Review>,
    val price: Price,
    val localLanguages: List<String>,
    val ageRecommendation: String,
    val thingsTodo: List<String>,
    val tags: List<String>,
    val imageUrl: String
)

data class Price(
    val value: Double,
    val currency: String
)

data class Review(
    val userId: String,
    val destination: String,
    val rating: Int,
    val title: String,
    val description: String,
    val timestamp: Timestamp
)