package com.example.bfpj3.ui.navigation

import ReviewHistoryScreen
import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
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
import com.example.bfpj3.database.FirebaseViewModel
import com.example.bfpj3.ui.home.DestinationDetail
import com.example.bfpj3.ui.home.HomeScreen
import com.example.bfpj3.ui.login.LoginScreen
import com.example.bfpj3.ui.profile.ProfileScreen
import com.example.bfpj3.ui.register.RegisterScreen
import com.example.bfpj3.ui.settting.SettingScreen
import com.example.bfpj3.ui.trip.AddNewTripScreen
import com.example.bfpj3.ui.trip.TripScreen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun NavigationGraph(auth: FirebaseAuth, db: FirebaseFirestore, storage: FirebaseStorage, firebaseViewModel: FirebaseViewModel) {
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
        bottomBar = { BottomNavigationBar(navController, bottomBarState) }
    ) {
        NavHost(navController, startDestination = "LoginScreen") {

            composable(BottomNavItem.Home.route) { HomeScreen(navController, firebaseViewModel, db) }
            composable(BottomNavItem.Trip.route) { TripScreen(navController, db, firebaseViewModel) }
            composable(BottomNavItem.Profile.route) { ProfileScreen(navController, db, storage, firebaseViewModel) }
            composable(BottomNavItem.Settings.route) { SettingScreen(db,firebaseViewModel, navController, auth) }
            composable("destination_detail"){ DestinationDetail(db,firebaseViewModel) }
            composable("LoginScreen") { LoginScreen(navController = navController, auth, firebaseViewModel) }
            composable("RegisterScreen") { RegisterScreen(navController = navController, auth, db,firebaseViewModel) }
            composable("reviewHistory") { ReviewHistoryScreen(navController,db,firebaseViewModel)}
            composable("addNewTripScreen") { AddNewTripScreen(navController, db, firebaseViewModel) }
        }
    }
}