package com.example.hermex

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.filled.StarHalf
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@Composable
fun ServiceListScreen(navController: NavController) {
    var searchText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Categorie top scroll
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("Design", "Dev", "Marketing", "AI", "Writing", "Altro").forEach { category ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(Color(0xFF6A11CB), Color(0xFF2575FC))
                            )
                        )
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(text = category, color = Color.White, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Search bar
        OutlinedTextField(
            value = searchText,
            onValueChange = { searchText = it },
            placeholder = { Text(stringResource(R.string.search_for_services)) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Lista servizi
        LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            items(FakeServiceRepository.services) { service ->
                ServiceItem(service = service, onClick = {
                    navController.navigate("service_detail/${service.id}")
                })
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
    }
}

@Composable
//funz di prima senza in serviceID
fun SServiceItem(onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .clickable { onClick() }
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(16.dp))
            .background(Color.White, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFB3E5FC)) // Placeholder immagine
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text("Titolo del servizio", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.scale(0.8f)
                    ) {
                        repeat(4) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = Color(0xFFFFC107),
                                modifier = Modifier.size(16.dp) // Stelle più piccole
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color.LightGray,
                            modifier = Modifier.size(16.dp)
                        )
                        //Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "4.0",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }
                Text("Una descrizione breve del servizio disponibile.", fontSize = 10.sp, color = Color.Gray)
                Text("Autore: Mario Rossi", fontSize = 12.sp, color = Color.DarkGray)
            }

            Spacer(modifier = Modifier.width(8.dp))
        }
    }
}

@Composable
fun ServiceItem(service: Service, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .clickable { onClick() }
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(16.dp))
            .background(Color.White, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFB3E5FC))
            )
            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(service.title, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(service.description, fontSize = 12.sp, color = Color.Gray)
                Text(service.author, fontSize = 12.sp, color = Color.DarkGray)
                //Text("${service.rating}", fontSize = 12.sp, color = Color.Gray)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val fullStars = service.rating.toInt()
                    val hasHalfStar = (service.rating - fullStars) >= 0.5

                    repeat(fullStars) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Stella piena",
                            tint = Color(0xFFFFC107)
                        )
                    }

                    if (hasHalfStar) {
                        Icon(
                            imageVector = Icons.Default.StarHalf,
                            contentDescription = "Mezza stella",
                            tint = Color(0xFFFFC107)
                        )
                    }

                    val remainingStars = 5 - fullStars - if (hasHalfStar) 1 else 0
                    repeat(remainingStars) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Stella vuota",
                            tint = Color.LightGray
                        )
                    }

                    Text(
                        text = "${service.rating}",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 4.dp),
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                }

            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun ServiceListScreenPreview() {
    ServiceListScreen(navController = rememberNavController())
}
