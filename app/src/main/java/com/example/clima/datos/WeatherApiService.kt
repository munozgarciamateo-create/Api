package com.example.clima.datos

import com.example.clima.entidad.RespuestaClima
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {
    @GET("forecast.json")
    suspend fun obtenerPronostico(
        @Query("key") key: String,
        @Query("q") ciudad: String,
        @Query("days") dias: Int = 3,
        @Query("lang") idioma: String = "es",
        @Query("aqi") calidadAire: String = "no",
        @Query("alerts") alertas: String = "no"
    ): RespuestaClima
}
