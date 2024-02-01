package com.example.bfpj3.ui.trip

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.bfpj3.ui.data.Destination
import com.example.bfpj3.ui.data.Price
import com.example.bfpj3.ui.data.Review
import com.google.firebase.Timestamp
import java.time.Duration
import java.time.LocalDate
import kotlin.time.Duration.Companion.days

@RequiresApi(Build.VERSION_CODES.O)
class TripViewModel : ViewModel() {
    val trips = MutableLiveData<List<Trip>>()
    var selectedTrip = MutableLiveData<Trip>()
//    init {
//        //TODO DELETE THESE!! TEMP CARD ITEMS
//        val samplePrice = Price(199.99, "USD")
//        val testPrice = Price(99.99, "USD")
//        val testPrice2 = Price(299.99, "USD")
//        val currentTimestamp = Timestamp.now().toString()
//
//        val review1 = Review(
//            userId = "1",
//            destination = "Eiffel Tower",
//            rating = 5,
//            title = "Amazing Experience",
//            description = "The view from the top is breathtaking!",
//            timestamp = currentTimestamp
//        )
//
//        val review2 = Review(
//            userId = "2",
//            destination = "Eiffel Tower",
//            rating = 4,
//            title = "Great but crowded",
//            description = "A must-visit place, though it gets quite crowded.",
//            timestamp = currentTimestamp
//        )
//
//        val review3 = Review(
//            userId = "3",
//            destination = "Eiffel Tower",
//            rating = 3,
//            title = "Great but great",
//            description = "A must-visit place.",
//            timestamp = currentTimestamp
//        )
//        val review4 = Review(
//            userId = "4",
//            destination = "Eiffel Tower",
//            rating = 2,
//            title = "Great but great",
//            description = "A must-visit place.",
//            timestamp = currentTimestamp
//        )
//
//        val eiffelTower = Destination(
//            destinationId = "test case 1",
//            name = "A Eiffel Tower",
//            ownerOrganization = "City of Paris",
//            location = "Paris, France",
//            description = "Iconic tower offering expansive aerial views of Paris.",
//            reviewList = listOf(review1, review2, review3),
//            price = samplePrice.value,
//            localLanguages = listOf("French"),
//            ageRecommendation = "All ages",
//            thingsTodo = listOf("Sightseeing", "Photography", "Dining"),
//            tags = listOf("Historic", "Culture"),
//            imageUrl = "https://marvel-b1-cdn.bc0a.com/f00000000270502/s19538.pcdn.co/wp-content/uploads/2021/07/road-trip.jpg"
//        )
//        val eiffelTower2 = Destination(
//            destinationId = "test case 2",
//            name = "B Eiffel Tower2",
//            ownerOrganization = "City of Paris",
//            location = "Paris, France",
//            description = "Iconic tower offering expansive aerial views of Paris.",
//            reviewList = listOf(review2, review3),
//            price = testPrice.value,
//            localLanguages = listOf("French"),
//            ageRecommendation = "All ages",
//            thingsTodo = listOf("Sightseeing", "Photography", "Dining"),
//            tags = listOf("Historic", "Culture"),
//            imageUrl = "https://marvel-b1-cdn.bc0a.com/f00000000270502/s19538.pcdn.co/wp-content/uploads/2021/07/road-trip.jpg"
//        )
//        val eiffelTower3 = Destination(
//            destinationId = "test case 3",
//            name = "C Eiffel Tower3",
//            ownerOrganization = "City of Paris",
//            location = "Paris, France",
//            description = "Iconic tower offering expansive aerial views of Paris.",
//            reviewList = listOf(review3,review4),
//            price = testPrice2.value,
//            localLanguages = listOf("French"),
//            ageRecommendation = "All ages",
//            thingsTodo = listOf("Sightseeing", "Photography", "Dining"),
//            tags = listOf("Music"),
//            imageUrl = "https://marvel-b1-cdn.bc0a.com/f00000000270502/s19538.pcdn.co/wp-content/uploads/2021/07/road-trip.jpg"
//        )
////        val temp1 = Trip(mutableListOf(eiffelTower, eiffelTower2), 2, LocalDate.of(2023, 5, 12), LocalDate.of(2023, 5, 13), "trip1", "trip1 description", true)
////        val temp2 = Trip(mutableListOf(eiffelTower, eiffelTower3), 3, LocalDate.of(2018, 7, 24), LocalDate.of(2018, 7, 26), "trip2", "trip2 description", true)
////        val temp3 = Trip(mutableListOf(eiffelTower, eiffelTower2, eiffelTower3), 4, LocalDate.of(2022, 9, 9), LocalDate.of(2022, 9, 13), "trip3", "trip3 description", false)
////
////        trips.value = listOf(temp1, temp2, temp3)
//    }

}