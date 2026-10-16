package com.example.clima.entidad

// Los nombres coinciden con el diagrama y el JSON de WeatherAPI.
data class Pronostico(
    val forecastday: List<DiaPronostico>
)
