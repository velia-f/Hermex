package com.example.hermex

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

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
                .baseUrl("https://hermex-api.onrender.com/")
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
                        Text("Data: ${formatDate(ordine.data_acquisto)}")

                        Spacer(modifier = Modifier.height(12.dp))

                        when (ordine.stato) {
                            "in_attesa" -> {
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button(onClick = {
                                        aggiornaStatoOrdine(ordine, "accettato", token!!, context, coroutineScope) {
                                            ordini = ordini.map {
                                                if (it.id_ordine == ordine.id_ordine) it.copy(stato = "accettato") else it
                                            }
                                        }
                                    }) {
                                        Text("Accetta")
                                    }

                                    OutlinedButton(onClick = {
                                        aggiornaStatoOrdine(ordine, "rifiutato", token!!, context, coroutineScope) {
                                            ordini = ordini.map {
                                                if (it.id_ordine == ordine.id_ordine) it.copy(stato = "rifiutato") else it
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

@RequiresApi(Build.VERSION_CODES.O)
fun formatDate(rawDate: String): String {
    return try {
        val parsed = ZonedDateTime.parse(rawDate)
        val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy, HH:mm", Locale.getDefault())
        parsed.format(formatter)
    } catch (e: Exception) {
        rawDate
    }
}

fun aggiornaStatoOrdine(
    ordine: OrdineRicevuto,
    nuovoStato: String,
    token: String,
    context: android.content.Context,
    coroutineScope: CoroutineScope,
    onSuccess: () -> Unit
) {
    coroutineScope.launch {
        try {
            val retrofit = Retrofit.Builder()
                .baseUrl("https://hermex-api.onrender.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val api = retrofit.create(OrdiniApi::class.java)
            api.aggiornaOrdine(
                ordine.id_ordine,
                "Bearer $token",
                mapOf("nuovaStato" to nuovoStato)
            )
            onSuccess()
        } catch (e: Exception) {
            Toast.makeText(context, "Errore aggiornamento", Toast.LENGTH_SHORT).show()
        }
    }
}
