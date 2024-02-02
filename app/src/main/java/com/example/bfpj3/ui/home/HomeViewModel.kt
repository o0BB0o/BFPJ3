package com.example.bfpj3.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.bfpj3.ui.data.Destination


class HomeViewModel : ViewModel() {
    var selectedDestination = MutableLiveData<Destination>()

    fun getavgRating(d:Destination): String {
        if (d.reviewList.isEmpty()) {
            return "N/A"
        }
        val totalRating = d.reviewList.sumOf { it.rating }
        val averageRating = totalRating.toDouble() / d.reviewList.size
        return String.format("%.2f", averageRating)
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