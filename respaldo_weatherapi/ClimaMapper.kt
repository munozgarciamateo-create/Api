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
    location = ubicacion.aEntidad(),
    current = actual.aEntidad(),
    forecast = pronostico.aEntidad()
)
