package com.example.hermex

import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

data class NavItem(val label: String, val icon: ImageVector)

@Composable
fun BottomNavBar() {
    val items = listOf(
        NavItem(stringResource(R.string.home), Icons.Default.Home),
        NavItem(stringResource(R.string.cerca), Icons.Default.Search),
        NavItem(stringResource(R.string.aggiungi), Icons.Default.Add),
        NavItem(stringResource(R.string.guadagna), Icons.Default.AttachMoney),
        NavItem(stringResource(R.string.profile), Icons.Default.Person)
    )

    var selectedItem by remember { mutableStateOf(0) }

    NavigationBar {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = selectedItem == index,
                onClick = {
                    selectedItem = index
                    // TODO: Gestisci la navigazione qui
                },
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) }
            )
        }
    }
}
