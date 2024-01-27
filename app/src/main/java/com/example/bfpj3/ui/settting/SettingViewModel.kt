package com.example.bfpj3.ui.settting

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class SettingViewModel : ViewModel() {
    var selectedCurrency = mutableStateOf("USD")
        private set
    var feedbackText = mutableStateOf("")
        private set
    var rating = mutableStateOf(0)
        private set
}
