package com.example.clima.entidad

// Los nombres coinciden con el diagrama y el JSON de WeatherAPI.
data class RespuestaClima(
    val location: Ubicacion,
    val current: ClimaActual,
    val forecast: Pronostico
)
