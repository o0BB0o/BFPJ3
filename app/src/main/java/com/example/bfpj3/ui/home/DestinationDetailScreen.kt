package com.example.bfpj3.ui.home

import androidx.activity.ComponentActivity
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun DestinationDetail() {
    val viewModel:HomeViewModel = viewModel(LocalContext.current as ComponentActivity)
    val selectedDestination by viewModel.selectedDestination.observeAsState()
    selectedDestination?.let { Text(text = it.name) }
}