package com.example.hermex

import android.content.Intent
import android.net.Uri
import android.util.Base64
import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.hermex.TokenManager.getToken
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

data class ServiceDetail(
    val id_servizio: Int,
    val nome_servizio: String,
    val descrizione_servizio: String,
    val prezzo: Double,
    val immagine_servizio: String,
    val autore: String,
    val immagine_autore: String,
    val rating: Float,
    val id_utente: Int
)

interface InternalServiceApi {
    @GET("api/servizio/{id}")
    suspend fun getService(
        @Path("id") id: Int,
        @Header("Authorization") token: String
    ): ServiceDetail
}

interface ServiceListApi {
    @GET("api/servizi")
    suspend fun getAllServices(@Header("Authorization") token: String): List<ServiceDetail>
}

@Composable
fun ServiceDetailScreen(serviceId: Int, navController: NavController) {
    val context = LocalContext.current
    val token = getToken(context)
    var service by remember { mutableStateOf<ServiceDetail?>(null) }
    var idUtenteLoggato by remember { mutableStateOf<Int?>(null) }
    var recentServices by remember { mutableStateOf<List<ServiceDetail>>(emptyList()) }

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
                .baseUrl("http://10.0.2.2:3000/")
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
            AsyncImage(
                model = s.immagine_servizio,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(16.dp))
            )

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
                Text(
                    text = "Autore: ${s.autore}",
                    style = MaterialTheme.typography.bodyMedium
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    val fullStars = s.rating.toInt()
                    val hasHalfStar = (s.rating - fullStars) >= 0.5

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
                        text = String.format("%.1f", s.rating),
                        color = Color.Gray,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = { /* Compra */ },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2575FC), contentColor = Color.White)
                ) {
                    Text("Compra • ${String.format("%.2f", s.prezzo)}€")
                }

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

                if (isAutore) {
                    OutlinedButton(
                        onClick = {
                            navController.navigate("modifica_servizio/${s.id_servizio}")
                        },
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color(0xFF4CAF50))
                    ) {
                        Text("Modifica", color = Color(0xFF4CAF50))
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Ultimi servizi pubblicati",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Column {
                recentServices.forEach { item ->
                    Column(
                        modifier = Modifier
                            .clickable {
                                navController.navigate("service_detail/${item.id_servizio}")
                            }
                            .fillMaxWidth()
                            .shadow(6.dp, RoundedCornerShape(16.dp))
                            .background(Color.White, RoundedCornerShape(16.dp))
                            .padding(12.dp)
                    ) {
                        Row {
                            AsyncImage(
                                model = item.immagine_servizio,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(RoundedCornerShape(10.dp))
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.nome_servizio,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "di ${item.autore}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )
                                Text(
                                    text = "€ ${String.format("%.2f", item.prezzo)}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color(0xFF2575FC),
                                    fontWeight = FontWeight.Medium
                                )

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    val fullStars = item.rating.toInt()
                                    val hasHalf = (item.rating - fullStars) >= 0.5
                                    repeat(fullStars) {
                                        Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFC107), modifier = Modifier.size(16.dp))
                                    }
                                    if (hasHalf) {
                                        Icon(Icons.Default.StarHalf, contentDescription = null, tint = Color(0xFFFFC107), modifier = Modifier.size(16.dp))
                                    }
                                    repeat(5 - fullStars - if (hasHalf) 1 else 0) {
                                        Icon(Icons.Default.Star, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(16.dp))
                                    }

                                    Text(
                                        text = String.format("%.1f", item.rating),
                                        color = Color.Gray,
                                        fontSize = 12.sp,
                                        modifier = Modifier.padding(start = 4.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    } ?: run {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Caricamento...", style = MaterialTheme.typography.bodyLarge)
        }
    }
}
