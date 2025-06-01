package com.example.hermex

import android.content.Intent
import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.hermex.TokenManager.getToken
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header

// API INTERFACE
interface SaldoApi {
    @GET("api/user/saldo")
    suspend fun getSaldo(@Header("Authorization") token: String): SaldoResponse
}

data class SaldoResponse(val saldo: Double)

@Composable
fun GuadagnaCoinScreen(navController: NavController) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val openDialog = remember { mutableStateOf(false) }
    val dialogTitle = remember { mutableStateOf("") }

    var saldo by remember { mutableStateOf(0.0) }

    // Recupero saldo dal backend
    LaunchedEffect(Unit) {
        try {
            val token = getToken(context)
            if (token != null) {
                val retrofit = Retrofit.Builder()
                    .baseUrl("http://10.0.2.2:3000/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                val api = retrofit.create(SaldoApi::class.java)
                val response = api.getSaldo("Bearer $token")
                saldo = response.saldo
                Log.d("SALDO_DEBUG", "Saldo attuale: $saldo")
            }
        } catch (e: Exception) {
            Log.e("SALDO_ERROR", "Errore nel recupero saldo", e)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // TOP BAR con saldo
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(Color(0xFF979D9C))
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.guadagna_coin),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.MonetizationOn,
                    contentDescription = null,
                    tint = Color.Yellow
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "%.2f".format(saldo),
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        // CONTENUTO
        Column(
            modifier = Modifier
                .padding(16.dp)
                .verticalScroll(scrollState)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            ActionCardStyled(
                icon = Icons.Default.PlayArrow,
                title = stringResource(R.string.guarda_ads),
                subtitle = stringResource(R.string.guadagna_coin),
                onClick = {
                    dialogTitle.value = "Guarda Ads"
                    openDialog.value = true
                }
            )

            ActionCardStyled(
                icon = Icons.Default.ShoppingCart,
                title = stringResource(R.string.acquista_coin),
                subtitle = stringResource(R.string._1_coin_per_0_5_3_coin_per_1),
                onClick = {
                    dialogTitle.value = "Acquista Coin"
                    openDialog.value = true
                }
            )

            ActionCardStyled(
                icon = Icons.Default.Receipt,
                title = stringResource(R.string.riscatta_il_tuo_certificato),
                subtitle = stringResource(R.string.pagamento),
                onClick = {
                    dialogTitle.value = "Riscatta certificato"
                    openDialog.value = true
                }
            )

            ActionCardStyled(
                icon = Icons.Default.Add,
                title = stringResource(R.string.inserisci_annuncio_vendita_servizio),
                subtitle = null,
                onClick = {
                    navController.navigate(Screen.Add.route)
                }
            )

            ActionCardStyled(
                icon = Icons.Default.Share,
                title = stringResource(R.string.referral_code),
                subtitle = stringResource(R.string.condividendo_il_tuo_codice_si_guadagnano_dei_coin),
                onClick = {
                    val sendIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, "Usa il mio codice referral: ABC123 per guadagnare coin!")
                        type = "text/plain"
                    }
                    val shareIntent = Intent.createChooser(sendIntent, null)
                    context.startActivity(shareIntent)
                }
            )
        }

        if (openDialog.value) {
            AlertDialog(
                onDismissRequest = { openDialog.value = false },
                confirmButton = {
                    TextButton(onClick = { openDialog.value = false }) {
                        Text("Grazie :)")
                    }
                },
                title = {
                    Text(dialogTitle.value)
                },
                text = {
                    Text("Siamo lavorando per Te e per darti il meglio\nAncora un attimo ;)")
                }
            )
        }
    }
}

@Composable
fun ActionCardStyled(
    icon: ImageVector?,
    title: String,
    subtitle: String?,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F7F7))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color(0xFF2575FC), shape = RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
            }
            Column {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                if (!subtitle.isNullOrEmpty()) {
                    Text(
                        text = subtitle,
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}
