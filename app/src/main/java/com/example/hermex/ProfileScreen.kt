package com.example.hermex

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@Composable
fun ProfileScreen(navController: NavController) {
    val scrollState = rememberScrollState()
    /*Scaffold(
        bottomBar = { BottomNavBar(navController = navController) }
    ) { paddingValues ->*/
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {

            // Top bar
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                /*Button(onClick = {
                    // TODO: Torna indietro
                }) {
                    Text(stringResource(R.string.back))
                }*/

                Text(
                    text = stringResource(R.string.profile),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(horizontal = 8.dp),
                                    //.align(Alignment.CenterHorizontally)
                )

                Button(onClick = {
                    // TODO: Modifica profilo
                }) {
                    Text(stringResource(R.string.edit))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Profile picture placeholder
            Box(
                modifier = Modifier
                    .size(150.dp)
                    .background(Color.LightGray, shape = RoundedCornerShape(8.dp))
                    .align(Alignment.CenterHorizontally),
                contentAlignment = Alignment.Center
            ) {
                Text("X", style = MaterialTheme.typography.displayMedium)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Change background button
            Button(
                onClick = {
                    // TODO: Cambia sfondo
                },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(stringResource(R.string.change_background))
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Display name
            ProfileTextField(title = stringResource(R.string.display_name))
            //{ // TODO: Modifica nome visualizzato }

            // Email
            ProfileTextField(title = stringResource(R.string.email))
            //{ // TODO: Modifica email }

            // Password
            ProfileTextField(title = stringResource(R.string.password))
            //{ // TODO: Modifica password }

            Spacer(modifier = Modifier.height(24.dp))

            // Log out
            ProfileButton(title = stringResource(R.string.log_out)) {
                // TODO: Logout
                navController.navigate(Screen.Login.route)
            }

            // Delete profile
            ProfileButton(title = stringResource(R.string.delete_profile)) {
                // TODO: Cancella profilo
            }
        }
  //  }
}

@Composable
fun ProfileField(title: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .border(1.dp, Color.Black, shape = RoundedCornerShape(6.dp))
            .clickable { onClick() }
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(text = title, style = MaterialTheme.typography.bodyLarge)
    }

    Spacer(modifier = Modifier.height(8.dp))
}

@Composable
fun ProfileTextField(title: String) {
    val textToSave = remember { mutableStateOf("") }
    OutlinedTextField(
        value = textToSave.value,
        onValueChange = { textToSave.value = it /* TODO: salvare*/ },
        placeholder = { Text(title) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        singleLine = true
    )
}

@Composable
fun ProfileButton(title: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        shape = RoundedCornerShape(6.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
    ) {
        Text(title, color = Color.Black)
    }
    Spacer(modifier = Modifier.height(8.dp))
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    ProfileScreen(navController = rememberNavController())
}