package com.example.hermex

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.material.icons.filled.StarHalf
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@Composable
fun ContactButton(email: String) {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }

    OutlinedButton(
        onClick = { showDialog = true },
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = Color(0xFF2575FC)
        ),
        border = BorderStroke(1.dp, Color(0xFF2575FC))
    ) {
        Text(text = stringResource(R.string.contact))
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Chiudi")
                }
            },
            title = { Text("Contatta la persona") },
            text = {
                val annotatedString = buildAnnotatedString {
                    append("Puoi contattare la persona via email: ")
                    pushStringAnnotation(
                        tag = "EMAIL",
                        annotation = "mailto:$email"
                    )
                    withStyle(style = SpanStyle(color = Color(0xFF2575FC), textDecoration = TextDecoration.Underline)) {
                        append(email)
                    }
                    pop()
                }

                ClickableText(
                    text = annotatedString,
                    onClick = { offset ->
                        annotatedString.getStringAnnotations(tag = "EMAIL", start = offset, end = offset)
                            .firstOrNull()?.let { annotation ->
                                val intent = Intent(Intent.ACTION_SENDTO).apply {
                                    data = Uri.parse(annotation.item)
                                }
                                try {
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Nessuna app per email trovata.", Toast.LENGTH_SHORT).show()
                                }
                            }
                    }
                )
            }
        )
    }
}


@Composable
fun ServiceDetailScreen(serviceId: Int, navController: NavController) {
    val scrollState = rememberScrollState()
    val service = FakeServiceRepository.getById(serviceId)
    if (service == null) {
        Text("Servizio non trovato")
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // Image
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    brush = Brush.linearGradient(
                        listOf(Color(0xFF6A11CB), Color(0xFF2575FC))
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.image),
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            //text = stringResource(R.string.service_title),
            text = service.title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            //text = stringResource(R.string.lorem_ipsum),
            text = service.description,
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
                //stringResource(R.string.author_name),
                text = "${service.author}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                val fullStars = service.rating.toInt()
                val hasHalfStar = (service.rating - fullStars) >= 0.5

                repeat(fullStars) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Stella piena",
                        tint = Color(0xFFFFC107)
                    )
                }

                if (hasHalfStar) {
                    Icon(
                        imageVector = Icons.Default.StarHalf,
                        contentDescription = "Mezza stella",
                        tint = Color(0xFFFFC107)
                    )
                }

                val remainingStars = 5 - fullStars - if (hasHalfStar) 1 else 0
                repeat(remainingStars) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Stella vuota",
                        tint = Color.LightGray
                    )
                }

                Text(
                    text = "${service.rating}",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 4.dp),
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }

        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = { /* TODO: Compra */ },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2575FC),
                    contentColor = Color.White
                )
            ) {
                Text(text = stringResource(R.string.buy))
            }

            ContactButton(email = "info@hermex.com")
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            stringResource(R.string.similar),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            repeat(4) {
                val serviceId = it + 1
                val currentService = FakeServiceRepository.getById(serviceId)

                if (currentService != null) {
                    OutlinedButton(
                        modifier = Modifier
                            .size(100.dp)
                            .shadow(4.dp, RoundedCornerShape(12.dp))
                            .background(Color.White, RoundedCornerShape(12.dp)),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFF2575FC)
                        ),
                        border = BorderStroke(1.dp, Color(0xFF2575FC)),
                        onClick = {
                            navController.navigate(Screen.ServiceDetail.createRoute(serviceId))
                        }
                    ) {
                        Text(
                            text = currentService.title,
                            color = Color(0xFF2575FC),
                            style = MaterialTheme.typography.titleSmall
                        )
                    }
                }
            }

            /*repeat(4) {
                OutlinedButton(
                    modifier = Modifier
                        .size(100.dp)
                        .shadow(4.dp, RoundedCornerShape(12.dp))
                        .background(Color.White, RoundedCornerShape(12.dp))
                        .clickable { /* TODO */ },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFF2575FC)
                    ),
                    border = BorderStroke(1.dp, Color(0xFF2575FC)),
                    onClick = {
                        //navController.navigate(Screen.ServiceDetail.route)//da modificare tale per cui vada nella pagina con idpost giusto
                        // TODO: migliorare logica (sotto)
                        navController.navigate(Screen.ServiceDetail.createRoute(serviceId = it + 1))
                    },
                ) {
                    Text(
                        text = FakeServiceRepository.getById(serviceId)?.title ?: "null",
                        color = Color(0xFF2575FC),
                        style = MaterialTheme.typography.titleSmall
                    )
                }
            }*/
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ServiceDetailPreview() {
    ServiceDetailScreen(serviceId = 1, navController = rememberNavController())
}
