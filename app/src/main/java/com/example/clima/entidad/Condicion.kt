package com.example.clima.entidad

// Los nombres coinciden con el diagrama y el JSON de WeatherAPI.
data class Condicion(
    val text: String,
    val icon: String,
    val code: Int
)
