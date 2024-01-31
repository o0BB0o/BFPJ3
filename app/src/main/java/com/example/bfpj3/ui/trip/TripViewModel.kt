package com.example.bfpj3.ui.trip

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.bfpj3.ui.data.Destination
import java.time.Duration
import java.time.LocalDate
import kotlin.time.Duration.Companion.days

@RequiresApi(Build.VERSION_CODES.O)
class TripViewModel : ViewModel() {
    val trips = MutableLiveData<List<Trip>>()
    var selectedTrip = MutableLiveData<Trip>()
    init {
        //TODO DELETE THESE!! TEMP CARD ITEMS
        val d1 = Destination("TEMP1",
            "https://marvel-b1-cdn.bc0a.com/f00000000270502/s19538.pcdn.co/wp-content/uploads/2021/07/road-trip.jpg",
            5.0, "Space", listOf("Nature", "Historic"))
        val d2 = Destination("TEMP2",
            "https://marvel-b1-cdn.bc0a.com/f00000000270502/s19538.pcdn.co/wp-content/uploads/2021/07/road-trip.jpg",
            4.0, "Space", listOf("Historic"))
        val d3 = Destination("TEMP3",
            "https://marvel-b1-cdn.bc0a.com/f00000000270502/s19538.pcdn.co/wp-content/uploads/2021/07/road-trip.jpg",
            3.0, "Space", listOf("Historic", "Science"))
        val temp1 = Trip(mutableListOf(d1, d2), 2, LocalDate.of(2023, 5, 12), LocalDate.of(2023, 5, 13), "trip1", "trip1 description", true)
        val temp2 = Trip(mutableListOf(d2, d3), 3, LocalDate.of(2018, 7, 24), LocalDate.of(2018, 7, 26), "trip2", "trip2 description", true)
        val temp3 = Trip(mutableListOf(d1, d2, d3), 4, LocalDate.of(2022, 9, 9), LocalDate.of(2022, 9, 13), "trip3", "trip3 description", false)

        trips.value = listOf(temp1, temp2, temp3)
    }

}