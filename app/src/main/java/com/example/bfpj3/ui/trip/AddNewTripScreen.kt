package com.example.bfpj3.ui.trip

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.OutlinedTextField
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.text.isDigitsOnly
import com.example.bfpj3.database.FirebaseViewModel
import com.google.firebase.firestore.FirebaseFirestore
import java.text.DateFormat.getDateInstance
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AddNewTripScreen(db: FirebaseFirestore, firebaseViewModel: FirebaseViewModel) {
    var tripName by remember { mutableStateOf("") }
    var numOfPeople by remember { mutableStateOf("") }
    var isPublic by remember { mutableStateOf(true) }

    var showDatePicker by remember { mutableStateOf(false) }
    var selectedDatePicker by remember { mutableStateOf("start") }
    var startDate by remember { mutableStateOf(getTodayDate()) }
    var endDate by remember { mutableStateOf(getTodayDate()) }
    var context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Create New Trip",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = tripName,
            onValueChange = { tripName = it },
            label = { Text("Trip Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = numOfPeople,
            onValueChange = {
                // Ensure it's a positive integer
                if (it.isDigitsOnly() && it.isNotEmpty()) {
                    numOfPeople = it
                } else if (it.isEmpty()) {
                    numOfPeople = ""
                }
            },
            label = { Text("Number of People") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Checkbox(
                checked = isPublic,
                onCheckedChange = { isPublic = it },
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(
                text = if (isPublic) "Public" else "Private",
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Start Date OutlinedTextField with Clickable Behavior
            OutlinedTextField(
                value = startDate,
                enabled = false,
                onValueChange = {},
                label = { Text("Start Date") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        // Open Date Picker Dialog for Start Date
                        showDatePicker = true
                        selectedDatePicker = "start"
                    }
                    .weight(1f)
            )

            Spacer(modifier = Modifier.width(8.dp))

            // End Date OutlinedTextField with Clickable Behavior
            OutlinedTextField(
                value = endDate,
                enabled = false,
                onValueChange = {},
                label = { Text("End Date") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        // Open Date Picker Dialog for End Date
                        showDatePicker = true
                        selectedDatePicker = "end"
                    }
                    .weight(1f)
            )

            if (showDatePicker) {
                MyDatePickerDialog(
                    onDateSelected = {
                        if (selectedDatePicker == "start") {
                            startDate = it
                        } else if (selectedDatePicker == "end") {
                            endDate = it
                        }
                    },
                    onDismiss = { showDatePicker = false }
                )
            }
        }

        Button(
            onClick = {
//                 Create the trip and pass it to the callback
                    val newTrip = Trip(
                        userId = "",
                        tripId = "",
                        destinations = mutableListOf(),
                        numOfPeople = numOfPeople.toInt(),
                        startDate = LocalDate.parse(startDate, DateTimeFormatter.ofPattern("MMM dd, yyyy")),
                        endDate = LocalDate.parse(endDate, DateTimeFormatter.ofPattern("MMM dd, yyyy")),
                        title = tripName,
                        description = "",
                        isPublic = isPublic
                    )
                    firebaseViewModel.storeTripInfoOnTrip(db, newTrip, context)
//                onAddTrip(newTrip)
//                onDismiss()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text("Create Trip")
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun getTodayDate(): String {
    val currentDateTime = LocalDateTime.now()
    val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy")
    return currentDateTime.format(formatter)
}

@RequiresApi(Build.VERSION_CODES.O)
private fun convertMillisToDate(millis: Long): String {
    val timeZone = ZoneId.systemDefault()
    val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy")
    return Instant.ofEpochMilli(millis)
        .atZone(timeZone)
        .toLocalDate()
        .format(formatter)
}
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyDatePickerDialog(
    onDateSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState()

    val selectedDate = datePickerState.selectedDateMillis?.let {
        convertMillisToDate(it)
    } ?: ""

    DatePickerDialog(
        onDismissRequest = { onDismiss() },
        confirmButton = {
            Button(onClick = {
                onDateSelected(selectedDate)
                onDismiss()
            }) {
                Text(text = "OK")
            }
        },
        dismissButton = {
            Button(onClick = {
                onDismiss()
            }) {
                Text(text = "Cancel")
            }
        }
    ) {
        DatePicker(
            state = datePickerState
        )
    }
}