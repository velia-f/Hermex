package com.example.hermex

import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarHalf
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.hermex.TokenManager.getToken
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header

interface InlineServiceApi {
    @GET("api/servizi")
    suspend fun getServizi(@Header("Authorization") token: String): List<Service>
}

@Composable
fun ServiceListScreen(navController: NavController) {
    val context = LocalContext.current
    val token = getToken(context)
    val scope = rememberCoroutineScope()
    var servizi by remember { mutableStateOf<List<Service>>(emptyList()) }
    var searchText by remember { mutableStateOf("") }

    LaunchedEffect(true) {
        if (token == null) {
            navController.navigate("login")
            return@LaunchedEffect
        }

        try {
            val retrofit = Retrofit.Builder()
                .baseUrl("http://10.0.2.2:3000/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val api = retrofit.create(InlineServiceApi::class.java)
            servizi = api.getServizi("Bearer $token")
        } catch (e: Exception) {
            Log.e("API_ERROR", "Errore nel recupero servizi", e)
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("Design", "Dev", "Marketing", "AI", "Writing", "Altro").forEach {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Brush.horizontalGradient(listOf(Color(0xFF6A11CB), Color(0xFF2575FC))))
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(it, color = Color.White, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = searchText,
            onValueChange = { searchText = it },
            placeholder = { Text("Cerca servizi...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            items(servizi.filter {
                it.nome_servizio.contains(searchText, true) || it.descrizione_servizio.contains(searchText, true)
            }) { service ->
                ServiceItem(service) {
                    navController.navigate("service_detail/${service.id_servizio}")
                }
            }
        }
    }
}

@Composable
fun ServiceItem(service: Service, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .clickable(onClick = onClick)
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(16.dp))
            .background(Color.White, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = service.immagine_autore,
                contentDescription = "Foto autore",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(service.nome_servizio, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(
                    text = service.descrizione_servizio,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text("Autore: ${service.autore}", fontSize = 12.sp, color = Color.DarkGray)
                Text("Prezzo: €${"%.2f".format(service.prezzo)}", fontSize = 12.sp, color = Color.Black)

                Row(verticalAlignment = Alignment.CenterVertically) {
                    val fullStars = service.rating.toInt()
                    val hasHalfStar = (service.rating - fullStars) >= 0.5
                    repeat(fullStars) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFC107))
                    }
                    if (hasHalfStar) {
                        Icon(Icons.Default.StarHalf, contentDescription = null, tint = Color(0xFFFFC107))
                    }
                    repeat(5 - fullStars - if (hasHalfStar) 1 else 0) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = Color.LightGray)
                    }

                    Text(
                        text = String.format("%.1f", service.rating),
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
