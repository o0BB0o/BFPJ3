package com.example.bfpj3.ui.profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.bfpj3.ui.data.Review
import com.google.firebase.Timestamp

class ReviewHistoryViewModel : ViewModel() {
    val reviews = MutableLiveData<List<Review>>()

    init{
        val currentTimestamp = Timestamp.now()
        val review1 = Review(
            userId = "1",
            destination = "A Eiffel Tower",
            rating = 5,
            title = "Amazing Experience",
            description = "The view from the top is breathtaking!",
            timestamp = currentTimestamp
        )
        val review2 = Review(
            userId = "1",
            destination = "B Eiffel Tower",
            rating = 4,
            title = "Great but crowded",
            description = "A must-visit place, though it gets quite crowded.",
            timestamp = currentTimestamp
        )
        reviews.value = listOf(review1, review2)
    }
}