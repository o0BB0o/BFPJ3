package com.example.bfpj3.ui.trip

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


@RequiresApi(Build.VERSION_CODES.O)
class TripViewModel : ViewModel() {
    var selectedTrip = MutableLiveData<Trip>()
}