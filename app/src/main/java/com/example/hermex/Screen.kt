package com.example.hermex

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Home : Screen("home", "Home", Icons.Filled.Home)
    object Profile : Screen("profile", "Profilo", Icons.Filled.Person)
    object Earn : Screen("earn", "Guadagna", Icons.Filled.AttachMoney)
    object Add : Screen("add", "Aggiungi", Icons.Filled.Add)
    object ServiceDetail : Screen("service_detail/{serviceId}", "Dettaglio", Icons.Filled.Info) {
        fun createRoute(serviceId: Int) = "service_detail/$serviceId"
    }
    //object ServiceDetail : Screen("serviceDetail", "Dettaglio", Icons.Filled.Info)
    object Login : Screen("login", "Login", Icons.Filled.Person)
    object RegisterScreen : Screen("registerScreen", "Registrati", Icons.Filled.Person)
}
