package com.example.hermex

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun MainScreen() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { BottomNavBar(navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) { ServiceListScreen(navController) }
            composable(Screen.Add.route) { VenditaServizioScreen(navController) }
            composable(Screen.Earn.route) { GuadagnaCoinScreen(navController) }
            composable(Screen.Profile.route) { ProfileScreen(navController) }
            composable(Screen.ServiceDetail.route) { ServiceDetailScreen(navController) }
            composable(Screen.Login.route) { LoginScreen(navController) }
            composable(Screen.RegisterScreen.route) { RegisterScreen(navController) }

        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    MainScreen()
}