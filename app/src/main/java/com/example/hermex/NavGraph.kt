package com.example.hermex

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun NavGraph(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {
        composable(Screen.Home.route) { ServiceListScreen(navController) }
        composable(Screen.Add.route) { VenditaServizioScreen(navController) }
        composable(Screen.Earn.route) { GuadagnaCoinScreen(navController) }
        composable(Screen.Profile.route) { ProfileScreen(navController) }
        //composable(Screen.ServiceDetail.route) { ServiceDetailScreen(navController) }
        composable(
            route = "service_detail/{serviceId}",
            arguments = listOf(navArgument("serviceId") { type = NavType.IntType })
        ) { backStackEntry ->
            val serviceId = backStackEntry.arguments?.getInt("serviceId") ?: -1
            ServiceDetailScreen(serviceId = serviceId, navController = navController)
        }
        composable(Screen.Login.route) { LoginScreen(navController) }
        composable(Screen.RegisterScreen.route) { RegisterScreen(navController) }
    }
}
