package com.example.clima.datos.mapper

import com.example.clima.datos.dto.*
import com.example.clima.entidad.*
// Cada mapper transforma un DTO recibido de la API en una entidad de la app.
fun CondicionDto.aEntidad(): Condicion = Condicion(
    text = descripcion,
    icon = icono,
    code = codigo
)

fun UbicacionDto.aEntidad(): Ubicacion = Ubicacion(
    name = nombre,
    region = region,
    country = pais,
    localtime = horaLocal,
    lat = latitud,
    lon = longitud
)

fun ClimaActualDto.aEntidad(): ClimaActual = ClimaActual(
    temp_c = temperatura,
    feelslike_c = sensacionTermica,
    humidity = humedad,
    wind_kph = viento,
    precip_mm = precipitacion,
    uv = indiceUv,
    last_updated = ultimaActualizacion,
    condition = condicion.aEntidad()
)

fun ResumenDiaDto.aEntidad(): ResumenDia = ResumenDia(
    maxtemp_c = maxima,
    mintemp_c = minima,
    daily_chance_of_rain = probabilidadLluvia,
    condition = condicion.aEntidad()
)

fun DiaPronosticoDto.aEntidad(): DiaPronostico = DiaPronostico(
    date = fecha,
    day = resumen.aEntidad()
)

fun PronosticoDto.aEntidad(): Pronostico = Pronostico(
    forecastday = dias.map { it.aEntidad() }
)

fun RespuestaClimaDto.aEntidad(): RespuestaClima = RespuestaClima(
    location = Ubicacion(
        name = nombre,
        region = "Pokémon #$id",
        country = "",
        localtime = "",
        lat = altura / 10.0,
        lon = peso / 10.0
    ),
    current = ClimaActual(
        temp_c = altura / 10.0,
        feelslike_c = peso / 10.0,
        humidity = 0,
        wind_kph = 0.0,
        precip_mm = 0.0,
        uv = 0.0,
        last_updated = "",
        condition = Condicion(
            text = nombre,
            icon = sprites.imagen.orEmpty(),
            code = id
        )
    ),
    forecast = Pronostico(forecastday = emptyList())
)
