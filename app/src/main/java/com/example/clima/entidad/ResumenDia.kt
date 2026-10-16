package com.example.clima.entidad

// Los nombres coinciden con el diagrama y el JSON de WeatherAPI.
data class ResumenDia(
    val maxtemp_c: Double,
    val mintemp_c: Double,
    val daily_chance_of_rain: Int,
    val condition: Condicion
)
