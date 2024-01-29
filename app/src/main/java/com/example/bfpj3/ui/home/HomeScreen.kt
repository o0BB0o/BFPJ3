package com.example.bfpj3.ui.home


import androidx.activity.ComponentActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Card
import androidx.compose.material.Chip
import androidx.compose.material.DropdownMenu
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.example.bfpj3.ui.data.Destination
import com.example.bfpj3.ui.theme.BFPJ3Theme

@Composable
fun HomeScreen(navController: NavController) {
    val viewModel:HomeViewModel = viewModel(LocalContext.current as ComponentActivity)
    val destinations by viewModel.destinations.observeAsState(emptyList())
    Column {
        SearchBar()
        Row(modifier = Modifier
            .padding(start = 8.dp)
            .padding(end = 8.dp)) {
            FilterTags(viewModel)
            Spacer(modifier = Modifier.weight(1f))
            SortDropdownMenu(viewModel)
        }
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(bottom = 8.dp)
        ) {
            items(destinations) { destination ->
                DestinationCard(destination, viewModel,
                    onClick = {
                        viewModel.selectedDestination.value = destination
                        navController.navigate("destination_detail")})
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DestinationCard(destination: Destination, viewModel: HomeViewModel, onClick: () -> Unit) {
    Card(modifier = Modifier
        .padding(8.dp)
        .fillMaxWidth()
        .clickable(onClick = onClick), elevation = 4.dp) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = rememberAsyncImagePainter(model = destination.imageUrl),
                contentDescription = "Destination Image",
                modifier = Modifier.size(100.dp)
            )
            Column(modifier = Modifier.padding(8.dp)) {
                Text(text = destination.name, style = MaterialTheme.typography.headlineMedium)
                Text(text = "Rating: ${destination.rating}")
                Text(text = "Location: ${destination.location}")
                Row(){
                    destination.tags.forEach { tag ->
                        Chip(onClick = {}) {
                            Text(tag)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SearchBar(/*onSearch: (String) -> Unit*/) {
    var text by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    TextField(
        value = text,
        onValueChange = { text = it },
        label = { Text("Search") },
        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Search") },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Search
        ),
        keyboardActions = KeyboardActions(
            onSearch = {
                //onSearch(text) TODO
                focusManager.clearFocus()
            }
        )
    )
}

@Composable
fun FilterTags(viewModel: HomeViewModel) {
    var expanded by remember { mutableStateOf(false) }
    var currSelection by remember { mutableStateOf(HomeViewModel.FilteringOption.None) }
    Column {
        TextButton(onClick = { expanded = true }) {
            Text("Filter By: ${currSelection.displayName}")
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            HomeViewModel.FilteringOption.values().forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.displayName) },
                    onClick = {
                        expanded = false
                        currSelection = option
                        viewModel.filterProducts(option)
                    }
                )
            }
        }
    }
}

@Composable
fun SortDropdownMenu(viewModel: HomeViewModel) {
    var expanded by remember { mutableStateOf(false) }
    var currSelection by remember { mutableStateOf(HomeViewModel.SortingOption.Name) }
    Column {
        TextButton(onClick = { expanded = true }) {
            Text("Sort By: ${currSelection.displayName}")
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            HomeViewModel.SortingOption.values().forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.displayName) },
                    onClick = {
                        expanded = false
                        currSelection = option
                        viewModel.sortDestinations(option)
                    }
                )
            }
        }
    }
}



@Preview(showBackground = true)
@Composable
fun HomePreview() {
    BFPJ3Theme {
        val navController = rememberNavController()
        HomeScreen(navController)
    }
}