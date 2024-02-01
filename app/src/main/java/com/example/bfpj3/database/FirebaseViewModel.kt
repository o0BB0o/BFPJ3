package com.example.bfpj3.database
import android.content.ContentValues.TAG
import android.content.Context
import android.net.Uri
import android.os.Build
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.bfpj3.ui.data.Destination
import com.example.bfpj3.ui.data.Review
import com.example.bfpj3.ui.navigation.BottomNavItem
import com.example.bfpj3.ui.trip.Trip
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
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

    private var _destinations = MutableStateFlow<MutableList<Destination>>(mutableListOf())
    val destinations: StateFlow<List<Destination>> = _destinations

    //SearchBar
    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()
    fun onSearchTextChange(text: String) {
        _searchText.value = text
    }
    val searchResultDestinations = searchText
        .combine(_destinations) { text, destinations ->
            if (text.isBlank()) {
                listOf()
            }
            else {
                destinations.filter {
                    it.doesMatchSearchQuery(text)
                }
            }
        }


    private var _trips = MutableStateFlow<MutableList<Trip>>(mutableListOf())
    val trips: StateFlow<List<Trip>> = _trips

    private var _currentUserTrips = MutableStateFlow<MutableList<Trip>>(mutableListOf())
    val currentUserTrips: StateFlow<List<Trip>> = _currentUserTrips

    private var _currentUserReviews = MutableStateFlow<MutableList<Review>>(mutableListOf())
    val currentUserReviews: StateFlow<List<Review>> = _currentUserReviews

    // Sorting + Filtering
    var currentSortOption = MutableLiveData<SortingOption>(SortingOption.Name)
    var currentFilterOption: FilteringOption = FilteringOption.None
    lateinit var allDestinations : List<Destination>
    private var isReversed = MutableLiveData<Boolean>(false)

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
            "reviewIdList" to reviewList,
            "tripIdList" to tripList,
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

    fun updateDisplayNameOnProfileAndUser(db: FirebaseFirestore, newDisplayName:String) {
        val userId = getCurrentUserId()
        val profileDocRef = db.collection("profiles").document("profile $userId")
        val userDocRef = db.collection("users").document("user $userId")

        db.runTransaction { transaction ->
            transaction.update(profileDocRef, "displayName", newDisplayName)
        }.addOnSuccessListener {
            Log.d(TAG, "Transaction updateDisplayNameOnProfileAndUser: success!")
            getCurrentUserDisplayNameFromProfile(db)
        }.addOnFailureListener { e ->
                Log.w(TAG, "Transaction updateDisplayNameOnProfileAndUser: failure", e)
        }

        db.runTransaction { transaction ->
            transaction.update(userDocRef, "displayName", newDisplayName)
        }.addOnSuccessListener {
            Log.d(TAG, "Transaction updateDisplayNameOnProfileAndUser: success!")
            getCurrentUserDisplayNameFromProfile(db)
        }.addOnFailureListener { e ->
            Log.w(TAG, "Transaction updateDisplayNameOnProfileAndUser: failure", e)
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

    fun getAllDestinations(db: FirebaseFirestore) {
        db.collection("destinations")
            .get()
            .addOnSuccessListener { result ->
                val newDestinations = mutableListOf<Destination>()
                //for each destination
                for (document in result) {
                    // Extract values from the document
                    val destinationId = document.getString("destinationId") ?: ""
                    val name = document.getString("name") ?: ""
                    val ownerOrganization = document.getString("ownerOrganization") ?: ""
                    val location = document.getString("location") ?: ""
                    val description = document.getString("description") ?: ""
                    val reviewIdList = document.get("reviewIdList") as? List<String> ?: emptyList()
                    val price = document.getDouble("price") ?: 0.0
                    val localLanguageList = document.get("localLanguageList") as? List<String> ?: emptyList()
                    val ageRecommendation = document.getString("ageRecommendation") ?: ""
                    val thingsTodoList = document.get("thingsTodoList") as? List<String> ?: emptyList()
                    val tagList = document.get("tagList") as? List<String> ?: emptyList()
                    val imageUrl = document.getString("imageUrl") ?: ""

                    val reviewList = mutableListOf<Review>()
                    //if a destination has no review
                    if(reviewIdList.isEmpty()){
                        val destination = Destination(
                            destinationId,
                            name,
                            ownerOrganization,
                            location,
                            description,
                            reviewList,
                            price,
                            localLanguageList,
                            ageRecommendation,
                            thingsTodoList,
                            tagList,
                            imageUrl
                        )
                        newDestinations.add(destination)
                    }

                    for (reviewId in reviewIdList) {
                        getReviewInfoFromReview(db, reviewId) { review ->
                            reviewList.add(review)
                            Log.d(TAG, "current reviewlist size: ${reviewList.size}")
                            // Check if all reviews for this destination have been retrieved
                            if (reviewList.size == reviewIdList.size) {
                                val destination = Destination(
                                    destinationId,
                                    name,
                                    ownerOrganization,
                                    location,
                                    description,
                                    reviewList,
                                    price,
                                    localLanguageList,
                                    ageRecommendation,
                                    thingsTodoList,
                                    tagList,
                                    imageUrl
                                )
                                newDestinations.add(destination)
                            }
                        }
                    }
                }

                _destinations.value = newDestinations
                allDestinations = newDestinations
                applyCurrentFiltersAndSort()
                Log.d(TAG, "success: getAllDestinations")
                Log.d(TAG, "Destinations $newDestinations")
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "Failed: getAllDestinations: $exception")
            }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun storeReviewInfoOnReview(db: FirebaseFirestore,
                                currentDestinationId: String,
                                rating: Int,
                                title: String,
                                description: String,
                                context: Context
    ) {
        if(rating==0 || title.isBlank() || description.isBlank()){
            showMessage(context, "All fields are required to leave a review")
            return
        }
        //refresh _currentUserReviews
        getCurrentUserReviewListFromUser(db, context){
            //waiting for callback to proceed
            val hasDestinationId = _currentUserReviews.value.any { review ->
                review.destinationId == currentDestinationId
            }
            //if user already post review for this destination
            if (hasDestinationId) {
                showMessage(context,"You can only post one review")
            }else{
                val userId = getCurrentUserId()
                val review = hashMapOf(
                    "reviewId" to "",
                    "userId" to userId,
                    "destinationId" to currentDestinationId,
                    "rating" to rating,
                    "title" to title,
                    "description" to description,
                    "timestamp" to getCurrentDate(),
                )
                db.collection("reviews")
                    .add(review)
                    .addOnSuccessListener { documentReference ->
                        val generatedId = documentReference.id
                        review["reviewId"] = generatedId

                        db.collection("reviews")
                            .document(generatedId)  // Use the generatedId as the document ID
                            .set(review)
                            .addOnSuccessListener {
                                //showMessage(context,"Review Added")
                                Log.d(TAG, "success: storeReviewInfoOnReview with id: $generatedId")
                                addReviewIdOnDestination(db, currentDestinationId, generatedId, context)
                                addReviewIdOnUser(db, generatedId)
                            }
                            .addOnFailureListener { e ->
                                Log.d(TAG, "failed: storeReviewInfoOnReview: $e")
                            }
                    }
            }
        }
    }

    fun getReviewInfoFromReview(db: FirebaseFirestore, reviewId: String, callback: (Review) -> Unit){
        val review = Review("","","", 0, "","","")
        Log.d(TAG, "empty review initialized here")
        db.collection("reviews")
            .document(reviewId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists() && document != null ) {
                    review.reviewId = document.data?.get("reviewId").toString()
                    review.userId = document.data?.get("userId").toString()
                    review.destinationId = document.data?.get("destinationId").toString()
                    review.rating = (document.data?.get("rating") as Number).toInt()
                    review.title = document.data?.get("title").toString()
                    review.description = document.data?.get("description").toString()
                    review.timestamp = document.data?.get("timestamp").toString()
                    Log.d(TAG, "getReviewInfoFromReview: Success")
                } else {
                    Log.d(TAG, "No such document")
                }
                callback(review)
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: getReviewInfoFromReview", exception)
                callback(review)
            }
    }
    fun updateReviewOnReview(db: FirebaseFirestore, reviewId: String, editedDescription: String, editedRating: Int, context: Context){
        val docRef = db.collection("reviews").document(reviewId)

        db.runTransaction { transaction ->
            transaction.update(docRef, "description", editedDescription)
            transaction.update(docRef, "rating", editedRating)
        }
        .addOnSuccessListener {
            getCurrentUserReviewListFromUser(db, context){}
            Log.d(TAG, "Transaction success: updateReviewOnReview $reviewId")
            showMessage(context,"Review Updated")
        }
        .addOnFailureListener { e ->
            Log.w(TAG, "Transaction failure: updateReviewOnReview $reviewId\": $e")
        }
    }

    fun addReviewIdOnDestination(db: FirebaseFirestore,
                                 destinationId: String,
                                 reviewId: String,
                                 context: Context
    ) {
        val docRef = db.collection("destinations").document(destinationId)

        db.runTransaction { transaction ->
            val currentList = transaction.get(docRef).get("reviewIdList")
                              as? MutableList<String> ?: mutableListOf()
            currentList.add(reviewId)
            transaction.update(docRef, "reviewIdList", currentList)
        }
        .addOnSuccessListener {
            Log.d(TAG, "Transaction success: ReviewId added to destination $reviewId")
            showMessage(context,"Review Added")
        }
        .addOnFailureListener { e ->
            Log.w(TAG, "Transaction failure: ReviewId added to destination $reviewId\": $e")
        }
    }

    fun addReviewIdOnUser(db: FirebaseFirestore, reviewId: String){
        val userId = getCurrentUserId()
        val docRef = db.collection("users").document("user $userId")

        db.runTransaction { transaction ->
            val currentList = transaction.get(docRef).get("reviewIdList")
                    as? MutableList<String> ?: mutableListOf()
            currentList.add(reviewId)
            transaction.update(docRef, "reviewIdList", currentList)
        }
            .addOnSuccessListener {
                Log.d(TAG, "Transaction success: addReviewIdOnUser")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Transaction failure: addReviewIdOnUser: $e")
            }
    }

    fun getCurrentUserReviewListFromUser(db: FirebaseFirestore, context: Context, callback: (String) -> Unit){
        val userId = getCurrentUserId()
        db.collection("users")
            .document("user $userId")
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val reviewIdList = document.get("reviewIdList") as? MutableList<String> ?: mutableListOf()
                    //No review found
                    if(reviewIdList.size==0){
                        callback("proceed")
                    }
                    val reviewList = mutableListOf<Review>()
                    for (reviewId in reviewIdList) {
                        getReviewInfoFromReview(db, reviewId) { review ->
                            reviewList.add(review)
                            // Check if all titles have been retrieved
                            if (reviewList.size == reviewIdList.size) {
                                _currentUserReviews.value = reviewList
                                callback("proceed")
                                Log.d(TAG, "Success: getCurrentUserReviewIdListFromUser")
                            }
                        }
                    }
                } else {
                    Log.d(TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: getCurrentUserReviewIdListFromUser", exception)
            }
    }
    fun getDestinationNameByDestinationId(db: FirebaseFirestore, destinationId: String, callback: (String) -> Unit){
        val userId = getCurrentUserId()
        if (userId.isNotBlank()) {
            db.collection("destinations")
                .document(destinationId)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val name = document.data?.get("name").toString()
                        callback(name)
                        Log.d(TAG, "getDestinationNameByDestinationId: $name")
                    } else {
                        Log.d(TAG, "No such document")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: getDestinationNameByDestinationId", exception)
                }
        } else {
            Log.d("zander", "Empty userId: getDestinationNameByDestinationId")
        }
    }

    fun getUserDisplayNameByUserId(db: FirebaseFirestore, userId: String, callback: (String) -> Unit) {
        if (userId.isNotBlank()) {
            db.collection("profiles")
                .document("profile $userId")
                .get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val displayName = document.data?.get("displayName").toString()
                        callback(displayName)
                        Log.d(TAG, "getUserDisplayNameByUserId: $displayName")
                    } else {
                        Log.d(TAG, "No such document")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: getUserDisplayNameByUserId", exception)
                }
        } else {
            Log.d("zander", "Empty userId: getUserDisplayNameByUserId")
        }
    }
    fun getUserProfileImageUriByUserId(db: FirebaseFirestore, userId: String, callback: (String) -> Unit) {
        Log.d("zander", "currentId: $userId")
        if (userId.isNotBlank()) {
            db.collection("profiles")
                .document("profile $userId")
                .get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val profileImageUri = document.data?.get("profilePicId").toString()
                        callback(profileImageUri)
                        Log.d(TAG, "getUserProfileImageUriByUserId: $profileImageUri")
                    } else {
                        Log.d(TAG, "No such document")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: getUserProfileImageUriByUserId", exception)
                }
        } else {
            Log.d("zander", "Empty userId: getUserProfileImageUriByUserId")
        }
    }
    //Trip
    fun storeTripInfoOnTrip(db: FirebaseFirestore,
                                newTrip: Trip,
                                context: Context
    ) {
        if(newTrip.numOfPeople==0 || newTrip.title.isBlank()){
            showMessage(context, "All fields are required")
            return
        }
        val userId = getCurrentUserId()
        val trip = hashMapOf(
            "tripId" to "",
            "userId" to userId,
            "destinationIdList" to newTrip.destinations,
            "numOfPeople" to newTrip.numOfPeople,
            "startDate" to newTrip.startDate.toString(),
            "endDate" to newTrip.startDate.toString(),
            "title" to newTrip.title,
            "description" to newTrip.description,
            "isPublic" to newTrip.isPublic,
        )
        db.collection("trips")
            .add(trip)
            .addOnSuccessListener { documentReference ->
                val generatedId = documentReference.id

                // Update the tripId field in the existing trip HashMap
                trip["tripId"] = generatedId

                // Add the trip to Firestore with the updated tripId
                db.collection("trips")
                    .document(generatedId)  // Use the generatedId as the document ID
                    .set(trip)
                    .addOnSuccessListener {
                        // Handle success if needed
                        Log.d(TAG, "success: storeTripInfoOnTrip with id: $generatedId")
                        addTripIdOnUser(db, generatedId, context)
                    }
                    .addOnFailureListener { e ->
                        Log.d(TAG, "failed: storeTripInfoOnTrip with id: $e")
                    }
            }
    }
    fun addTripIdOnUser(db: FirebaseFirestore,
                                 generatedId: String,
                                 context: Context
    ) {
        val userId = getCurrentUserId()
        val docRef = db.collection("users").document("user $userId")

        db.runTransaction { transaction ->
            val currentList = transaction.get(docRef).get("tripIdList")
                    as? MutableList<String> ?: mutableListOf()
            currentList.add(generatedId)
            transaction.update(docRef, "tripIdList", currentList)
        }
            .addOnSuccessListener {
                Log.d(TAG, "Transaction success: TripId added to tripList on user")
                showMessage(context,"Trip Added")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Transaction failure: TripId added to tripList on user: $e")
            }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun getAllTrip(db: FirebaseFirestore) {
        db.collection("trips")
            .get()
            .addOnSuccessListener { result ->
                val currentUserId = getCurrentUserId()
                val newTrips = mutableListOf<Trip>()
                for (document in result) {
                    val tripId = document.getString("tripId") ?: ""
                    val userId = document.getString("userId") ?: ""
                    val destinationIdList = document.get("destinationIdList") as? MutableList<String> ?: mutableListOf()
                    val numOfPeople = (document.get("numOfPeople") as Number).toInt()
                    val startDate = document.getString("startDate") ?: ""
                    val endDate = document.getString("endDate") ?: ""
                    val title = document.getString("title") ?: ""
                    val description = document.getString("description") ?: ""
                    val isPublic = document.get("isPublic") as Boolean

                    if(!isPublic && currentUserId!=userId) continue

                    val destinationList = mutableListOf<Destination>()
                    //if a trip has no destination
                    if (destinationIdList.isEmpty()) {
                        val trip = Trip(
                            tripId,
                            userId,
                            destinationList,
                            numOfPeople,
                            parseStringToLocalDate(startDate),
                            parseStringToLocalDate(endDate),
                            title,
                            description,
                            isPublic
                        )
                        newTrips.add(trip)
                    }

                    for (destinationId in destinationIdList) {
                        val matchingDestination =
                            _destinations.value.find {
                                it.destinationId == destinationId
                            }
                        matchingDestination?.let {
                            destinationList.add(it)
                        }
                        val trip = Trip(
                            tripId,
                            userId,
                            destinationList,
                            numOfPeople,
                            parseStringToLocalDate(startDate),
                            parseStringToLocalDate(endDate),
                            title,
                            description,
                            isPublic
                        )
                        newTrips.add(trip)
                    }
                }
                _trips.value = newTrips
                Log.w(TAG, "Success getting documents: getAllTrip")

            }.addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: getAllTrip", exception)
            }
    }
    fun updateIsPublicForTrip(db: FirebaseFirestore,
                              tripId: String,
                              isPublic: Boolean,
                              context: Context
    ){
        val docRef = db.collection("trips").document(tripId)

        db.runTransaction { transaction ->
            transaction.update(docRef, "isPublic", isPublic)
        }.addOnSuccessListener {
            Log.d(TAG, "Transaction updateIsPublicForTrip: success!")
            Log.d(TAG, "isPublic: $isPublic")
            showMessage(context,"Trip Visibility Updated")
        }.addOnFailureListener { e ->
            Log.w(TAG, "Transaction updateIsPublicForTrip: failure", e)
        }
    }
    // Save this destination to a trip current user created
    fun addDestinationIdOnTrip(db: FirebaseFirestore,
                        tripId: String,
                        destinationId: String,
                        context: Context
    ) {
        val docRef = db.collection("trips").document(tripId)

        db.runTransaction { transaction ->
            val currentIdList = transaction.get(docRef).get("destinationIdList") as? MutableList<String> ?: mutableListOf()

            if (!currentIdList.contains(destinationId)) {
                currentIdList.add(destinationId)
                transaction.update(docRef, "destinationIdList", currentIdList)
            } else {
                showMessage(context,"Cannot add duplicate destination")
                Log.d(TAG, "Cannot add duplicate destination")
                return@runTransaction
            }
        }
        .addOnSuccessListener {
            Log.d(TAG, "Transaction success: TripId added to tripList on user")
            showMessage(context,"Trip Added")
        }
        .addOnFailureListener { e ->
            Log.w(TAG, "Transaction failure: TripId added to tripList on user: $e")
        }
    }
    //On a destination detail screen, user can get the tripId and tripTitle of the trips they created
    fun getTripsForCurrentUser(db: FirebaseFirestore, callback: (List<Pair<String, String>>) -> Unit){
        val userId = getCurrentUserId()
        db.collection("users")
            .document("user $userId")
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val tripIdList = document.get("destinationIdList") as? MutableList<String> ?: mutableListOf()
                    val tripTitleList = mutableListOf<Pair<String, String>>()
                    for (tripId in tripIdList) {
                        getTripTitleByTripId(db, tripId) { tripTitle ->
                            tripTitleList.add(Pair(tripId, tripTitle))
                            // Check if all titles have been retrieved
                            if (tripTitleList.size == tripIdList.size) {
                                callback(tripTitleList)
                            }
                        }
                    }

                    Log.d(TAG, "Success: get TripIdList ForCurrentUser")
                } else {
                    Log.d(TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: getCurrentUserCurrencyFromUser", exception)
            }

    }
    fun getTripTitleByTripId(db: FirebaseFirestore, tripId: String, callback: (String) -> Unit){
        val userId = getCurrentUserId()
        db.collection("trips")
            .document(tripId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val title = document.getString("title") ?: ""
                    callback(title)
                    Log.d(TAG, "Success: getTripTitleByTripId")
                } else {
                    Log.d(TAG, "No such document: getTripTitleByTripId")
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: getTripTitleByTripId", exception)
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

    @RequiresApi(Build.VERSION_CODES.O)
    fun parseStringToLocalDate(dateString: String): LocalDate {
        // Define the date format
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        // Parse the string to LocalDate
        return LocalDate.parse(dateString, formatter)
    }


    //Sorting + Filtering
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

        _destinations.value = if (isReversed.value == true) sortedList.reversed().toMutableList() else sortedList.toMutableList()
    }

    private fun getavgRating(d: Destination): Double {
        if (d.reviewList.isEmpty()) {
            return 0.0
        }
        val totalRating = d.reviewList.sumOf { it.rating }
        return totalRating.toDouble() / d.reviewList.size
    }

    fun toggleSortOrder(currentSortOption: SortingOption? = null) {
        isReversed.value = !(isReversed.value ?: false)
        applyCurrentFiltersAndSort()
    }

}