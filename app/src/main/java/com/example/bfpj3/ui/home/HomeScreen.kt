package com.example.bfpj3.ui.home


import android.os.Build
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Card
import androidx.compose.material.Chip
import androidx.compose.material.DropdownMenu
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.bfpj3.R
import com.example.bfpj3.database.FilteringOption
import com.example.bfpj3.database.FirebaseViewModel
import com.example.bfpj3.database.SortingOption
import com.example.bfpj3.ui.data.Destination
import com.example.bfpj3.ui.theme.BFPJ3Theme
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(navController: NavController, firebaseViewModel: FirebaseViewModel, db: FirebaseFirestore) {
    val viewModel:HomeViewModel = viewModel(LocalContext.current as ComponentActivity)
    val destinations by firebaseViewModel.destinations.collectAsState(listOf())
    val currentSortOption by firebaseViewModel.currentSortOption.observeAsState(SortingOption.Name)
    val currentCurrency by firebaseViewModel.userCurrency.collectAsState("")
    firebaseViewModel.getCurrentUserCurrencyFromUser(db)
    LaunchedEffect(firebaseViewModel.destinations.collectAsState()) {
        firebaseViewModel.getAllDestinations(db)
    }

    Column(modifier = Modifier.padding(bottom = 60.dp)) {
        SearchBar(firebaseViewModel, navController)
        Row(modifier = Modifier
            .padding(start = 8.dp)
            .padding(end = 8.dp)) {
            FilterTags(firebaseViewModel)
            Spacer(modifier = Modifier.weight(1f))
            SortDropdownMenu(firebaseViewModel)
            IconButton(onClick = { firebaseViewModel.toggleSortOrder(currentSortOption) }) {
                Icon(Icons.AutoMirrored.Filled.Sort, contentDescription = "Reverse Sort Order")
            }
        }
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(bottom = 8.dp)
        ) {
            items(destinations) { destination ->
                DestinationCard(destination, currentCurrency, firebaseViewModel,db,
                    onClick = {
                        viewModel.selectedDestination.value = destination
                        navController.navigate("destination_detail")})
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalLayoutApi::class)
@Composable
fun DestinationCard(destination: Destination, currentCurrency: String, firebaseViewModel: FirebaseViewModel, db: FirebaseFirestore,onClick: () -> Unit) {
    var showAddToTripDialog by remember { mutableStateOf(false) }
    val viewModel:HomeViewModel = viewModel(LocalContext.current as ComponentActivity)
    Card(modifier = Modifier
        .padding(8.dp)
        .fillMaxWidth()
        .clickable(onClick = onClick), elevation = 4.dp) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = destination.imageUrl,
                modifier = Modifier
                    .width(100.dp)
                    .aspectRatio(1f)
                    .clip(RectangleShape),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = R.drawable.ic_launcher_foreground),
                error = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = "Destination Image")
            Column(modifier = Modifier
                .padding(8.dp)
                .weight(1f)) {
                Text(text = destination.name, style = MaterialTheme.typography.headlineMedium)
                Text(text = "Rating: ${viewModel.getavgRating(destination)}")
                Text(text = "Location: ${destination.location}")
                Text(text = "Price: ${viewModel.priceExchanger(destination.price, currentCurrency)}")
                FlowRow(){
                    destination.tags.forEach { tag ->
                        Chip(onClick = {}) {
                            Text(tag)
                        }
                    }
                }
            }
            IconButton(onClick = { showAddToTripDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add to Trip")
            }
        }
    }
    if (showAddToTripDialog) {
        AddToTripDialog(
            firebaseViewModel,
            db,
            destination.destinationId,
            onDismiss = { showAddToTripDialog = false },
            onSelectTripId = { selectedTrip ->
                //TODO add to destination to trip
                //viewModel.addToTrip(destination, selectedTrip)
                showAddToTripDialog = false
            }
        )
    }
}

@Composable
fun AddToTripDialog(firebaseViewModel: FirebaseViewModel, db: FirebaseFirestore, destinationId: String, onDismiss: () -> Unit, onSelectTripId: (String) -> Unit) {
    // TODO change trip to trip from VM (Also slightly change the logic below)
    var selectedTripId by remember { mutableStateOf<String?>(null) }
    val trips = firebaseViewModel.currentUserTrips.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(firebaseViewModel.currentUserTrips.collectAsState()) {
        firebaseViewModel.getCurrentUserTrips(db)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add to Trip") },
        text = {
            Column {
                trips.value.forEach { (tripId, tripTitle) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedTripId = tripId },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            onClick = { selectedTripId = tripId},
                            selected = selectedTripId == tripId
                        )
                        Text(tripTitle)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    selectedTripId?.let { onSelectTripId(it) }
                    firebaseViewModel.addDestinationIdOnTrip(db, selectedTripId!!,destinationId, context)
                },
                enabled = selectedTripId != null
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}


@Composable
fun SearchBar(firebaseViewModel: FirebaseViewModel, navController: NavController) {
    val text by firebaseViewModel.searchText.collectAsState()
    val searchResult by firebaseViewModel.searchResultDestinations.collectAsState(listOf())
    val isDropdownVisible = text.isNotBlank() && searchResult.isNotEmpty()
    val viewModel:HomeViewModel = viewModel(LocalContext.current as ComponentActivity)
    Column {
        TextField(
            value = text,
            onValueChange = firebaseViewModel::onSearchTextChange,
            label = { Text("Search")
                firebaseViewModel.onSearchTextChange(text)},
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Search") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Search
            ))
        if(isDropdownVisible) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                items(searchResult) { destination ->
                    TextButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            viewModel.selectedDestination.value = destination
                            navController.navigate("destination_detail")
                        }
                    ) {
                        Text(
                            text = destination.name,
                            style = MaterialTheme.typography.headlineSmall,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Start
                        )
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun FilterTags(firebaseViewModel: FirebaseViewModel) {
    var expanded by remember { mutableStateOf(false) }
    val currSelection by firebaseViewModel.currentFilterOption.observeAsState(FilteringOption.None)
    Column {
        TextButton(onClick = { expanded = true }) {
            Text("Filter By: ${currSelection.displayName}")
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            FilteringOption.values().forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.displayName) },
                    onClick = {
                        expanded = false
                        //firebaseViewModel.currentFilterOption.value = option
                        firebaseViewModel.filterProducts(option)
                    }
                )
            }
        }
    }
}

@Composable
fun SortDropdownMenu(firebaseViewModel: FirebaseViewModel) {
    var expanded by remember { mutableStateOf(false) }
    val currSelection by firebaseViewModel.currentSortOption.observeAsState(SortingOption.Name)
    Column {
        TextButton(onClick = { expanded = true }) {
            Text("Sort By: ${currSelection.displayName}")
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            SortingOption.values().forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.displayName) },
                    onClick = {
                        expanded = false
                        //firebaseViewModel.currentSortOption.value = option
                        firebaseViewModel.sortDestinations(option)
                    }
                )
            }
        }
    }
}



@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun HomePreview() {
    BFPJ3Theme {
        val navController = rememberNavController()
        val firebaseViewModel: FirebaseViewModel = viewModel()
        val db = Firebase.firestore
        HomeScreen(navController, firebaseViewModel, db)
    }
}