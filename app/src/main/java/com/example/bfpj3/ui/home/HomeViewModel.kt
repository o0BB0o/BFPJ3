package com.example.bfpj3.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.bfpj3.ui.data.Destination

class HomeViewModel : ViewModel() {
    val destinations = MutableLiveData<List<Destination>>()
    var selectedDestination = MutableLiveData<Destination>()
    init {
        //TODO DELETE THESE!! TEMP CARD ITEMS
        val d1 = Destination("TEMP1",
            "https://marvel-b1-cdn.bc0a.com/f00000000270502/s19538.pcdn.co/wp-content/uploads/2021/07/road-trip.jpg",
            5.0, "Space", listOf("Nature", "Historic"))
        val d2 = Destination("TEMP2",
            "https://marvel-b1-cdn.bc0a.com/f00000000270502/s19538.pcdn.co/wp-content/uploads/2021/07/road-trip.jpg",
            4.0, "Space", listOf("Historic"))
        var temp = listOf(d1, d2)
        destinations.value = temp
    }
    enum class SortingOption(val displayName: String) {
        Name("Name"),
        Price("Price"),
        Ratings("Ratings")
    }

    enum class FilteringOption(val displayName: String) {
        None("None"),
        Nature("Nature"),
        Historic("Historic"),
        Culture("Culture"),
        Music("Music")
    }

    fun sortDestinations(sortOption: SortingOption) { //TODO
        when (sortOption) {
            SortingOption.Name -> { }
            SortingOption.Price -> { }
            SortingOption.Ratings -> {}
        }
    }

    fun filterProducts(filterOption: FilteringOption) { //TODO
        when (filterOption) {
            FilteringOption.None -> { }
            FilteringOption.Nature -> { }
            FilteringOption.Historic -> {}
            FilteringOption.Culture -> {}
            FilteringOption.Music -> {}
        }
    }
}