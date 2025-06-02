package com.example.hermex

import android.util.Base64
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

data class ReviewItem(

    val id_ordine: Int,
    val nome_servizio: String,
    val prezzo: Double,
    val venditore: String
)

interface ReviewApi {
    @GET("api/recensioni/da_fare")
    suspend fun getPendingReviews(@Header("Authorization") token: String): List<ReviewItem>

    @POST("api/recensioni")
    suspend fun submitReview(
        @Header("Authorization") token: String,
        @Body review: SubmitReviewRequest
    ): ResponseMessage
}

data class SubmitReviewRequest(val id_ordine: Int, val voto: Int, val commento: String?)
data class ResponseMessage(val message: String)

@Composable
fun LeaveReviewsScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var token by remember { mutableStateOf<String?>(null) }
    var reviewList by remember { mutableStateOf<List<ReviewItem>>(emptyList()) }

    // Carica token asincronamente
    LaunchedEffect(Unit) {
        token = TokenManager.getToken(context)
    }

    // Carica recensioni da fare
    LaunchedEffect(token) {
        if (token != null) {
            try {
                val retrofit = Retrofit.Builder()
                    .baseUrl("https://hermex-api.onrender.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                val api = retrofit.create(ReviewApi::class.java)
                reviewList = api.getPendingReviews("Bearer $token")
            } catch (e: Exception) {
                Toast.makeText(context, "Errore caricamento recensioni", Toast.LENGTH_SHORT).show()
            }
        }
    }

    if (token == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Caricamento token...")
        }
        return
    }

    if (reviewList.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Non hai recensioni da lasciare")
        }
    } else {
        Column(modifier = Modifier.padding(16.dp)) {
            reviewList.forEach { item ->
                var rating by remember { mutableStateOf(0) }
                var comment by remember { mutableStateOf(TextFieldValue("")) }
                var isSubmitting by remember { mutableStateOf(false) }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(item.nome_servizio, style = MaterialTheme.typography.titleMedium)
                        Text("Fornito da: ${item.venditore}", color = Color.DarkGray, fontSize = 14.sp)
                        Text("Prezzo: ${item.prezzo} ", color = Color.Gray)

                        Spacer(modifier = Modifier.height(8.dp))

                        Row {
                            for (i in 1..5) {
                                Icon(
                                    imageVector = if (i <= rating) Icons.Default.Star else Icons.Default.StarBorder,
                                    contentDescription = null,
                                    tint = if (i <= rating) Color(0xFFFFC107) else Color.Gray,
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clickable { rating = i }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = comment,
                            onValueChange = { comment = it },
                            placeholder = { Text("Scrivi un commento (opzionale)") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = {
                                scope.launch {
                                    isSubmitting = true
                                    try {
                                        val retrofit = Retrofit.Builder()
                                            .baseUrl("https://hermex-api.onrender.com/")
                                            .addConverterFactory(GsonConverterFactory.create())
                                            .build()
                                        val api = retrofit.create(ReviewApi::class.java)
                                        val response = api.submitReview(
                                            "Bearer $token",
                                            SubmitReviewRequest(item.id_ordine, rating, comment.text.takeIf { it.isNotBlank() })
                                        )
                                        Toast.makeText(context, response.message, Toast.LENGTH_SHORT).show()
                                        reviewList = reviewList.filterNot { it.id_ordine == item.id_ordine }
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "Errore invio recensione", Toast.LENGTH_SHORT).show()
                                    } finally {
                                        isSubmitting = false
                                    }
                                }
                            },
                            enabled = rating > 0 && !isSubmitting
                        ) {
                            Text(if (isSubmitting) "Invio..." else "Invia recensione")
                        }
                    }
                }
            }
        }
    }
}
