package com.example.bfpj3

import android.annotation.SuppressLint
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.bfpj3.ui.home.DestinationDetail
import com.example.bfpj3.ui.home.HomeScreen
import com.example.bfpj3.ui.navigation.BottomNavItem
import com.example.bfpj3.ui.navigation.BottomNavigationBar
import com.example.bfpj3.ui.profile.ProfileScreen
import com.example.bfpj3.ui.settting.SettingScreen
import com.example.bfpj3.ui.trip.TripScreen

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun MainScreen() {
    val bottomBarState = rememberSaveable { (mutableStateOf(true)) }
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    when (navBackStackEntry?.destination?.route) {
        BottomNavItem.Home.route -> {
            bottomBarState.value = true
        }
        BottomNavItem.Trip.route -> {
            bottomBarState.value = true
        }
        BottomNavItem.Profile.route -> {
            bottomBarState.value = true
        }
        BottomNavItem.Settings.route -> {
            bottomBarState.value = true
        }
        else -> {bottomBarState.value = false}
    }
    Scaffold(
        bottomBar = { BottomNavigationBar(navController = navController, bottomBarState = bottomBarState) }
    ) {
        NavHost(navController, startDestination = BottomNavItem.Home.route) {
            composable(BottomNavItem.Home.route) { HomeScreen(navController) }
            composable(BottomNavItem.Trip.route) { TripScreen() }
            composable(BottomNavItem.Profile.route) { ProfileScreen() }
            composable(BottomNavItem.Settings.route) { SettingScreen() }
            composable("destination_detail"){ DestinationDetail() }
        }
    }
}

