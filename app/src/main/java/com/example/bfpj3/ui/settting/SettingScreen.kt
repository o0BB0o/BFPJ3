package com.example.bfpj3.ui.settting


import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconToggleButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.bfpj3.database.FirebaseViewModel
import com.example.bfpj3.ui.navigation.BottomNavItem
import com.example.bfpj3.ui.theme.BFPJ3Theme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(db: FirebaseFirestore, firebaseViewModel: FirebaseViewModel, navController: NavController, auth: FirebaseAuth) {
    val viewModel: SettingViewModel = viewModel()
    // var selectedCurrency by viewModel.selectedCurrency
    var feedbackText by remember {mutableStateOf("")}
    var rating by remember {mutableStateOf(0)}
    val context = LocalContext.current

    val userCurrency by firebaseViewModel.userCurrency.collectAsState()

    LaunchedEffect(firebaseViewModel.userCurrency.collectAsState()) {
        firebaseViewModel.getCurrentUserCurrencyFromUser(db)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .padding(bottom = 60.dp)
    ) {
        Text(
            text = "Change Currency",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(top = 32.dp)
        )
        CurrencyDropdownMenu(userCurrency,
            onCurrencyChange = { selectedCurrency ->
                firebaseViewModel.updateCurrentUserCurrencyOnUser(db, selectedCurrency, context)
            }
        )

        Text(
            text = "Feedback",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(top = 16.dp)
        )

        RatingBar(rating) { rating = it }
        TextField(
            value = feedbackText,
            onValueChange = { feedbackText = it },
            label = { Text("Write a feedback for us!") },
            modifier = Modifier.fillMaxWidth().height(120.dp)
        )
        Button(
            onClick = {
                firebaseViewModel.storeFeedbackInfo(db,rating,feedbackText,context)
            },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 16.dp),
        ) {
            Text("Submit Feedback")
        }

        Spacer(Modifier.weight(1f))
        Button(
            onClick = {
                auth.signOut()
                navController.navigate("LoginScreen") {
                    popUpTo(BottomNavItem.Home.route) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Logout")
        }
    }
}


@Composable
fun CurrencyDropdownMenu(userCurrency: String, onCurrencyChange: (String) -> Unit) {
    // TODO
    var expanded by remember { mutableStateOf(false) }
    val currencies = listOf("USD", "EUR", "CNY")

    Column {
        TextButton(
            onClick = { expanded = true }
        ) {
            Text(text = "Selected Currency: $userCurrency")
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            currencies.forEach { currency ->
                DropdownMenuItem(onClick = {
                    onCurrencyChange(currency)
                    expanded = false
                }) {
                    Text(text = currency)
                }
            }
        }
    }
}


@Composable
fun RatingBar(currentRating: Int, onRatingChange: (Int) -> Unit) {
    Row {
        (1..5).forEach { index ->
            IconToggleButton(
                checked = currentRating >= index,
                onCheckedChange = { onRatingChange(index) }
            ) {
                val icon =
                    if (currentRating >= index) Icons.Filled.Star else Icons.Filled.StarBorder
                Icon(icon, contentDescription = "Rating $index")
            }
        }
    }
}