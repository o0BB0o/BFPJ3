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
) {
    fun doesMatchSearchQuery(query:String):Boolean {
        val matchingCombinations = listOf(
            name,
            name.split(" ")[0],
            location,
            location.split(",")[0]
        )
        return matchingCombinations.any {
            it.contains(query, ignoreCase = true)
        }
    }
}

data class Price(
    val value: Double,
    val currency: String
)

data class Review(
    var reviewId: String,
    var userId: String,
    var destinationId: String,
    var rating: Int,
    var title: String,
    var description: String,
    var timestamp: String
)