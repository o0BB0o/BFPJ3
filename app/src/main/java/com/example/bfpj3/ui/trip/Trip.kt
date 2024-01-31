package com.example.bfpj3.ui.trip

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.bfpj3.ui.data.Destination
import java.time.LocalDate
import java.time.temporal.ChronoUnit


@RequiresApi(Build.VERSION_CODES.O)
class Trip(
    var destinations: MutableList<Destination>,
    var numOfPeople: Int,
    var startDate: LocalDate, // Add startDate
    var endDate: LocalDate,   // Add endDate
    var title: String,
    var description: String,
    var isPublic: Boolean
) {
    var duration: Long = 0  // Duration in days

    init {
        // Calculate the duration in days based on startDate and endDate
        duration = ChronoUnit.DAYS.between(startDate, endDate)
    }
}