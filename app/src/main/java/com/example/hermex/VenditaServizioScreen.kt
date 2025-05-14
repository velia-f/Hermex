package com.example.hermex

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.text.input.KeyboardType

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
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
import com.example.hermex.ui.theme.HermexTheme


@Composable
fun VenditaServizioScreen(navController: NavController) {
    val titolo = remember { mutableStateOf("") }
    val descrizione = remember { mutableStateOf("") }
    val prezzo = remember { mutableStateOf("") }
    val localizzazione = remember { mutableStateOf("") }

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
                onClick = { /* TODO: Salva */ },
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
            Text("Titolo", fontWeight = FontWeight.Bold)
            OutlinedTextField(
                value = titolo.value,
                onValueChange = { titolo.value = it },
                placeholder = { Text("Inserisci il titolo") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            // Box per immagine
            Text("Aggiungi una foto", fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 16.dp))
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF2575FC).copy(alpha = 0.2f))
                    .border(2.dp, Color(0xFF2575FC), RoundedCornerShape(16.dp))
                    .clickable { /* TODO: Aggiungi foto */ },
                contentAlignment = Alignment.Center
            ) {
                Text("+", fontSize = 36.sp, color = Color(0xFF2575FC), fontWeight = FontWeight.Bold)
            }

            // Descrizione
            Text("Descrizione", fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 16.dp))
            OutlinedTextField(
                value = descrizione.value,
                onValueChange = { descrizione.value = it },
                placeholder = { Text("Scrivi una descrizione") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(12.dp),
                maxLines = 4
            )

            // Tag
            Text("Categorie", fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Cucina", "Programmazione", "+").forEach { tag ->
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
            Text("Prezzo minimo", fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 16.dp))
            OutlinedTextField(
                value = prezzo.value,
                onValueChange = { prezzo.value = it },
                placeholder = { Text("€15.00") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            // Localizzazione
            Text("Localizzazione", fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 16.dp))
            OutlinedTextField(
                value = localizzazione.value,
                onValueChange = { localizzazione.value = it },
                placeholder = { Text("Es. Milano") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(12.dp)
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun VenditaServizioScreenPreview() {
    VenditaServizioScreen(navController = rememberNavController())
}