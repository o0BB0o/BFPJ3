package com.example.bfpj3.ui.data

import com.google.firebase.Timestamp

data class Destination(
val destinationId: String,
val name: String,
val ownerOrganization: String,
val location: String,
val description: String,
val reviewList: List<Review>,
val price: Double,
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
    var userId: String,
    var destination: String,
    var rating: Int,
    var title: String,
    var description: String,
    var timestamp: String
)