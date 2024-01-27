package com.example.bfpj3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.bfpj3.ui.home.HomeScreen
import com.example.bfpj3.ui.login.LoginScreen
import com.example.bfpj3.ui.navigation.NavigationGraph
import com.example.bfpj3.ui.register.RegisterScreen
import com.example.bfpj3.ui.theme.BFPJ3Theme
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class MainActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        auth = Firebase.auth
        setContent {
            BFPJ3Theme {
                // A surface container using the 'background' color from the theme
//                val navController = rememberNavController()
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavigationGraph(auth)
                }
//                NavHost(navController = navController, startDestination = "LoginScreen"){
//                    composable("LoginScreen") {
//                        LoginScreen(navController = navController, auth)
//                    }
//                    composable("RegisterScreen") {
//                        RegisterScreen(navController = navController, auth)
//                    }
//                    composable("MainScreen") {
//                        MainScreen()
//                    }
//                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    BFPJ3Theme {
        Greeting("Android")
    }
}