package com.example.clima.datos.dto

import com.google.gson.annotations.SerializedName

data class RecursoNombreDto(
    @SerializedName("name") val nombre: String
)

data class TipoSlotDto(
    @SerializedName("slot") val posicion: Int,
    @SerializedName("type") val tipo: RecursoNombreDto
)

data class EstadisticaSlotDto(
    @SerializedName("base_stat") val valorBase: Int,
    @SerializedName("stat") val estadistica: RecursoNombreDto
)

data class SpritesDto(
    @SerializedName("front_default") val frente: String?
)

data class PokemonDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val nombre: String,
    @SerializedName("height") val altura: Int,
    @SerializedName("weight") val peso: Int,
    @SerializedName("sprites") val sprites: SpritesDto,
    @SerializedName("types") val tipos: List<TipoSlotDto>,
    @SerializedName("stats") val estadisticas: List<EstadisticaSlotDto>
)