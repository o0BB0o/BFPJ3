package com.example.bfpj3.ui.home

import androidx.activity.ComponentActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material.Chip
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.bfpj3.R
import com.example.bfpj3.ui.data.Destination
import com.example.bfpj3.ui.data.Review

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DestinationDetail() {
    val viewModel:HomeViewModel = viewModel(LocalContext.current as ComponentActivity)
    val destination by viewModel.selectedDestination.observeAsState()
    Column(modifier = Modifier.padding(16.dp).verticalScroll(rememberScrollState())) {
        Text(destination!!.name, style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(top = 32.dp))
        Text(destination!!.location)
        Image(
            painter = rememberAsyncImagePainter(model = destination!!.imageUrl),
            contentDescription = "Destination Image",
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        )
        val avgRating = viewModel.getavgRating(destination!!)
        Text("Average Rating: $avgRating",
            style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.size(16.dp))
        Text("Things to Do", style = MaterialTheme.typography.headlineSmall)
        Row(){
            destination!!.thingsTodo.forEach { tag ->
                Chip(onClick = {}) {
                    androidx.compose.material.Text(tag)
                }
            }
        }

        Spacer(modifier = Modifier.size(16.dp))
        ReviewsSection(destination!!, viewModel)

        Spacer(modifier = Modifier.size(16.dp))
        if (!viewModel.hasUserReviewed(destination!!)) {
            WriteReviewSection(onSubmit = { rating, description ->
                // viewModel.submitReview(destination, rating, description)
                //TODO submit review
            })
        }
    }
}


@Composable
fun ReviewsSection(destination: Destination, viewModel:HomeViewModel) {
    destination.reviewList.forEach { review ->
        ReviewItem(review)
    }
}

@Composable
fun ReviewItem(review: Review) {
    Row(modifier = Modifier.padding(top = 8.dp)) {
        // getUserInfo() TODO Need fun from Firebase for user Icon
        Column {
            ProfileImageSection(null)
            Text("User name") //TODO
        }

        Column {
            Text(review.title, style = MaterialTheme.typography.headlineSmall)
            ReviewRatingBar(review.rating)
            Text(review.description)
        }
    }
}

@Composable
fun WriteReviewSection(onSubmit: (Int, String) -> Unit) {
    var rating by remember { mutableStateOf(0) } //TODO move to VM
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    Text("Write your review! ", style = MaterialTheme.typography.headlineSmall)
    ReviewTitleEdit(title) { title = it }
    ReviewContentEdit(description) { description = it }
    RatingBar(rating) { rating = it }
    Button(onClick = { onSubmit(rating, description) }) {
        Text("Submit Review")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewTitleEdit(title: String, onTitleChange: (String) -> Unit) {
    TextField(
        value = title,
        onValueChange = { newText ->
            val filteredText = newText.replace("\n", "")
            onTitleChange(filteredText)
        },
        modifier = Modifier.fillMaxWidth(),
        label = { Text("Title") }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewContentEdit(content: String, onContentChange: (String) -> Unit) {
    val wordCount = content.trim().split("\\s+".toRegex()).count { it.isNotEmpty() }
    val isOverLimit = wordCount > 100

    TextField(
        value = content,
        onValueChange = { newText ->
            val newWordCount = newText.trim().split("\\s+".toRegex()).count { it.isNotEmpty() }
            if (newWordCount <= 100) {
                onContentChange(newText)
            }
        },
        modifier = Modifier.fillMaxWidth().height(120.dp),
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
    Row {
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
        if (profileImageUri != null) {
            // TODO: Load the image from the URI
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = "Profile Picture",
                modifier = imageModifier
            )
        } else {
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = "Profile Picture Placeholder",
                modifier = imageModifier
            )
        }
    }
}