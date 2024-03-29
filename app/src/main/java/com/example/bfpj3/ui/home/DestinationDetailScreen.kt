package com.example.bfpj3.ui.home

import android.os.Build
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Chip
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.bfpj3.R
import com.example.bfpj3.database.FirebaseViewModel
import com.example.bfpj3.ui.data.Destination
import com.example.bfpj3.ui.data.Review
import com.google.firebase.firestore.FirebaseFirestore

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterialApi::class, ExperimentalLayoutApi::class)
@Composable
fun DestinationDetail(db: FirebaseFirestore, firebaseViewModel: FirebaseViewModel) {
    val viewModel:HomeViewModel = viewModel(LocalContext.current as ComponentActivity)
    val destination by viewModel.selectedDestination.observeAsState()
    val context = LocalContext.current
    var showAddToTripDialog by remember { mutableStateOf(false) }
    Column(modifier = Modifier
        .padding(16.dp)
        .verticalScroll(rememberScrollState())) {
        Text(destination!!.name, style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(top = 32.dp))
        Text(destination!!.location)
        Text(destination!!.ownerOrganization)
        Text("Recommended Age: ${destination!!.ageRecommendation}")
        AsyncImage(
            model = destination!!.imageUrl,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            placeholder = painterResource(id = R.drawable.ic_launcher_foreground),
            error = painterResource(id = R.drawable.ic_launcher_foreground),
            contentDescription = "Destination Image")
        val avgRating = viewModel.getavgRating(destination!!)
        Spacer(modifier = Modifier.size(16.dp))
        Text(text = destination!!.description)
        Spacer(modifier = Modifier.size(16.dp))
        Text("Things to Do", style = MaterialTheme.typography.headlineSmall)
        FlowRow {
            destination!!.thingsTodo.forEach { tag ->
                Chip(onClick = {}) {
                    androidx.compose.material.Text(tag)
                }
            }
        }
        Spacer(modifier = Modifier.size(16.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "Interested?", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.size(16.dp))
            Button(onClick = {showAddToTripDialog = true}, modifier = Modifier.fillMaxWidth()){
                Text(text = "Add to Trip Now!")
            }
        }
        Spacer(modifier = Modifier.size(16.dp))
        Text("Average Rating: $avgRating",
            style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.size(16.dp))
        ReviewsSection(destination!!, db, firebaseViewModel)

        Spacer(modifier = Modifier.size(16.dp))
        WriteReviewSection(onSubmit = { rating, description, title ->
            firebaseViewModel.storeReviewInfoOnReview(db,destination!!.destinationId,rating,title,description, context)
        })
    }
    if (showAddToTripDialog) {
        AddToTripDialog(
            firebaseViewModel,
            db,
            destination!!.destinationId,
            onDismiss = { showAddToTripDialog = false },
            onSelectTripId = {
                showAddToTripDialog = false
            }
        )
    }
}


@Composable
fun ReviewsSection(
    destination: Destination,
    db: FirebaseFirestore,
    firebaseViewModel: FirebaseViewModel
) {
    if(destination.reviewList.isEmpty()) {
        Text(text = "No Review Right Now. Check back later! ")
    }
    destination.reviewList.forEach { review ->
        ReviewItem(review,db,firebaseViewModel)
    }
}

@Composable
fun ReviewItem(review: Review,db: FirebaseFirestore, firebaseViewModel: FirebaseViewModel) {
    Row(modifier = Modifier.padding(top = 8.dp)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.size(100.dp)) {
            var reviewDisplayName by remember { mutableStateOf("") }
            var profileImageUri by remember { mutableStateOf("") }
            firebaseViewModel.getUserDisplayNameByUserId(db,review.userId){ name ->
                reviewDisplayName = name
            }
            firebaseViewModel.getUserProfileImageUriByUserId(db, review.userId){ uri ->
                profileImageUri = uri
            }
            ProfileImageSection(profileImageUri)
            Text(reviewDisplayName)
        }
        Spacer(modifier = Modifier.size(8.dp))
        Column {
            Text(review.title, style = MaterialTheme.typography.headlineSmall)
            ReviewRatingBar(review.rating)
            Text(review.description)
        }
    }
}

@Composable
fun WriteReviewSection(onSubmit: (Int, String, String) -> Unit) {
    var rating by remember { mutableStateOf(0) }
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    Text("Write your review! ", style = MaterialTheme.typography.headlineSmall)
    ReviewTitleEdit(title) { title = it }
    ReviewContentEdit(description) { description = it }
    RatingBar(rating) { rating = it }
    Button(onClick = { onSubmit(rating, description, title) }, modifier = Modifier.fillMaxWidth()) {
        Text("Submit Review")
    }
}

@Composable
fun ReviewTitleEdit(title: String, onTitleChange: (String) -> Unit) {
    OutlinedTextField(
        value = title,
        onValueChange = { newText ->
            val filteredText = newText.replace("\n", "")
            onTitleChange(filteredText)
        },
        modifier = Modifier.fillMaxWidth(),
        label = { Text("Title") }
    )
}

@Composable
fun ReviewContentEdit(content: String, onContentChange: (String) -> Unit) {
    val wordCount = content.trim().split("\\s+".toRegex()).count { it.isNotEmpty() }

    OutlinedTextField(
        value = content,
        onValueChange = { newText ->
            val newWordCount = newText.trim().split("\\s+".toRegex()).count { it.isNotEmpty() }
            if (newWordCount <= 100) {
                onContentChange(newText)
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        label = { Text("Write your review here!") },
        trailingIcon = {
            Text(
                text = "$wordCount/100",
                style = MaterialTheme.typography.bodySmall
            )
        }
    )
}


@Composable
fun RatingBar(currentRating: Int, onRatingChange: (Int) -> Unit) {
    Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
        (1..5).forEach { index ->
            IconToggleButton(
                checked = currentRating >= index,
                onCheckedChange = { onRatingChange(index) }
            ) {
                val icon = if (currentRating >= index) Icons.Filled.Star else Icons.Filled.StarBorder
                Icon(icon, contentDescription = "Rating $index")
            }
        }
    }
}

@Composable
fun ReviewRatingBar(currentRating: Int) {
    Row() {
        (1..5).forEach { index ->
            val icon = if (currentRating >= index) Icons.Filled.Star else Icons.Filled.StarBorder
            Icon(icon, contentDescription = "Rating $index", modifier = Modifier.size(16.dp))
        }
    }
}


@Composable
fun ProfileImageSection(profileImageUri: String?) {
    val imageModifier = Modifier
        .size(80.dp)
        .clip(CircleShape)
        .border(2.dp, Color.Gray, CircleShape)

    Box {
        AsyncImage(
            model = profileImageUri,
            modifier = imageModifier,
            contentScale = ContentScale.Crop,
            placeholder = painterResource(id = R.drawable.ic_launcher_foreground),
            error = painterResource(id = R.drawable.ic_launcher_foreground),
            contentDescription = "Profile Image")
    }
}