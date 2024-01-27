package com.example.bfpj3.database
import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth

class FirebaseViewModel: ViewModel() {
    fun register(auth: FirebaseAuth, email: String, password: String, context: Context, navController: NavController) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "createUserWithEmail:success")
                    showMessage(context, "Signed up successfully")
                    val user = auth.currentUser
                    val userId = user?.uid
                    navController.navigate("LoginScreen")
                } else {
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    showMessage(context, "Failed to sign up\n${task.exception?.message}")
                }
            }
    }
    fun login(auth: FirebaseAuth, email: String, password: String, context: Context, navController: NavController) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener{ task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "LogInWithEmail:success")
                    showMessage(context, "Logged in successfully")
                    val user = auth.currentUser
                    navController.navigate("home") {
                        popUpTo(navController.graph.startDestinationId) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    showMessage(context, "Failed to log in\n${task.exception?.message}")
                }
            }
    }

    fun showMessage(context: Context, message:String){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}