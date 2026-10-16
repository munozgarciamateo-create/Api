package com.example.clima.datos

import com.example.clima.datos.dto.PokemonDto
import retrofit2.http.GET
import retrofit2.http.Path

interface PokemonApiService {
    @GET("pokemon/{name}")
    suspend fun obtenerPokemon(@Path("name") name: String): PokemonDto
}