package com.example.bfpj3.database
import android.content.ContentValues.TAG
import android.content.Context
import android.net.Uri
import android.os.Build
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.bfpj3.ui.navigation.BottomNavItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class FirebaseViewModel: ViewModel() {
    private var currentUserId: String = ""
    private var _displayName = MutableStateFlow("Current Name")
    val displayName: StateFlow<String> = _displayName
    private var _profilePicDownloadUri = MutableStateFlow("")
    val profilePicDownloadUri: StateFlow<String> = _profilePicDownloadUri
    private var _userCurrency = MutableStateFlow("")
    val userCurrency: StateFlow<String> = _userCurrency

    @RequiresApi(Build.VERSION_CODES.O)
    fun register(auth: FirebaseAuth,
                 email: String,
                 password: String,
                 displayName: String,
                 location: String,
                 context: Context,
                 navController: NavController,
                 db: FirebaseFirestore
    ) {
        if (email.isBlank() || password.isBlank() || displayName.isBlank() || location.isBlank()) {
            showMessage(context, "All fields are required")
            return
        }
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "createUserWithEmail:success")
                    showMessage(context, "Signed up successfully")
                    val userId = auth.currentUser!!.uid
                    storeUserInfo(db,userId,displayName,email,password,emptyList(),emptyList(),emptyList(),"USD")
                    storeProfileInfo(db,userId, displayName,location, getCurrentDate(), "","", emptyList())
                    navController.navigate("LoginScreen")
                } else {
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    showMessage(context, "Failed to sign up\n${task.exception?.message}")
                }
            }
    }
    private fun storeUserInfo(db: FirebaseFirestore,
                              userId: String,
                              displayName: String,
                              email: String,
                              password: String,
                              reviewList: List<String>,
                              tripList: List<String>,
                              saveList: List<String>,
                              currency: String,
    ) {
        val user = hashMapOf(
            "displayName" to displayName,
            "email" to email,
            "password" to password,
            "reviewList" to reviewList,
            "tripList" to tripList,
            "saveList" to saveList,
            "currency" to currency,
        )
        db.collection("users")
            .document("user $userId")
            .set(user)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot added with ID: $documentReference")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }
    }
    private fun storeProfileInfo(db: FirebaseFirestore,
                              userId: String,
                              displayName: String,
                              location: String,
                              joinDate: String,
                              profilePicId: String,
                              aboutYou: String,
                              uploadedPhoto: List<String>,
    ) {
        val profile = hashMapOf(
            "displayName" to displayName,
            "location" to location,
            "joinDate" to joinDate,
            "profilePicId" to profilePicId,
            "aboutYou" to aboutYou,
            "uploadedPhoto" to uploadedPhoto,
        )
        db.collection("profiles")
            .document("profile $userId")
            .set(profile)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot added with ID: $documentReference")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }
    }

    fun login(auth: FirebaseAuth, email: String, password: String, context: Context, navController: NavController) {
        if (email.isBlank() || password.isBlank()) {
            showMessage(context, "All fields are required")
            return
        }
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener{ task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "LogInWithEmail:success")
                    showMessage(context, "Logged in successfully")
                    val userId = auth.currentUser!!.uid
                    currentUserId = userId
                    Log.d("zander", "currentId from login: $currentUserId")
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
    //Display name
    fun getCurrentUserDisplayNameFromProfile(db: FirebaseFirestore) {
        val userId = getCurrentUserId()
        Log.d("zander", "currentId: $userId")
        if (userId.isNotBlank()) {
            db.collection("profiles")
                .document("profile $userId")
                .get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        _displayName.value = document.data?.get("displayName").toString()
                        Log.d(TAG, "getCurrentUserDisplayNameFromProfile: ${_displayName.value}")
                    } else {
                        Log.d(TAG, "No such document")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: getCurrentUserDisplayNameFromProfile.", exception)
                }
        } else {
            Log.d("zander", "Empty userId: getCurrentUserDisplayNameFromProfile")
        }
    }

    fun updateDisplayNameOnProfile(db: FirebaseFirestore, newDisplayName:String) {
        val userId = getCurrentUserId()
        val docRef = db.collection("profiles").document("profile $userId")

        db.runTransaction { transaction ->
            transaction.update(docRef, "displayName", newDisplayName)
        }.addOnSuccessListener {
            Log.d(TAG, "Transaction updateDisplayNameOnProfile: success!")
            getCurrentUserDisplayNameFromProfile(db)
        }.addOnFailureListener { e ->
                Log.w(TAG, "Transaction updateDisplayNameOnProfile: failure", e)
        }
    }
    //Profile Picture
    fun updateCurrentUserProfilePic(db: FirebaseFirestore, storage: FirebaseStorage, uri: Uri, context: Context){
        val userId = getCurrentUserId()
        var storageRef = storage.reference
        val fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri.toString())
        // val imageName = "${UUID.randomUUID()}.$fileExtension"
        val imageName = "$userId.$fileExtension"
        val imageRef = storageRef.child("profile_images/$imageName")

        var uploadTask = imageRef.putFile(uri)
        uploadTask.addOnSuccessListener {
            // Image successfully uploaded, now get the download URL
            imageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                // Use the value property to update the MutableStateFlow
                _profilePicDownloadUri.value = downloadUri.toString()
                updateProfilePicUriOnProfile(db,downloadUri.toString())
                Log.d(TAG, "updateProfilePic: success!")
                Log.d(TAG, "updateCurrentUserProfilePic downloadUri: $downloadUri")
                showMessage(context, "Profile image uploaded")
            }.addOnFailureListener { exception ->
                Log.e(TAG, "Failed to get download URL of updateProfilePic: $exception")
            }
        }.addOnFailureListener { exception ->
            Log.e(TAG, "updateProfilePic failed: $exception")
        }
    }
    fun updateProfilePicUriOnProfile(db: FirebaseFirestore, newProfilePicUri:String) {
        val userId = getCurrentUserId()
        val docRef = db.collection("profiles").document("profile $userId")

        db.runTransaction { transaction ->
            transaction.update(docRef, "profilePicId", newProfilePicUri)
        }.addOnSuccessListener {
            Log.d(TAG, "Transaction updateProfilePicOnProfile: success!")
            //Update new profilePicDownloadUri
            //getCurrentUserDisplayNameFromProfile(db)
        }.addOnFailureListener { e ->
            Log.w(TAG, "Transaction updateProfilePicOnProfile: failure", e)
        }
    }
    fun getCurrentUserProfilePicUriFromProfile(db: FirebaseFirestore) {
        val userId = getCurrentUserId()
        Log.d("zander", "currentId: $userId")
        if (userId.isNotBlank()) {
            db.collection("profiles")
                .document("profile $userId")
                .get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        _profilePicDownloadUri.value = document.data?.get("profilePicId").toString()
                        Log.d(TAG, "getCurrentUserProfilePicUriFromProfile: ${_profilePicDownloadUri.value}")
                    } else {
                        Log.d(TAG, "No such document: getCurrentUserProfilePicUriFromProfile")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: getCurrentUserProfilePicUriFromProfile", exception)
                }
        } else {
            Log.d("zander", "Empty userId trying getCurrentUserProfilePicUriFromProfile")
        }
    }
    //Delete Account
    fun deleteAccountAndData(db: FirebaseFirestore, storage: FirebaseStorage, context: Context, navController: NavController){
        //Delete user's info
        val userId = getCurrentUserId()
        db.collection("users")
            .document("user $userId")
            .delete()
            .addOnSuccessListener {
                Log.d(TAG, "User info deleted successfully")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error deleting user info", e)
            }
        db.collection("profiles")
            .document("profile $userId")
            .delete()
            .addOnSuccessListener {
                Log.d(TAG, "Profile info deleted successfully")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error deleting profile info", e)
            }

        deleteCurrentUserProfilePic(storage, context)

        //Delete Auth
        val user = FirebaseAuth.getInstance().currentUser
        user?.delete()
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    FirebaseAuth.getInstance().signOut()
                    showMessage(context, "Account Deleted")
                    navController.navigate("LoginScreen") {
                        popUpTo(BottomNavItem.Home.route) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                } else {
                    showMessage(context, "Account deletion failed: ${task.exception?.message}")
                }
            }
    }
    fun deleteCurrentUserProfilePic(storage: FirebaseStorage, context: Context) {
        val userId = getCurrentUserId()
        val storageRef = storage.reference.child("profile_images")

        storageRef.listAll()
            .addOnSuccessListener { listResult ->
                for (item in listResult.items) {
                    if (item.name.startsWith(userId)) {
                        // Delete the image
                        item.delete()
                            .addOnSuccessListener {
                                showMessage(context, "Profile image deleted")
                            }
                            .addOnFailureListener { exception ->
                                Log.e(TAG, "Failed to delete profile image: $exception")
                            }
                    }
                }
                Log.e(TAG, "No matching profile image found for userId: $userId")
                showMessage(context, "No matching profile image found")
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error listing files: $exception")
            }
    }
    //Setting: Change currency
    fun getCurrentUserCurrencyFromUser(db: FirebaseFirestore) {
        val userId = getCurrentUserId()
        Log.d("zander", "currentId: $userId")
        if (userId.isNotBlank()) {
            db.collection("users")
                .document("user $userId")
                .get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        _userCurrency.value = document.data?.get("currency").toString()
                        Log.d(TAG, "getCurrentUserCurrencyFromUser: ${_userCurrency.value}")
                    } else {
                        Log.d(TAG, "No such document")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: getCurrentUserCurrencyFromUser", exception)
                }
        } else {
            Log.d("zander", "Empty userId: getCurrentUserCurrencyFromUser")
        }
    }
    fun updateCurrentUserCurrencyOnUser(db: FirebaseFirestore, newCurrency:String, context: Context) {
        val userId = getCurrentUserId()
        val docRef = db.collection("users").document("user $userId")

        db.runTransaction { transaction ->
            transaction.update(docRef, "currency", newCurrency)
        }.addOnSuccessListener {
            Log.d(TAG, "Transaction updateCurrentUserCurrencyOnUser: success!")
            getCurrentUserCurrencyFromUser(db)
            showMessage(context, "New Currency Stored")
        }.addOnFailureListener { e ->
            Log.w(TAG, "Transaction updateCurrentUserCurrencyOnUser: failure", e)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun storeFeedbackInfo(db: FirebaseFirestore,
                                  rating: Int,
                                  description: String,
                                  context: Context
    ) {
        if (rating == 0) {
            showMessage(context, "Please give us a rating!")
            return
        }
        else if (description.isBlank()) {
            showMessage(context, "Please give us a feedback!")
            return
        }
        val userId = getCurrentUserId()
        val feedback = hashMapOf(
            "rating" to rating,
            "description" to description,
            "timestamp" to getCurrentDate()
        )
        db.collection("feedbacks")
            .document("feedback $userId")
            .set(feedback)
            .addOnSuccessListener { documentReference ->
                showMessage(context, "Thank you for your feedback!")
                Log.d(TAG, "DocumentSnapshot added with ID: $documentReference")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error storeFeedbackInfo", e)
            }
    }
    fun showMessage(context: Context, message:String){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getCurrentDate(): String {
        // Get the current date
        val currentDate = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        return currentDate.format(formatter)
    }

    fun getCurrentUserId(): String {
        return currentUserId
    }

}