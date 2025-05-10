package com.example.hermex

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@Composable
fun GuadagnaCoinScreen(navController: NavController) {
    val scrollState = rememberScrollState()
    /*Scaffold(
        bottomBar = { BottomNavBar(navController = navController) }
    ) { paddingValues ->*/
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {
            // Top bar non va
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.guadagna_coin),
                    style = MaterialTheme.typography.titleLarge
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.MonetizationOn,
                        contentDescription = null,
                        tint = Color.Black
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "10")
                }
            }

            // Card: Guarda Ads
            ActionCard(
                icon = Icons.Default.PlayArrow,
                title = stringResource(R.string.guarda_ads),
                subtitle = stringResource(R.string.guadagna_coin),
                onClick = {
                    // TODO: Guarda un video pubblicitario
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Card: Acquista Coin
            ActionCard(
                icon = Icons.Default.ShoppingCart,
                title = stringResource(R.string.acquista_coin),
                subtitle = stringResource(R.string._1_coin_per_0_5_3_coin_per_1),
                onClick = {
                    // TODO: Apri schermata di acquisto
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Card: Riscatta Certificato
            ActionCard(
                icon = Icons.Default.Receipt,
                title = stringResource(R.string.riscatta_il_tuo_certificato),
                subtitle = stringResource(R.string.pagamento),
                onClick = {
                    // TODO: Riscatta certificato
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Card: Inserisci Annuncio
            ActionCard(
                icon = Icons.Default.Add,
                title = stringResource(R.string.inserisci_annuncio_vendita_servizio),
                subtitle = null,
                onClick = {
                    navController.navigate(Screen.Add.route)
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Referral Code
            ActionCard(
                icon = Icons.Default.Share,
                title = stringResource(R.string.referral_code),
                subtitle = stringResource(R.string.condividendo_il_tuo_codice_si_guadagnano_dei_coin),
                onClick = {
                    // TODO: Condividi referral
                }
            )
        }
  //  }
}

@Composable
fun ActionCard(
    icon: ImageVector?,
    title: String,
    subtitle: String?,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, Color.Black)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(38.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium
                )
                if (!subtitle.isNullOrEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall
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