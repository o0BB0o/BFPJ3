package com.example.bfpj3.database

enum class SortingOption(val displayName: String) {
    Name("Name"),
    Price("Price"),
    Ratings("Ratings");
}

enum class FilteringOption(val displayName: String) {
    None("None"),
    Nature("Nature"),
    Historic("Historic"),
    Culture("Culture"),
    Music("Music"),
    Tech("Tech")
}