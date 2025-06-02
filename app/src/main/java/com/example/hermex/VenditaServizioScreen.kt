package com.example.hermex

import android.net.Uri
import android.util.Base64
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.hermex.TokenManager.getToken
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import java.io.InputStream

@Composable
fun VenditaServizioScreen(navController: NavController) {
    val titolo = remember { mutableStateOf("") }
    val descrizione = remember { mutableStateOf("") }
    val prezzo = remember { mutableStateOf("") }
    val localizzazione = remember { mutableStateOf("") }
    val immagine = remember { mutableStateOf<Uri?>(null) }

    val showErrors = remember { mutableStateOf(false) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val imagePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        immagine.value = uri
    }

    LaunchedEffect(Unit) {
        val token = getToken(context)
        if (token == null) {
            navController.navigate("login")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(Color(0xFF2575FC)),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.vendita_servizio),
                modifier = Modifier.padding(start = 16.dp),
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Button(
                onClick = {
                    showErrors.value = true

                    val isValid = titolo.value.isNotBlank() &&
                            descrizione.value.isNotBlank() &&
                            prezzo.value.isNotBlank() &&
                            localizzazione.value.isNotBlank() &&
                            immagine.value != null

                    if (!isValid) {
                        Toast.makeText(context, "Compila tutti i campi obbligatori", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    val token = getToken(context)
                    if (token == null) {
                        navController.navigate("login")
                        return@Button
                    }

                    val input: InputStream? = context.contentResolver.openInputStream(immagine.value!!)
                    val bytes = input?.readBytes()
                    input?.close()
                    val base64Image = if (bytes != null) {
                        "data:image/jpeg;base64," + Base64.encodeToString(bytes, Base64.NO_WRAP)
                    } else null

                    if (base64Image == null) {
                        Toast.makeText(context, "Errore nell'immagine", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    val retrofit = Retrofit.Builder()
                        .baseUrl("https://hermex-api.onrender.com/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()

                    val servizioApi = retrofit.create(ServizioApi::class.java)

                    scope.launch {
                        try {
                            val response = servizioApi.creaServizio(
                                "Bearer $token",
                                CreaServizioRequest(
                                    nome_servizio = titolo.value,
                                    descrizione_servizio = descrizione.value,
                                    prezzo = prezzo.value.toDouble(),
                                    localizzazione = localizzazione.value,
                                    immagine = base64Image
                                )
                            )
                            Toast.makeText(context, response.message, Toast.LENGTH_SHORT).show()
                            navController.navigate("home")
                        } catch (e: Exception) {
                            Toast.makeText(context, "Errore salvataggio", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.padding(end = 16.dp)
            ) {
                Text("Salva", color = Color(0xFF2575FC), fontWeight = FontWeight.Bold)
            }
        }

        Column(modifier = Modifier.padding(16.dp).verticalScroll(rememberScrollState())) {
            Text("Titolo", fontWeight = FontWeight.Bold)
            OutlinedTextField(
                value = titolo.value,
                onValueChange = { titolo.value = it },
                placeholder = { Text("Inserisci il titolo") },
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                isError = showErrors.value && titolo.value.isBlank()
            )

            Text("Immagine", fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 16.dp))
            Row(modifier = Modifier.padding(vertical = 8.dp)) {
                immagine.value?.let {
                    AsyncImage(
                        model = it,
                        contentDescription = null,
                        modifier = Modifier
                            .size(120.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .border(2.dp, Color(0xFF2575FC), RoundedCornerShape(16.dp))
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF2575FC).copy(alpha = 0.2f))
                        .border(2.dp, Color(0xFF2575FC), RoundedCornerShape(16.dp))
                        .clickable { imagePickerLauncher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    Text("+", fontSize = 36.sp, color = Color(0xFF2575FC), fontWeight = FontWeight.Bold)
                }
            }

            Text("Descrizione", fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 16.dp))
            OutlinedTextField(
                value = descrizione.value,
                onValueChange = { descrizione.value = it },
                placeholder = { Text("Descrizione") },
                modifier = Modifier.fillMaxWidth().height(120.dp).padding(vertical = 8.dp),
                shape = RoundedCornerShape(12.dp),
                maxLines = 4,
                isError = showErrors.value && descrizione.value.isBlank()
            )

            Text("Prezzo", fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 16.dp))
            OutlinedTextField(
                value = prezzo.value,
                onValueChange = { prezzo.value = it },
                placeholder = { Text("Es. 10.0") },
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = showErrors.value && prezzo.value.isBlank()
            )

            Text("Localizzazione", fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 16.dp))
            OutlinedTextField(
                value = localizzazione.value,
                onValueChange = { localizzazione.value = it },
                placeholder = { Text("Es. Milano") },
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                shape = RoundedCornerShape(12.dp),
                isError = showErrors.value && localizzazione.value.isBlank()
            )
        }
    }
}

// DATA E API

data class CreaServizioRequest(
    val nome_servizio: String,
    val descrizione_servizio: String,
    val prezzo: Double,
    val localizzazione: String,
    val immagine: String
)

//data class MessageResponse(val message: String)

interface ServizioApi {
    @POST("api/servizi")
    suspend fun creaServizio(
        @Header("Authorization") token: String,
        @Body body: CreaServizioRequest
    ): MessageResponse
}
