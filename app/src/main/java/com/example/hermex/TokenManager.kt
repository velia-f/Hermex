package com.example.hermex

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("auth")

object TokenManager {
    private val TOKEN_KEY = stringPreferencesKey("token")

    fun saveToken(context: Context, token: String) {
        Log.d("TOKEN_SALVATO", token)
        val prefs = context.applicationContext.getSharedPreferences("auth", Context.MODE_PRIVATE)
        prefs.edit().putString("token", token).apply()
    }

    fun getToken(context: Context): String? {
        val prefs = context.applicationContext.getSharedPreferences("auth", Context.MODE_PRIVATE)
        val token = prefs.getString("token", null)
        Log.d("TOKEN_DEBUG", "Token ottenuto: ${token ?: "NULL"}")
        return token
    }

    fun clearToken(context: Context) {
        val prefs = context.applicationContext.getSharedPreferences("auth", Context.MODE_PRIVATE)
        prefs.edit().remove("token").apply()
    }

}
