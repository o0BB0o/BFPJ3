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
    private val allDestinations = mutableListOf<Destination>()
    var isReversed = MutableLiveData<Boolean>(false)
    var currentSortOption = MutableLiveData<SortingOption>(SortingOption.Name)
    init {
        //TODO DELETE THESE!! TEMP CARD ITEMS
        val samplePrice = Price(199.99, "USD")
        val testPrice = Price(99.99, "USD")
        val testPrice2 = Price(299.99, "USD")
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
            rating = 3,
            title = "Great but great",
            description = "A must-visit place.",
            timestamp = currentTimestamp
        )
        val review4 = Review(
            destination = "Eiffel Tower",
            rating = 2,
            title = "Great but great",
            description = "A must-visit place.",
            timestamp = currentTimestamp
        )

        val eiffelTower = Destination(
            name = "A Eiffel Tower",
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
            name = "B Eiffel Tower2",
            ownerOrganization = "City of Paris",
            location = "Paris, France",
            description = "Iconic tower offering expansive aerial views of Paris.",
            reviewList = listOf(review2, review3),
            price = testPrice,
            localLanguages = listOf("French"),
            ageRecommendation = "All ages",
            thingsTodo = listOf("Sightseeing", "Photography", "Dining"),
            tags = listOf("Historic", "Culture"),
            imageUrl = "https://marvel-b1-cdn.bc0a.com/f00000000270502/s19538.pcdn.co/wp-content/uploads/2021/07/road-trip.jpg"
        )
        val eiffelTower3 = Destination(
            name = "C Eiffel Tower3",
            ownerOrganization = "City of Paris",
            location = "Paris, France",
            description = "Iconic tower offering expansive aerial views of Paris.",
            reviewList = listOf(review3,review4),
            price = testPrice2,
            localLanguages = listOf("French"),
            ageRecommendation = "All ages",
            thingsTodo = listOf("Sightseeing", "Photography", "Dining"),
            tags = listOf("Music"),
            imageUrl = "https://marvel-b1-cdn.bc0a.com/f00000000270502/s19538.pcdn.co/wp-content/uploads/2021/07/road-trip.jpg"
        )
        allDestinations.addAll(listOf(eiffelTower, eiffelTower2,eiffelTower3))
        destinations.value = allDestinations
    }
    enum class SortingOption(val displayName: String) {

        Name("Name"),
        Price("Price"),
        Ratings("Ratings");
    }

    enum class FilteringOption(val displayName: String) {
        None("None"),
        Nature("Nature"),
        Historic("Historic"),
        Culture("Culture"),
        Music("Music")
    }

    fun sortDestinations(sortOption: SortingOption) { //TODO
        destinations.value = when (sortOption) {
            SortingOption.Name -> allDestinations.sortedBy { it.name }
            SortingOption.Price -> allDestinations.sortedBy {it.price.value}
            SortingOption.Ratings -> allDestinations.sortedByDescending {getavgRating(it)}
        }
        destinations.value = if (isReversed.value == true) {
            destinations.value!!.reversed()
        } else {
            destinations.value
        }
    }

    fun filterProducts(filterOption: FilteringOption) { //TODO
        destinations.value = when (filterOption) {
            FilteringOption.None -> allDestinations
            else -> allDestinations.filter { filterOption.displayName in it.tags }
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
    fun toggleSortOrder(currentSortOption: SortingOption? = null) {
        isReversed.value = isReversed.value != true
        sortDestinations(currentSortOption ?: SortingOption.Name)
    }
}