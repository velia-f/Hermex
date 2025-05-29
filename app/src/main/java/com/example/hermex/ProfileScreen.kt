package com.example.hermex

import android.content.Context
import com.example.hermex.TokenManager.getToken
import com.example.hermex.TokenManager.clearToken
import android.content.SharedPreferences
import android.net.Uri
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PUT
import java.io.InputStream


// TokenManager locale
fun saveToken(context: Context, token: String) {
    val prefs: SharedPreferences = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
    prefs.edit().putString("token", token).apply()
}

suspend fun getToken(context: Context): String? {
    return context.getSharedPreferences("auth", Context.MODE_PRIVATE).getString("token", null)
}

fun clearToken(context: Context) {
    val prefs: SharedPreferences = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
    prefs.edit().remove("token").apply()
}

// UI
@Composable
fun ProfileScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    var username by remember { mutableStateOf(TextFieldValue("")) }
    var email by remember { mutableStateOf(TextFieldValue("")) }
    var imageUrl by remember { mutableStateOf("") }
    val selectedImageUri = remember { mutableStateOf<Uri?>(null) }
    var isEditable by remember { mutableStateOf(false) }

    val imagePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        selectedImageUri.value = uri
    }

    LaunchedEffect(true) {
        val token = getToken(context)
        Log.d("TOKEN_DEBUG", "Token ottenuto: ${token ?: "NULL"}")

        if (token == null) {
            navController.navigate("login")
            return@LaunchedEffect
        }

        try {
            val user = provideUserApi().getUserData("Bearer $token")
            username = TextFieldValue(user.username)
            email = TextFieldValue(user.email)
            imageUrl = user.profileImageUrl
        } catch (e: Exception) {
            Toast.makeText(context, "Errore nel caricamento", Toast.LENGTH_SHORT).show()
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Profilo", fontSize = 24.sp)
            Text(
                if (isEditable) "Annulla" else "Modifica",
                color = Color(0xFF1E88E5),
                modifier = Modifier.clickable { isEditable = !isEditable }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Box(
            modifier = Modifier
                .size(140.dp)
                .align(Alignment.CenterHorizontally)
                .border(2.dp, Color.Gray, RoundedCornerShape(100.dp))
                .clickable(enabled = isEditable) { imagePickerLauncher.launch("image/*") },
            contentAlignment = Alignment.Center
        ) {
            val imageToShow = selectedImageUri.value ?: imageUrl.takeIf { it.isNotEmpty() }
            if (imageToShow != null) {
                AsyncImage(
                    model = imageToShow,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(140.dp)
                        .clip(RoundedCornerShape(100.dp))
                )
            } else {
                Text("👤", fontSize = 48.sp)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            enabled = isEditable,
            placeholder = { Text("Username") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            enabled = isEditable,
            placeholder = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        if (isEditable) {
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    scope.launch {
                        val token = getToken(context.applicationContext)
                        var imageBase64: String? = null

                        selectedImageUri.value?.let { uri ->
                            val input: InputStream? = context.contentResolver.openInputStream(uri)
                            val bytes = input?.readBytes()
                            input?.close()
                            if (bytes != null) {
                                imageBase64 = "data:image/jpeg;base64," + Base64.encodeToString(bytes, Base64.NO_WRAP)
                            }
                        }

                        try {
                            val response = provideUserApi().updateProfile(
                                "Bearer $token",
                                UpdateProfileRequest(username.text, email.text, imageBase64)
                            )
                            Toast.makeText(context, response.message, Toast.LENGTH_SHORT).show()
                            isEditable = false
                        } catch (e: Exception) {
                            Toast.makeText(context, "Errore aggiornamento", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E88E5))
            ) {
                Text("Salva", color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                scope.launch {
                    clearToken(context)
                    navController.navigate("login")
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
        ) {
            Text("Esci", color = Color.Black)
        }

        Spacer(modifier = Modifier.height(32.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .background(Color(0xFFE0E0E0), shape = RoundedCornerShape(12.dp))
                .padding(16.dp)
        ) {
            Text("📦 Spazio riservato ai tuoi servizi")
        }
    }
}
