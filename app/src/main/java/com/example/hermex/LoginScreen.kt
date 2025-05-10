package com.example.hermex

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@Composable
fun LoginScreen(navController: NavController) {
    var email by remember { mutableStateOf(TextFieldValue("")) }
    var password by remember { mutableStateOf(TextFieldValue("")) }
    var isLoginEnabled by remember { mutableStateOf(true) }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Title
        Text(
            text = "Accedi al tuo account",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // Email Field
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Email") },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Password Field
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(stringResource(R.string.password)) },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = stringResource(R.string.password)) },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Login Button
        Button(
            onClick = {
                // Placeholder per logica login
                // TODO: controllo creds
                if (email.text.isEmpty() || password.text.isEmpty()) {
                    Toast.makeText(
                        navController.context,
                        context.getString(R.string.per_favore_inserisci_email_e_password),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        navController.context,
                        context.getString(R.string.login_effettuato_con_successo),
                        Toast.LENGTH_SHORT
                    ).show()
                    navController.navigate(Screen.Home.route)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(50),
            enabled = isLoginEnabled
        ) {
            Text(text = stringResource(R.string.accedi))
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = {
                //navController.navigate(Screen.CreateProfile.route)
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(50)
        ) {
            Text(text = stringResource(R.string.crea_profilo))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen(navController = rememberNavController())
}
