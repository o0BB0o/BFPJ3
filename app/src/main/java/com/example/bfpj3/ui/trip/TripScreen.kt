package com.example.bfpj3.ui.trip

import android.annotation.SuppressLint
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.bfpj3.R
import com.example.bfpj3.ui.data.Destination
import com.example.bfpj3.ui.home.HomeViewModel
import com.example.bfpj3.database.FirebaseViewModel
import com.google.firebase.firestore.FirebaseFirestore

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TripScreen(navController: NavController, db: FirebaseFirestore, firebaseViewModel: FirebaseViewModel) {
    val tripViewModel: TripViewModel = viewModel(LocalContext.current as ComponentActivity)

    val trips by firebaseViewModel.allTrips.collectAsState(listOf())
    val selectedTrip by tripViewModel.selectedTrip.observeAsState()
    firebaseViewModel.getAllTrip(db)
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .padding(bottom = 60.dp)
    ) {
        // Placeholder card for adding new trips
        item {
            NewTripCard {
                navController.navigate("addNewTripScreen")
            }
        }
        // Display list of trips
        items(trips) { trip ->
            TripCard(trip, selectedTrip, tripViewModel,db,firebaseViewModel)
        }
    }
}

@Composable
fun NewTripCard(onAddTripClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .clickable(onClick = { onAddTripClick() }),
        elevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(8.dp),
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(text = "Add New Trip", style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TripCard(trip: Trip, selectedTrip: Trip?, tripViewModel: TripViewModel, db: FirebaseFirestore, firebaseViewModel: FirebaseViewModel) {
    var checked by remember { mutableStateOf(trip.isPublic) }
    val context = LocalContext.current
    val showConfirmationDialog = remember { mutableStateOf(false) }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .clickable(
                onClick = {
                    if (tripViewModel.selectedTrip.value == trip) {
                        tripViewModel.selectedTrip.value = null
                    } else {
                        tripViewModel.selectedTrip.value = trip
                    }
                }
            ),
        elevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Row for title and switch
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = trip.title, style = MaterialTheme.typography.headlineMedium)

                // Toggle switch for public/private
                Column {
                    Switch(
                        checked = checked,
                        onCheckedChange = {
                            // Update the public/private setting
                            checked = it
                            firebaseViewModel.updateIsPublicForTrip(db,trip.tripId,checked,context)
                        }
                    )

                    Text(text = if (checked) { "Public" } else { "Private "}, style = MaterialTheme.typography.bodyLarge)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Description and other trip details can be displayed here
            Text(text = trip.description)
            Text(text = "Number of People: ${trip.numOfPeople}")
            Text(text = "Start Date: ${trip.startDate}")
            Text(text = "End Date: ${trip.endDate}")

            Spacer(modifier = Modifier.height(8.dp))
            // Display list of destinations for the selected trip
            if (selectedTrip == trip) {
                DestinationList(trip,firebaseViewModel, db)
            }

            IconButton(
                onClick = {
                    // Show the confirmation dialog when the delete button is clicked
                    showConfirmationDialog.value = true
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Trip"
                )
            }

            if (showConfirmationDialog.value) {
                ConfirmationDialog(
                    onConfirm = {
                        // Perform the deletion logic here
                        // Remove the trip from the user, update the ViewModel, etc.
                        firebaseViewModel.deleteTrip(db, trip.tripId, context){}
                    },
                    onCancel = {
                        // Close the confirmation dialog
                        showConfirmationDialog.value = false
                    },
                    "trip"
                )
            }
        }
    }
}

@SuppressLint("UnrememberedMutableState")
@Composable
fun DestinationList(trip: Trip, firebaseViewModel: FirebaseViewModel, db: FirebaseFirestore) {
    val destinations = mutableStateListOf(*trip.destinations.toTypedArray())

    // State to track if the confirmation dialog should be shown
    val showConfirmationDialog = remember { mutableStateOf(false) }
    var selectedDestination by remember { mutableStateOf<Destination?>(null) }
    val context = LocalContext.current

    for (destination in destinations) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.weight(1f)) {
                SimpleDestinationCard(destination, firebaseViewModel,db,onClick = {})
            }

            IconButton(
                onClick = {
                    // Show the confirmation dialog when the delete button is clicked
                    showConfirmationDialog.value = true
                    selectedDestination = destination
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Destination"
                )
            }
        }
    }

    // Show the confirmation dialog when the state is true
    if (showConfirmationDialog.value) {
        ConfirmationDialog(
            onConfirm = {
                // Perform the deletion logic here
                // Remove the destination from the list, update the ViewModel, etc.
                destinations.remove(selectedDestination)
                trip.destinations = destinations
                firebaseViewModel.removeDestinationFromTrip(db, trip.tripId, selectedDestination!!.destinationId,context)
            },
            onCancel = {
                // Close the confirmation dialog
                showConfirmationDialog.value = false
            },
            "destination"
        )
    }
}

@Composable
fun ConfirmationDialog(
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    type: String
) {
    // UI for the confirmation dialog
    AlertDialog(
        onDismissRequest = onCancel,
        title = { Text(text = "Confirm Deletion") },
        text = { Text(text = "Are you sure you want to delete this ${type}?") },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm()
                    onCancel()
                }
            ) {
                Text(text = "Confirm")
            }
        },
        dismissButton = {
            Button(
                onClick = onCancel
            ) {
                Text(text = "Cancel")
            }
        }
    )
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalLayoutApi::class)
@Composable
fun SimpleDestinationCard(destination: Destination, firebaseViewModel: FirebaseViewModel, db: FirebaseFirestore,onClick: () -> Unit) {
    val viewModel:HomeViewModel = viewModel(LocalContext.current as ComponentActivity)
    val currentCurrency by firebaseViewModel.userCurrency.collectAsState("")
    Card(modifier = Modifier
        .padding(8.dp)
        .fillMaxWidth()
        .clickable(onClick = onClick), elevation = 4.dp) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = destination.imageUrl,
                modifier = Modifier
                    .width(50.dp)
                    .aspectRatio(1f)
                    .clip(RectangleShape),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = R.drawable.ic_launcher_foreground),
                error = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = "Destination Image")
            Column(modifier = Modifier
                .padding(8.dp)
                .weight(1f)) {
                Text(text = destination.name, style = MaterialTheme.typography.headlineSmall)
                Text(text = "Location: ${destination.location}")
                Text(text = "Price: ${viewModel.priceExchanger(destination.price, currentCurrency)}")
            }
        }
    }
}