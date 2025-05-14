package com.example.hermex

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.text.input.KeyboardType

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.hermex.ui.theme.HermexTheme
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext


@Composable
fun VenditaServizioScreen(navController: NavController) {
    val titolo = remember { mutableStateOf("") }
    val descrizione = remember { mutableStateOf("") }
    val prezzo = remember { mutableStateOf("") }
    val localizzazione = remember { mutableStateOf("") }
    val immagini = remember { mutableStateListOf<Uri>() }

    val showErrors = remember { mutableStateOf(false) }
    val titoloError = remember { derivedStateOf { showErrors.value && titolo.value.isBlank() } }
    val descrizioneError = remember { derivedStateOf { showErrors.value && descrizione.value.isBlank() } }
    val prezzoError = remember { derivedStateOf { showErrors.value && prezzo.value.isBlank() } }
    val localizzazioneError = remember { derivedStateOf { showErrors.value && localizzazione.value.isBlank() } }
    val immaginiError = remember { derivedStateOf { showErrors.value && immagini.isEmpty() } }


    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Header
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

                    val isValid = titolo.value.isNotBlank()
                            && descrizione.value.isNotBlank()
                            && prezzo.value.isNotBlank()
                            && localizzazione.value.isNotBlank()
                            && immagini.isNotEmpty()

                    if (!isValid) {
                        Toast.makeText(context, "Compila tutti i campi obbligatori", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Messo in vendita!", Toast.LENGTH_SHORT).show()
                        navController.navigate(Screen.Home.route)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.padding(end = 16.dp)
            ) {
                Text(
                    text = stringResource(R.string.salva),
                    color = Color(0xFF2575FC),
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Column(
            modifier = Modifier
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Titolo
            Text(stringResource(R.string.service_title), fontWeight = FontWeight.Bold)
            OutlinedTextField(
                value = titolo.value,
                onValueChange = { titolo.value = it },
                placeholder = { Text(stringResource(R.string.inserisci_il_titolo)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                isError = titoloError.value
            )
            if (titoloError.value) {
                Text(
                    text = "Il titolo è obbligatorio",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }


            // Box per immagine
            // Launcher per selezione immagini
            val imagePickerLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.GetContent()
            ) { uri: Uri? ->
                uri?.let { immagini.add(it) }
            }

            Text(
                text = stringResource(R.string.aggiungi_foto),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 16.dp)
            )
            // Griglia delle immagini
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                items(immagini.size) { index ->
                    AsyncImage(
                        model = immagini[index],
                        contentDescription = "Immagine selezionata",
                        modifier = Modifier
                            .size(120.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .border(2.dp, Color(0xFF2575FC), RoundedCornerShape(16.dp))
                    )
                }

                // Riquadro "+" alla fine
                item {
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF2575FC).copy(alpha = 0.2f))
                            .border(2.dp, Color(0xFF2575FC), RoundedCornerShape(16.dp))
                            .clickable { imagePickerLauncher.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "+",
                            fontSize = 36.sp,
                            color = Color(0xFF2575FC),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            if (immaginiError.value) {
                Text(
                    text = "Aggiungi almeno un'immagine",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }


            // Descrizione
            Text(stringResource(R.string.descrizione), fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 16.dp))
            OutlinedTextField(
                value = descrizione.value,
                onValueChange = { descrizione.value = it },
                placeholder = { Text(stringResource(R.string.aggiungi_una_tua_descrizione)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(12.dp),
                maxLines = 4,
                isError = descrizioneError.value
            )
            if (descrizioneError.value) {
                Text(
                    text = "La descrizione è obbligatoria",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            // Tag
            Text(stringResource(R.string.categorie), fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(stringResource(R.string.tag_cucina), stringResource(R.string.tag_programmazione), "+").forEach { tag ->
                    OutlinedButton(
                        onClick = { /* TODO */ },
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(1.dp, Color(0xFF2575FC)),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = if (tag == "+") Color(0xFF00796B) else Color(0xFF2575FC)
                        )
                    ) {
                        Text(tag)
                    }
                }
            }

            // Prezzo
            Text(stringResource(R.string.prezzo_minimo), fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 16.dp))
            OutlinedTextField(
                value = prezzo.value,
                onValueChange = { prezzo.value = it },
                placeholder = { Text(stringResource(R.string.example_value_price)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = prezzoError.value
            )
            if (prezzoError.value) {
                Text(
                    text = "Inserisci un prezzo",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            // Localizzazione
            Text(stringResource(R.string.localizzazione), fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 16.dp))
            OutlinedTextField(
                value = localizzazione.value,
                onValueChange = { localizzazione.value = it },
                placeholder = { Text(stringResource(R.string.es_milano)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(12.dp),
                isError = localizzazioneError.value
            )
            if (localizzazioneError.value) {
                Text(
                    text = "Inserisci una localizzazione",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun VenditaServizioScreenPreview() {
    VenditaServizioScreen(navController = rememberNavController())
}