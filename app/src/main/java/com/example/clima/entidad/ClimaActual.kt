package com.example.clima.entidad

// Los nombres coinciden con el diagrama y el JSON de WeatherAPI.
data class ClimaActual(
    val temp_c: Double,
    val feelslike_c: Double,
    val humidity: Int,
    val wind_kph: Double,
    val precip_mm: Double,
    val uv: Double,
    val last_updated: String,
    val condition: Condicion
)
