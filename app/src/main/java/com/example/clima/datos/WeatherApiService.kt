package com.example.clima.datos

import com.example.clima.datos.dto.RespuestaClimaDto
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Path
import retrofit2.http.Header


interface WeatherApiService {
    @GET("pokemon/{nombre}")
    suspend fun obtenerPronostico(
        @Header("key") key: String,
        @Path("nombre") ciudad: String,
        @Header("days") dias: Int = 3,
    ): RespuestaClimaDto
}
