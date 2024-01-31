import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.Card
import androidx.compose.material.IconToggleButton
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.bfpj3.ui.data.Review
import com.example.bfpj3.ui.profile.ReviewHistoryViewModel

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewHistoryScreen(navController: NavController) {
    val viewModel: ReviewHistoryViewModel = viewModel()
    val reviewList by viewModel.reviews.observeAsState(listOf())
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Review History",
                        style = MaterialTheme.typography.headlineMedium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) {
        ReviewsSection(reviewList)
    }
}

@Composable
fun ReviewsSection(reviewList: List<Review>) {
    var showEditDialog by remember { mutableStateOf(false) }
    var reviewToEdit by remember { mutableStateOf<Review?>(null) }

    LazyColumn {
        items(reviewList) { review ->
            ReviewItem(review) {
                reviewToEdit = review
                showEditDialog = true
            }
        }
    }

    if (showEditDialog && reviewToEdit != null) {
        EditReviewDialog(review = reviewToEdit!!, onDismiss = {
            showEditDialog = false
            reviewToEdit = null
        })
    }
}

@Composable
fun ReviewItem(review: Review, onEditClick: () -> Unit) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        elevation = 4.dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp)
            ) {
                Text(review.destination, style = MaterialTheme.typography.headlineMedium)
                Text(review.title, style = MaterialTheme.typography.headlineSmall)
                ReviewRatingBar(review.rating)
                Text(review.description)
            }
            IconButton(
                onClick = onEditClick,
                modifier = Modifier.align(Alignment.Top)
            ) {
                Icon(Icons.Default.Edit, contentDescription = "Edit Review")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditReviewDialog(review: Review, onDismiss: () -> Unit) {
    var editedDescription by remember { mutableStateOf(review.description) }
    var editedRating by remember { mutableStateOf(review.rating) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Review") },
        text = {
            Column {
                TextField(
                    value = editedDescription,
                    onValueChange = { editedDescription = it },
                    label = { Text("Description") }
                )
                RatingBar(editedRating) { editedRating = it }
            }
        },
        confirmButton = {
            Button(onClick = {
                // TODO: update the review
                onDismiss()
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun RatingBar(currentRating: Int, onRatingChange: (Int) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        (1..5).forEach { index ->
            IconToggleButton(
                checked = currentRating >= index,
                onCheckedChange = { onRatingChange(index) }
            ) {
                val icon =
                    if (currentRating >= index) Icons.Filled.Star else Icons.Filled.StarBorder
                androidx.compose.material.Icon(icon, contentDescription = "Rating $index")
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

