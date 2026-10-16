package com.example.clima.datos

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object PokemonApiClient {
    val service: PokemonApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://pokeapi.co/api/v2/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PokemonApiService::class.java)
    }
}