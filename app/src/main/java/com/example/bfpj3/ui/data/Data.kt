package com.example.bfpj3.ui.data

import com.example.bfpj3.ui.home.HomeViewModel

data class Destination( // TODO Not FINAL DESTINATION
    val name: String,
    val imageUrl: String,
    val rating: Double,
    val location: String,
    val tags: List<String>
)