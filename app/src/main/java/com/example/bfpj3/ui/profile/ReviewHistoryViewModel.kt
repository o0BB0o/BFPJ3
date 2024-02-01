package com.example.bfpj3.ui.profile

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.bfpj3.ui.data.Review
import com.google.firebase.Timestamp
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
class ReviewHistoryViewModel : ViewModel() {
    val reviews = MutableLiveData<List<Review>>()

//    init{
//        val currentTimestamp = Timestamp.now()
//        val review1 = Review(
//            userId = "1",
//            destinationId = "A Eiffel Tower",
//            rating = 5,
//            title = "Amazing Experience",
//            description = "The view from the top is breathtaking!",
//            timestamp = getCurrentDate()
//        )
//        val review2 = Review(
//            userId = "1",
//            destinationId = "B Eiffel Tower",
//            rating = 4,
//            title = "Great but crowded",
//            description = "A must-visit place, though it gets quite crowded.",
//            timestamp = getCurrentDate()
//        )
//        reviews.value = listOf(review1, review2)
//    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun getCurrentDate(): String {
        // Get the current date
        val currentDate = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        return currentDate.format(formatter)
    }
}