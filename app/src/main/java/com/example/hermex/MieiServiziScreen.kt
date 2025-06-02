package com.example.hermex

import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarHalf
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.hermex.TokenManager.getToken
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header

interface MieiServiziApi {
    @GET("api/user/servizi")
    suspend fun getMieiServizi(@Header("Authorization") token: String): List<Service>
}

@Composable
fun MieiServiziScreen(navController: NavController) {
    val context = LocalContext.current
    var servizi by remember { mutableStateOf<List<Service>>(emptyList()) }
    var searchText by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        val token = getToken(context)
        Log.d("MieiServiziScreen", "Token: $token")

        if (token == null) {
            navController.navigate("login")
            return@LaunchedEffect
        }

        try {
            val retrofit = Retrofit.Builder()
                .baseUrl("https://hermex-api.onrender.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val api = retrofit.create(MieiServiziApi::class.java)
            val response = api.getMieiServizi("Bearer $token")
            Log.d("MieiServiziScreen", "Servizi caricati: ${response.size}")
            servizi = response
        } catch (e: Exception) {
            Log.e("MieiServiziScreen", "Errore caricamento miei servizi", e)
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        OutlinedTextField(
            value = searchText,
            onValueChange = { searchText = it },
            placeholder = { Text("Cerca nei tuoi servizi...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
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
