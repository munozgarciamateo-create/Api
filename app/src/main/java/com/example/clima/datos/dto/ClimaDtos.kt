package com.example.clima.datos.dto

import com.google.gson.annotations.SerializedName

// Modelos de los datos que llegan desde WeatherAPI.
data class CondicionDto(
    @SerializedName("text") val descripcion: String,
    @SerializedName("icon") val icono: String,
    @SerializedName("code") val codigo: Int
)

data class UbicacionDto(
    @SerializedName("name") val nombre: String,
    @SerializedName("region") val region: String,
    @SerializedName("country") val pais: String,
    @SerializedName("localtime") val horaLocal: String,
    @SerializedName("lat") val latitud: Double,
    @SerializedName("lon") val longitud: Double
)

data class ClimaActualDto(
    @SerializedName("temp_c") val temperatura: Double,
    @SerializedName("feelslike_c") val sensacionTermica: Double,
    @SerializedName("humidity") val humedad: Int,
    @SerializedName("wind_kph") val viento: Double,
    @SerializedName("precip_mm") val precipitacion: Double,
    @SerializedName("uv") val indiceUv: Double,
    @SerializedName("last_updated") val ultimaActualizacion: String,
    @SerializedName("condition") val condicion: CondicionDto
)

data class ResumenDiaDto(
    @SerializedName("maxtemp_c") val maxima: Double,
    @SerializedName("mintemp_c") val minima: Double,
    @SerializedName("daily_chance_of_rain") val probabilidadLluvia: Int,
    @SerializedName("condition") val condicion: CondicionDto
)

data class DiaPronosticoDto(
    @SerializedName("date") val fecha: String,
    @SerializedName("day") val resumen: ResumenDiaDto
)

data class PronosticoDto(
    @SerializedName("forecastday") val dias: List<DiaPronosticoDto>
)

data class RespuestaClimaDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val nombre: String,
    @SerializedName("height") val altura: Int,
    @SerializedName("weight") val peso: Int,
    @SerializedName("sprites") val sprites: SpritesDto
)

class SpritesDto (
    @SerializedName("front_default") val imagen: String?

)
