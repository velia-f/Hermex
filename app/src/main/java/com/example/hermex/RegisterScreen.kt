package com.example.hermex

import android.app.DatePickerDialog
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import java.util.*

@Composable
fun RegisterScreen(navController: NavController) {
    val context = LocalContext.current

    var profilePictureUri by remember { mutableStateOf<Uri?>(null) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        profilePictureUri = uri
    }

    var username by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var cf by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val calendar = Calendar.getInstance()
    val datePickerDialog = remember {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                birthDate = "%04d-%02d-%02d".format(year, month + 1, dayOfMonth)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    fun isValidCF(cf: String): Boolean {
        val regex = Regex("^[A-Z0-9]{16}$", RegexOption.IGNORE_CASE)
        return regex.matches(cf)
    }

    fun isValidBirthDate(dateStr: String): Boolean {
        val parts = dateStr.split("-")
        if (parts.size != 3) return false
        val year = parts[0].toIntOrNull() ?: return false
        val month = parts[1].toIntOrNull() ?: return false
        val day = parts[2].toIntOrNull() ?: return false

        val calendar = Calendar.getInstance()
        calendar.set(year, month - 1, day)
        val birthDate = calendar.time

        val now = Calendar.getInstance().time
        if (birthDate.after(now)) return false

        val ageCalendar = Calendar.getInstance()
        ageCalendar.time = birthDate
        val today = Calendar.getInstance()

        var age = today.get(Calendar.YEAR) - ageCalendar.get(Calendar.YEAR)
        if (today.get(Calendar.DAY_OF_YEAR) < ageCalendar.get(Calendar.DAY_OF_YEAR)) {
            age--
        }
        return age >= 14
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Crea il tuo account",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(vertical = 32.dp)
        )

        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.LightGray)
                .align(Alignment.CenterHorizontally)
                .clickable { launcher.launch("image/*") },
            contentAlignment = Alignment.Center
        ) {
            if (profilePictureUri != null) {
                Image(
                    painter = rememberAsyncImagePainter(profilePictureUri),
                    contentDescription = "Immagine profilo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Text("Aggiungi", style = MaterialTheme.typography.titleMedium)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        RegisterTextField(value = username, onValueChange = { username = it }, label = "Username")
        RegisterTextField(value = firstName, onValueChange = { firstName = it }, label = "Nome")
        RegisterTextField(value = lastName, onValueChange = { lastName = it }, label = "Cognome")
        RegisterTextField(value = email, onValueChange = { email = it }, label = "Email")
        RegisterTextField(value = cf, onValueChange = { cf = it.uppercase() }, label = "Codice Fiscale")

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .clickable { datePickerDialog.show() }
        ) {
            OutlinedTextField(
                value = birthDate,
                onValueChange = {},
                readOnly = true,
                label = { Text("Data di nascita") },
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                enabled = false,
                modifier = Modifier.fillMaxWidth()
            )
        }

        RegisterTextField(value = password, onValueChange = { password = it }, label = "Password", isPassword = true)

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                when {
                    username.isBlank() || firstName.isBlank() || lastName.isBlank() || email.isBlank() || cf.isBlank() || birthDate.isBlank() || password.isBlank() -> {
                        Toast.makeText(context, "Completa tutti i campi", Toast.LENGTH_SHORT).show()
                    }
                    !isValidCF(cf) -> {
                        Toast.makeText(context, "Codice Fiscale non valido", Toast.LENGTH_SHORT).show()
                    }
                    !isValidBirthDate(birthDate) -> {
                        Toast.makeText(context, "Data di nascita non valida o minore di 14 anni", Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        Thread {
                            var imageBase64 = ""
                            try {
                                profilePictureUri?.let { uri ->
                                    context.contentResolver.openInputStream(uri)?.use { stream ->
                                        val bytes = stream.readBytes()
                                        imageBase64 = android.util.Base64.encodeToString(bytes, android.util.Base64.NO_WRAP)
                                        println("DEBUG: immagine letta, size=${bytes.size} bytes, uri=$uri")
                                    }
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                                Handler(Looper.getMainLooper()).post {
                                    Toast.makeText(context, "Errore nel caricamento immagine", Toast.LENGTH_LONG).show()
                                }
                                return@Thread
                            }

                            val json = """
                            {
                              "nome": "$firstName",
                              "cognome": "$lastName",
                              "email": "$email",
                              "cf": "$cf",
                              "data_nascita": "$birthDate",
                              "username": "$username",
                              "password": "$password",
                              "immagine": "$imageBase64"
                            }
                            """.trimIndent()

                            val client = OkHttpClient()
                            val mediaType = "application/json".toMediaTypeOrNull()
                            val body = RequestBody.create(mediaType, json)
                            val request = Request.Builder()
                                .url("http://10.0.2.2:3000/register")
                                .post(body)
                                .build()

                            try {
                                val response = client.newCall(request).execute()
                                val responseBody = response.body?.string()
                                Handler(Looper.getMainLooper()).post {

                                    if (response.isSuccessful) {
                                        Toast.makeText(context, "Registrazione completata!", Toast.LENGTH_SHORT).show()
                                        navController.navigate("login") {
                                            popUpTo("register") { inclusive = true } // pulizia dello stack
                                        }
                                    } else {
                                        Toast.makeText(context, "Errore: $responseBody", Toast.LENGTH_LONG).show()
                                    }
                                }
                            } catch (e: Exception) {
                                Handler(Looper.getMainLooper()).post {
                                    Toast.makeText(context, "Errore di connessione", Toast.LENGTH_LONG).show()
                                }
                            }
                        }.start()
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(text = "Registrati")
        }
    }
}

@Composable
fun RegisterTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isPassword: Boolean = false
) {
    var passwordVisible by remember { mutableStateOf(false) }
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
        trailingIcon = {
            if (isPassword) {
                val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                Icon(
                    imageVector = image,
                    contentDescription = if (passwordVisible) "Nascondi password" else "Mostra password",
                    modifier = Modifier.clickable { passwordVisible = !passwordVisible }
                )
            }
        },
        modifier = Modifier.fillMaxWidth()
    )
}
