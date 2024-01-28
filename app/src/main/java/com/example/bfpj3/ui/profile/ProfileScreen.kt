package com.example.bfpj3.ui.profile

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.bfpj3.R

@Composable
fun ProfileScreen(navController: androidx.navigation.NavController) {
    //TODO USE ViewModel
//    var displayName by remember { mutableStateOf("") }
    var profileImageUri by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ProfileImageSection(profileImageUri)
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Display Name: ", modifier = Modifier.align(Alignment.Start),
            style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(8.dp))
//        DisplayNameSection(displayName) { newName ->
//            displayName = newName
//        }
        EditableNameField()
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { navController.navigate("reviewHistory") }) {
            Text("Check Review History")
        }
        Spacer(modifier = Modifier.height(32.dp))
        DeleteAccountButton()

    }
}

@Composable
fun ProfileImageSection(profileImageUri: String?) {
    val imageModifier = Modifier
        .size(100.dp)
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

        IconButton(
            onClick = { }, // TODO
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
fun EditableNameField() {
    var name by remember { mutableStateOf("Current Name") }
    var tempName by remember { mutableStateOf(name) }
    var inEditMode by remember { mutableStateOf(false) }
    val minHeight = 56.dp

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().padding(start = 8.dp)
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
                name = tempName
                inEditMode = false
            }) {
                Icon(Icons.Default.Check, contentDescription = "Save")
            }
            IconButton(onClick = {
                tempName = name
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
                Text(text = name)
            }
            IconButton(onClick = {
                inEditMode = true
                tempName = name // Reset tempName to current name
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
