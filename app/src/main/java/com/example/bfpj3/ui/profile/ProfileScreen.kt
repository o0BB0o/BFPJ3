package com.example.bfpj3.ui.profile

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.example.bfpj3.R
import com.example.bfpj3.database.FirebaseViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import coil.compose.rememberImagePainter
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation

@Composable
fun ProfileScreen(navController: NavController, db: FirebaseFirestore, storage: FirebaseStorage, firebaseViewModel: FirebaseViewModel) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .padding(bottom = 60.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        ProfileImageSection(storage, db, firebaseViewModel)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Display Name: ",
            modifier = Modifier.align(Alignment.Start),
            style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(8.dp)
        )
//          DisplayNameSection(displayName) { newName ->
//             displayName = newName
//          }
        EditableNameField(db,firebaseViewModel)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { navController.navigate("reviewHistory") }) {
            Text("Check Review History")
        }
        Spacer(modifier = Modifier.weight(1f))
        DeleteAccountButton()

    }
}

@Composable
fun ProfileImageSection(storage: FirebaseStorage, db: FirebaseFirestore, firebaseViewModel: FirebaseViewModel) {
    val imageModifier = Modifier
        .size(100.dp)
        .clip(CircleShape)
        .border(2.dp, Color.Gray, CircleShape)
    val profileImageUri by firebaseViewModel.profilePicDownloadUri.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(firebaseViewModel.profilePicDownloadUri.collectAsState()) {
        firebaseViewModel.getCurrentUserProfilePicUriFromProfile(db)
    }

    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { selectedUri ->
            // Handle the selected image URI by uploading it to Firebase Cloud Storage
            firebaseViewModel.updateCurrentUserProfilePic(db, storage, selectedUri, context)
        }
    }

    Box {
        if (profileImageUri.isNotBlank()) {
            // TODO: Load the image from the URI
            AsyncImage(
                model = profileImageUri,
                modifier = imageModifier,
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = R.drawable.ic_launcher_foreground),
                error = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = "Image from Firebase")
        } else {
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = "Profile Picture Placeholder",
                modifier = imageModifier
            )
        }

        IconButton(
            onClick = {
                launcher.launch("image/*")
            },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(32.dp)
                .background(Color.White)
                .padding(4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Change Profile Picture",
                tint = Color.Black
            )
        }
    }
}

//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun DisplayNameSection(displayName: String, onNameChange: (String) -> Unit) {
//    OutlinedTextField(
//        value = displayName,
//        onValueChange = onNameChange,
//        label = { Text("Display Name") }
//    )
//}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditableNameField(db: FirebaseFirestore, firebaseViewModel: FirebaseViewModel) {
    val displayName by firebaseViewModel.displayName.collectAsState()
    var tempName by remember { mutableStateOf("") }
    var inEditMode by remember { mutableStateOf(false) }
    val minHeight = 56.dp

    LaunchedEffect(firebaseViewModel.displayName.collectAsState()) {
        firebaseViewModel.getCurrentUserDisplayNameFromProfile(db)
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp)
    ) {
        if (inEditMode) {
            // TextField for editing the name with a minimum height
            OutlinedTextField(
                value = tempName,
                onValueChange = { tempName = it },
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = minHeight)
            )
            IconButton(onClick = {
                firebaseViewModel.updateDisplayNameOnProfile(db, tempName)
                inEditMode = false
            }) {
                Icon(Icons.Default.Check, contentDescription = "Save")
            }
            IconButton(onClick = {
                tempName = displayName
                inEditMode = false
            }) {
                Icon(Icons.Default.Close, contentDescription = "Cancel")
            }
        } else {
            // Box to ensure Text is vertically centered and has a minimum height
            Box(modifier = Modifier
                .weight(1f)
                .heightIn(min = minHeight),
                contentAlignment = Alignment.CenterStart) {
                Text(text = displayName)
            }
            IconButton(onClick = {
                inEditMode = true
            }) {
                Icon(Icons.Default.Edit, contentDescription = "Edit")
            }
        }
    }
}


@Composable
fun DeleteAccountButton() {
    Button(onClick = { /* TODO: Implement delete account functionality */ },
        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)) {
        Text("Delete Account", color = Color.White)
    }
}

// Placeholder drawable resource
// Replace with your actual drawable resource ID
