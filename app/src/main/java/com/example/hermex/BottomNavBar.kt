package com.example.hermex

import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.material3.Text
import androidx.compose.material3.NavigationBarItem
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

data class NavItem(val label: String, val icon: ImageVector)

@Composable
fun BottomNavBar(navController: NavController) {
    val screens = listOf(
        Screen.Home,
        Screen.Add,
        Screen.Earn,
        Screen.Profile
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar {
        screens.forEach { screen ->
            NavigationBarItem(
                selected = currentRoute == screen.route,
                onClick = {
                    if (screen.route == Screen.Home.route) {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                            launchSingleTop = true
                        }
                    } else if (screen.route == Screen.Earn.route) {
                        navController.navigate(Screen.Earn.route) {
                            popUpTo(Screen.Earn.route) { inclusive = true }
                            launchSingleTop = true
                        }
                    } else if (screen.route == Screen.Profile.route) {
                        navController.navigate(Screen.Profile.route) {
                            popUpTo(Screen.Profile.route) { inclusive = true }
                            launchSingleTop = true
                        }
                    } else if (screen.route == Screen.Add.route) {
                        navController.navigate(Screen.Add.route) {
                            popUpTo(Screen.Add.route) { inclusive = true }
                            launchSingleTop = true
                        }
                    } else if (screen.route == Screen.ServiceDetail.route) {
                        navController.navigate(Screen.ServiceDetail.route) {
                            popUpTo(Screen.ServiceDetail.route) { inclusive = true }
                            launchSingleTop = true
                        }
                    } else {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = { Icon(screen.icon, contentDescription = screen.label) },
                label = { Text(screen.label) }
            )
        }
    }
}