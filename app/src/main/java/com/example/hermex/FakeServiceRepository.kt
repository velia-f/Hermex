package com.example.hermex

object FakeServiceRepository {
    val services = listOf(
        Service(1, "Logo Design", "Design professionale di loghi", "Anna Verdi", 4.5),
        Service(2, "Sviluppo App", "Creazione di app Android", "Luca Bianchi", 4.2),
        Service(3, "SEO Marketing", "Ottimizzazione SEO avanzata", "Giulia Neri", 3.9),
        Service(4, "Contenuti AI", "Generazione contenuti con AI", "Marco Gialli", 4.8),
        Service(5, "Copywriting", "Testi accattivanti per siti", "Sara Rosa", 4.0)
    )

    fun getById(id: Int): Service? = services.find { it.id == id }
}