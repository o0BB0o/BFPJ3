package com.example.bfpj3.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.bfpj3.ui.data.Destination
import com.example.bfpj3.ui.data.Price
import com.example.bfpj3.ui.data.Review
import com.google.firebase.Timestamp

class HomeViewModel : ViewModel() {
    val destinations = MutableLiveData<List<Destination>>()
    var selectedDestination = MutableLiveData<Destination>()
    init {
        //TODO DELETE THESE!! TEMP CARD ITEMS
        val samplePrice = Price(199.99, "USD")

        val currentTimestamp = Timestamp.now()

        val review1 = Review(
            destination = "Eiffel Tower",
            rating = 5,
            title = "Amazing Experience",
            description = "The view from the top is breathtaking!",
            timestamp = currentTimestamp
        )

        val review2 = Review(
            destination = "Eiffel Tower",
            rating = 4,
            title = "Great but crowded",
            description = "A must-visit place, though it gets quite crowded.",
            timestamp = currentTimestamp
        )

        val review3 = Review(
            destination = "Eiffel Tower",
            rating = 4,
            title = "Great but great",
            description = "A must-visit place.",
            timestamp = currentTimestamp
        )

        val eiffelTower = Destination(
            name = "Eiffel Tower",
            ownerOrganization = "City of Paris",
            location = "Paris, France",
            description = "Iconic tower offering expansive aerial views of Paris.",
            reviewList = listOf(review1, review2, review3),
            price = samplePrice,
            localLanguages = listOf("French"),
            ageRecommendation = "All ages",
            thingsTodo = listOf("Sightseeing", "Photography", "Dining"),
            tags = listOf("Historic", "Culture"),
            imageUrl = "https://marvel-b1-cdn.bc0a.com/f00000000270502/s19538.pcdn.co/wp-content/uploads/2021/07/road-trip.jpg"
        )
        val eiffelTower2 = Destination(
            name = "Eiffel Tower2",
            ownerOrganization = "City of Paris",
            location = "Paris, France",
            description = "Iconic tower offering expansive aerial views of Paris.",
            reviewList = listOf(),
            price = samplePrice,
            localLanguages = listOf("French"),
            ageRecommendation = "All ages",
            thingsTodo = listOf("Sightseeing", "Photography", "Dining"),
            tags = listOf("Historic", "Culture"),
            imageUrl = "https://marvel-b1-cdn.bc0a.com/f00000000270502/s19538.pcdn.co/wp-content/uploads/2021/07/road-trip.jpg"
        )
        destinations.value = listOf(eiffelTower, eiffelTower2)
    }
    enum class SortingOption(val displayName: String) {
        Name("Name"),
        Price("Price"),
        Ratings("Ratings")
    }

    enum class FilteringOption(val displayName: String) {
        None("None"),
        Nature("Nature"),
        Historic("Historic"),
        Culture("Culture"),
        Music("Music")
    }

    fun sortDestinations(sortOption: SortingOption) { //TODO
        when (sortOption) {
            SortingOption.Name -> { }
            SortingOption.Price -> { }
            SortingOption.Ratings -> {}
        }
    }

    fun filterProducts(filterOption: FilteringOption) { //TODO
        when (filterOption) {
            FilteringOption.None -> { }
            FilteringOption.Nature -> { }
            FilteringOption.Historic -> {}
            FilteringOption.Culture -> {}
            FilteringOption.Music -> {}
        }
    }

    fun getavgRating(d:Destination): Double {
        if (d.reviewList.isEmpty()) {
            return 0.0
        }
        val totalRating = d.reviewList.sumOf { it.rating }
        val averageRating = totalRating.toDouble() / d.reviewList.size
        return String.format("%.2f", averageRating).toDouble()
    }

    fun hasUserReviewed(destination: Destination): Boolean { // TODO
        return false
    }
}