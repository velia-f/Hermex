package com.example.hermex

import android.content.Intent
import android.net.Uri
import android.util.Base64
import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import org.json.JSONObject

data class OrdineRicevuto(
    val id_ordine: Int,
    val stato: String,
    val data_acquisto: String,
    val acquirente: String,
    val nome_servizio: String,
    val prezzo: Double,
    val email: String
)

interface OrdiniApi {
    @GET("api/ordini/ricevuti")
    suspend fun getOrdiniRicevuti(@Header("Authorization") token: String): List<OrdineRicevuto>

    @POST("api/ordini/{id}/aggiorna")
    suspend fun aggiornaOrdine(
        @Path("id") idOrdine: Int,
        @Header("Authorization") token: String,
        @Body body: Map<String, String>
    ): retrofit2.Response<Unit>
}

@Composable
fun OrdiniRicevutiScreen(navController: NavController) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var ordini by remember { mutableStateOf<List<OrdineRicevuto>>(emptyList()) }
    var token by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(true) {
        token = getToken(context)
        if (token == null) {
            navController.navigate("login")
            return@LaunchedEffect
        }

        try {
            val retrofit = Retrofit.Builder()
                .baseUrl("http://10.0.2.2:3000/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val api = retrofit.create(OrdiniApi::class.java)
            ordini = api.getOrdiniRicevuti("Bearer $token")
        } catch (e: Exception) {
            Toast.makeText(context, "Errore nel caricamento", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("Ordini Ricevuti", fontSize = 24.sp, fontWeight = FontWeight.Bold)

        if (ordini.isEmpty()) {
            Spacer(modifier = Modifier.height(32.dp))
            Text("Attualmente non hai ordini in entrata", color = Color.Gray)
        } else {
            Spacer(modifier = Modifier.height(16.dp))
            ordini.forEach { ordine ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(ordine.nome_servizio, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("Acquirente: ${ordine.acquirente}")
                        Text("Prezzo: €${String.format("%.2f", ordine.prezzo)}")
                        Text("Data: ${ordine.data_acquisto}")

                        Spacer(modifier = Modifier.height(12.dp))

                        when (ordine.stato) {
                            "in_attesa" -> {
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button(onClick = {
                                        coroutineScope.launch {
                                            val retrofit = Retrofit.Builder()
                                                .baseUrl("http://10.0.2.2:3000/")
                                                .addConverterFactory(GsonConverterFactory.create())
                                                .build()

                                            val api = retrofit.create(OrdiniApi::class.java)
                                            try {
                                                api.aggiornaOrdine(
                                                    ordine.id_ordine,
                                                    "Bearer $token",
                                                    mapOf("nuovaStato" to "accettato")
                                                )
                                                ordini = ordini.map {
                                                    if (it.id_ordine == ordine.id_ordine)
                                                        it.copy(stato = "accettato") else it
                                                }
                                            } catch (e: Exception) {
                                                Toast.makeText(context, "Errore aggiornamento", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    }) {
                                        Text("Accetta")
                                    }

                                    OutlinedButton(onClick = {
                                        coroutineScope.launch {
                                            val retrofit = Retrofit.Builder()
                                                .baseUrl("http://10.0.2.2:3000/")
                                                .addConverterFactory(GsonConverterFactory.create())
                                                .build()

                                            val api = retrofit.create(OrdiniApi::class.java)
                                            try {
                                                api.aggiornaOrdine(
                                                    ordine.id_ordine,
                                                    "Bearer $token",
                                                    mapOf("nuovaStato" to "rifiutato")
                                                )
                                                ordini = ordini.map {
                                                    if (it.id_ordine == ordine.id_ordine)
                                                        it.copy(stato = "rifiutato") else it
                                                }
                                            } catch (e: Exception) {
                                                Toast.makeText(context, "Errore aggiornamento", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    }) {
                                        Text("Rifiuta")
                                    }
                                }
                            }

                            else -> {
                                Text("Stato: ${ordine.stato}", color = when (ordine.stato) {
                                    "accettato" -> Color(0xFF4CAF50)
                                    "rifiutato" -> Color(0xFFF44336)
                                    else -> Color.Gray
                                })

                                if (ordine.stato == "accettato") {
                                    OutlinedButton(
                                        onClick = {
                                            val intent = Intent(Intent.ACTION_SENDTO).apply {
                                                data = Uri.parse("mailto:${ordine.email}")
                                            }
                                            context.startActivity(intent)
                                        },
                                        modifier = Modifier.padding(top = 8.dp)
                                    ) {
                                        Text("Contatta ${ordine.acquirente}")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
