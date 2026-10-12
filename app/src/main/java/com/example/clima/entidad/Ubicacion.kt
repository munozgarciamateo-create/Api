package com.example.clima.entidad

// Los nombres coinciden con el diagrama y el JSON de WeatherAPI.
data class Ubicacion(
    val name: String,
    val region: String,
    val country: String,
    val localtime: String,
    val lat: Double,
    val lon: Double
)
