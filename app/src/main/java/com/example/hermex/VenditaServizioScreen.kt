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
import com.example.hermex.ui.theme.HermexTheme


@Composable
fun VenditaServizioScreen() {
    val titolo = remember { mutableStateOf("") }
    val descrizione = remember { mutableStateOf("") }
    val prezzo = remember { mutableStateOf("") }
    val localizzazione = remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Barra in alto
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .background(Color(0xFF979D9C))
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { /* TODO: Azione back */ }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_back),
                    contentDescription = stringResource(R.string.aggiungi_una_tua_descrizione),
                    tint = Color.White
                )
            }

            Text(
                text = stringResource(R.string.vendita_servizio),
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                fontSize = 18.sp,
                color = Color(0xFF3D3F42),
                fontWeight = FontWeight.Bold
            )
            Button(
                onClick = { /* TODO: Salva azione */ },
                modifier = Modifier
                    .padding(16.dp)
                    .border(2.dp, Color.White, shape = RoundedCornerShape(8.dp)),  // Bordo bianco
                /*colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),*/
                contentPadding = PaddingValues(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.salva),
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

        }

        Column(
            modifier = Modifier
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(text = stringResource(R.string.titolo), fontWeight = FontWeight.Bold)
            OutlinedTextField(
                value = titolo.value,
                onValueChange = { titolo.value = it },
                placeholder = { Text(stringResource(R.string.inserisci_il_titolo)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                singleLine = true
            )

            Text(
                text = stringResource(R.string.aggiungi_foto),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 16.dp)
            )
            Box(
                modifier = Modifier
                    .size(100.dp)  // Imposta la dimensione del pulsante
                    .background(Color.Gray, shape = RoundedCornerShape(8.dp))  // Imposta il background con angoli arrotondati
                    .border(1.dp, Color.Gray, shape = RoundedCornerShape(8.dp))  // Imposta un bordo grigio
                    .clickable { /* Gestisci il click */ },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "+",  // Il simbolo "+" al centro
                    fontSize = 36.sp,  // Dimensione del simbolo
                    color = Color.White
                )
            }


            Text(
                text = stringResource(R.string.descrizione),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 16.dp)
            )
            OutlinedTextField(
                value = descrizione.value,
                onValueChange = { descrizione.value = it },
                placeholder = { Text(stringResource(R.string.scrivi_una_descrizione)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                singleLine = false,
                maxLines = 4
            )

            Row(modifier = Modifier.padding(top = 8.dp)) {
                Button(
                    onClick = { },
                    modifier = Modifier.padding(end = 8.dp),
                    colors = ButtonDefaults.outlinedButtonColors()
                ) {
                    Text(stringResource(R.string.tag_cucina))
                }
                Button(
                    onClick = { },
                    modifier = Modifier.padding(end = 8.dp),
                    colors = ButtonDefaults.outlinedButtonColors()
                ) {
                    Text(stringResource(R.string.tag_programmazione))
                }
                Button(
                    onClick = { },
                    colors = ButtonDefaults.outlinedButtonColors()
                ) {
                    Text("+", color = Color(0xFF00796B))
                }
            }

            Text(
                text = stringResource(R.string.prezzo_minimo),
                modifier = Modifier.padding(top = 16.dp)
            )
            OutlinedTextField(
                value = prezzo.value,
                onValueChange = { prezzo.value = it },
                placeholder = { Text(stringResource(R.string.example_value)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Text(
                text = stringResource(R.string.localizzazione),
                modifier = Modifier.padding(top = 16.dp)
            )
            OutlinedTextField(
                value = localizzazione.value,
                onValueChange = { localizzazione.value = it },
                placeholder = { Text(stringResource(R.string.es_milano)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun VenditaServizioScreenPreview() {
    VenditaServizioScreen()
}