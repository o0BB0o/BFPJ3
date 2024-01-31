package com.example.bfpj3.ui.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.bfpj3.ui.data.Destination
import com.example.bfpj3.ui.data.Price
import com.example.bfpj3.ui.data.Review
import com.google.firebase.Timestamp
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class HomeViewModel : ViewModel() {
    private val destinations = MutableLiveData<List<Destination>>()
    var selectedDestination = MutableLiveData<Destination>()
    private val allDestinations = mutableListOf<Destination>()
    private var isReversed = MutableLiveData<Boolean>(false)
    var currentSortOption = MutableLiveData<SortingOption>(SortingOption.Name)
    private var currentFilterOption: FilteringOption = FilteringOption.None
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
        Music("Music")
    }

    fun sortDestinations(sortOption: SortingOption) { //TODO
        currentSortOption.value = sortOption
        applyCurrentFiltersAndSort()
    }

    fun filterProducts(filterOption: FilteringOption) { //TODO
        currentFilterOption = filterOption
        applyCurrentFiltersAndSort()
    }
    private fun applyCurrentFiltersAndSort() {
        val filteredList = when (currentFilterOption) {
            FilteringOption.None -> allDestinations
            else -> allDestinations.filter { currentFilterOption.displayName in it.tags }
        }

        val sortedList = when (currentSortOption.value ?: SortingOption.Name) {
            SortingOption.Name -> filteredList.sortedBy { it.name }
            SortingOption.Price -> filteredList.sortedBy { it.price }
            SortingOption.Ratings -> filteredList.sortedByDescending { getavgRating(it) }
        }

        destinations.value = if (isReversed.value == true) sortedList.reversed() else sortedList
    }

    fun getavgRating(d:Destination): Double {
        if (d.reviewList.isEmpty()) {
            return 0.0
        }
        val totalRating = d.reviewList.sumOf { it.rating }
        val averageRating = totalRating.toDouble() / d.reviewList.size
        return String.format("%.2f", averageRating).toDouble()
    }

    fun hasUserReviewed(destination: Destination): Boolean { // TODO
        return false
    }
    fun toggleSortOrder(currentSortOption: SortingOption? = null) {
        isReversed.value = !(isReversed.value ?: false)
        applyCurrentFiltersAndSort()
    }

    fun priceExchanger(usdPrice: Double, currCurrency: String): String {
        val exchangeRateToEUR = 0.92
        val exchangeRateToCNY = 7.1
        val shownPrice = when (currCurrency) {
            "EUR" -> "€%.2f".format(usdPrice * exchangeRateToEUR)
            "CNY" -> "￥%.2f".format(usdPrice * exchangeRateToCNY)
            else -> "$%.2f".format(usdPrice)
        }
        return shownPrice
    }
}