package com.example.hermex

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold

@Composable
fun ServiceListScreen() {
    Scaffold(
        bottomBar = { BottomNavBar() }
    ) { paddingValues ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)) {

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                repeat(6) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color.LightGray, RoundedCornerShape(6.dp))
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // barra di Search
            OutlinedTextField(
                value = "",
                onValueChange = { /* TODO */ },
                placeholder = { Text(stringResource(R.string.search_for_services)) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // lista servizi
            LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                items(3) {
                    ServiceItem()
                }
            }
        }
    }
}

@Composable
fun ServiceItem() {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(Color.LightGray)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(stringResource(R.string.service_title), fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(stringResource(R.string.descrizione))
                Text(stringResource(R.string.author_name), style = MaterialTheme.typography.bodySmall)
            }

            Row {
                repeat(5) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = null,
                        tint = Color.Gray
                    )
                }
            }
        }

        Divider(modifier = Modifier.padding(top = 8.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun ServiceListScreenPreview() {
    ServiceListScreen()
}