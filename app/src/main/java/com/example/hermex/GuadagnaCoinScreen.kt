package com.example.hermex

import android.content.Intent
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.text.input.KeyboardType

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector
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
import com.example.hermex.ui.theme.HermexTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@Composable
fun GuadagnaCoinScreen(navController: NavController) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val openDialog = remember { mutableStateOf(false) }
    val dialogTitle = remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Barra in alto
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
                    text = "10",
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        // Contenuto scrollabile
        Column(
            modifier = Modifier
                .padding(16.dp)
                .verticalScroll(scrollState)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Sezione Cards
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

            Spacer(modifier = Modifier.height(16.dp))
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


@Preview(showBackground = true)
@Composable
fun GuadagnaCoinScreenPreview() {
    GuadagnaCoinScreen(navController = rememberNavController())
}