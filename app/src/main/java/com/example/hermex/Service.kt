package com.example.hermex

data class Service(
    val id_servizio: Int,
    val nome_servizio: String,
    val descrizione_servizio: String,
    val prezzo: Double,
    val immagine_servizio: String,
    val autore: String,
    val immagine_autore: String,
    val rating: Float,
    val id_utente: Int,
    val localizzazione: String
)
