package com.example.bfpj3.ui.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.bfpj3.R
import androidx.compose.ui.graphics.Shape

@Composable
fun ProfileScreen(navController: androidx.navigation.NavController) {
    var displayName by remember { mutableStateOf("") }
    var profileImageUri by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ProfileImageSection(profileImageUri)
        Spacer(modifier = Modifier.height(16.dp))
        DisplayNameSection(displayName) { newName ->
            displayName = newName
        }
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

    if (profileImageUri != null) {
        // TODO: Load the image from the URI
        // For now, using a placeholder
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

    Button(onClick = { /* TODO: Implement image picker */ }) {
        Text("Change Profile Picture")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DisplayNameSection(displayName: String, onNameChange: (String) -> Unit) {
    OutlinedTextField(
        value = displayName,
        onValueChange = onNameChange,
        label = { Text("Display Name") }
    )
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
