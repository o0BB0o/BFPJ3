package com.example.bfpj3.ui.navigation

import android.annotation.SuppressLint
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.bfpj3.ui.home.DestinationDetail
import com.example.bfpj3.ui.home.HomeScreen
import com.example.bfpj3.ui.login.LoginScreen
import com.example.bfpj3.ui.profile.ProfileScreen
import com.example.bfpj3.ui.register.RegisterScreen
import com.example.bfpj3.ui.settting.SettingScreen
import com.example.bfpj3.ui.trip.TripScreen
import com.google.firebase.auth.FirebaseAuth

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun NavigationGraph(auth: FirebaseAuth) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Trip,
        BottomNavItem.Profile,
        BottomNavItem.Settings
    )
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
        bottomBar = { BottomNavigationBar(navController, bottomBarState, items) }
    ) {
        NavHost(navController, startDestination = "LoginScreen") {
            composable(BottomNavItem.Home.route) { HomeScreen(navController) }
            composable(BottomNavItem.Trip.route) { TripScreen() }
            composable(BottomNavItem.Profile.route) { ProfileScreen() }
            composable(BottomNavItem.Settings.route) { SettingScreen(navController, auth) }
            composable("destination_detail"){ DestinationDetail() }
            composable("LoginScreen") { LoginScreen(navController = navController, auth) }
            composable("RegisterScreen") { RegisterScreen(navController = navController, auth) }
        }
    }
}