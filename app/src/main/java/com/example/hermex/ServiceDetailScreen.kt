package com.example.hermex

import android.content.Intent
import android.net.Uri
import android.util.Base64
import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarHalf
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.hermex.TokenManager.getToken
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface InternalServiceApi {
    @GET("api/servizio/{id}")
    suspend fun getService(
        @Path("id") id: Int,
        @Header("Authorization") token: String
    ): Service
}

interface ServiceListApi {
    @GET("api/servizi")
    suspend fun getAllServices(@Header("Authorization") token: String): List<Service>
}

@Composable
fun ServiceDetailScreen(serviceId: Int, navController: NavController) {
    val context = LocalContext.current
    val token = getToken(context)
    var service by remember { mutableStateOf<Service?>(null) }
    var idUtenteLoggato by remember { mutableStateOf<Int?>(null) }
    var recentServices by remember { mutableStateOf<List<Service>>(emptyList()) }

    LaunchedEffect(true) {
        if (token == null) {
            navController.navigate("login")
            return@LaunchedEffect
        }

        try {
            val payload = token.split(".")[1]
            val decoded = String(Base64.decode(payload, Base64.URL_SAFE))
            val json = JSONObject(decoded)
            idUtenteLoggato = json.getInt("id")
        } catch (e: Exception) {
            Toast.makeText(context, "Token non valido", Toast.LENGTH_SHORT).show()
        }

        try {
            val retrofit = Retrofit.Builder()
                .baseUrl("https://hermex-api.onrender.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val api = retrofit.create(InternalServiceApi::class.java)
            service = api.getService(serviceId, "Bearer $token")

            val serviceListApi = retrofit.create(ServiceListApi::class.java)
            val allServices = serviceListApi.getAllServices("Bearer $token")
            recentServices = allServices.filter { it.id_servizio != serviceId }.take(3)
        } catch (e: Exception) {
            Toast.makeText(context, "Errore nel caricamento", Toast.LENGTH_SHORT).show()
        }
    }

    service?.let { s ->
        val isAutore = s.id_utente == idUtenteLoggato

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .background(Color.Black)
            ) {
                AsyncImage(
                    model = s.immagine_servizio,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp),
                    contentScale = ContentScale.Crop
                )

            }


            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = s.nome_servizio,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = s.descrizione_servizio,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Autore: ${s.autore}", style = MaterialTheme.typography.bodyMedium)

                RatingBar(s.rating)
            }
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Localizzazione: ${s.localizzazione}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.DarkGray
            )

            Spacer(modifier = Modifier.height(16.dp))


            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                if (!isAutore) {
                    CompraButton(s, token!!, context, navController)
                    ContattaButton(context)
                } else {
                    ModificaButton(s.id_servizio, navController)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Ultimi servizi pubblicati",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            recentServices.forEach { item ->
                ServiceCard(item, navController)
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    } ?: Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Caricamento...", style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
fun RatingBar(rating: Float) {
    val fullStars = rating.toInt()
    val hasHalfStar = (rating - fullStars) >= 0.5

    Row(verticalAlignment = Alignment.CenterVertically) {
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
            text = String.format("%.1f", rating),
            color = Color.Gray,
            fontSize = 12.sp,
            modifier = Modifier.padding(start = 4.dp)
        )
    }
}

@Composable
fun CompraButton(service: Service, token: String, context: android.content.Context, navController: NavController) {
    Button(
        onClick = {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val client = okhttp3.OkHttpClient()
                    val json = """{ "id_servizio": ${service.id_servizio} }"""
                    val body = okhttp3.RequestBody.create("application/json".toMediaTypeOrNull(), json)
                    val request = okhttp3.Request.Builder()
                        .url("https://hermex-api.onrender.com/api/servizio/acquista")
                        .addHeader("Authorization", "Bearer $token")
                        .post(body)
                        .build()

                    val response = client.newCall(request).execute()
                    val responseBody = response.body?.string()

                    withContext(Dispatchers.Main) {
                        if (response.isSuccessful) {
                            Toast.makeText(context, "Acquisto completato!", Toast.LENGTH_SHORT).show()
                            navController.navigate("profile")
                        } else {
                            Toast.makeText(context, "Errore: $responseBody", Toast.LENGTH_LONG).show()
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Errore durante l'acquisto", Toast.LENGTH_LONG).show()
                    }
                }
            }
        },
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF2575FC),
            contentColor = Color.White
        )
    ) {
        Text("Compra • ${String.format("%.2f", service.prezzo)}")
    }
}

@Composable
fun ContattaButton(context: android.content.Context) {
    OutlinedButton(
        onClick = {
            val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:info@hermex.com")
            }
            context.startActivity(emailIntent)
        },
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color(0xFF2575FC))
    ) {
        Text("Contatta", color = Color(0xFF2575FC))
    }
}

@Composable
fun ModificaButton(serviceId: Int, navController: NavController) {
    OutlinedButton(
        onClick = { navController.navigate("modifica_servizio/$serviceId") },
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color(0xFF4CAF50))
    ) {
        Text("Modifica", color = Color(0xFF4CAF50))
    }
}

@Composable
fun ServiceCard(item: Service, navController: NavController) {
    Column(
        modifier = Modifier
            .clickable { navController.navigate("service_detail/${item.id_servizio}") }
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(16.dp))
            .background(Color.White, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = item.immagine_servizio,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray)
            )


            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(item.nome_servizio, fontWeight = FontWeight.Bold)
                Text("di ${item.autore}", color = Color.Gray)
                Text(
                    "${String.format("%.2f", item.prezzo)}",
                    color = Color(0xFF2575FC),
                    fontWeight = FontWeight.Medium
                )
                RatingBar(item.rating)
            }
        }
    }
}
