package com.example.bfpj3.ui.home

import android.widget.Toast
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController

@Composable
fun HomeScreen(navController: NavController) {
    Row() {
        Text(text= "Home Screen is here")
        Button(onClick = {navController.navigate("destination_detail")}) {
            Text("TEST NAVIGATE")
        }
    }
}