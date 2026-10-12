package com.example.clima.entidad

// Los nombres coinciden con el diagrama y el JSON de WeatherAPI.
data class DiaPronostico(
    val date: String,
    val day: ResumenDia
)
