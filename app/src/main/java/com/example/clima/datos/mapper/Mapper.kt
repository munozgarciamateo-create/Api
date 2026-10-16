package com.example.clima.datos.mapper

import com.example.clima.datos.dto.*
import com.example.clima.entidad.*

// ============================================================
// MAPPER ÚNICO
// Cada API aporta una función  XxxDto.aRespuestaClima()
// que convierte su respuesta a RespuestaClima, la entidad
// que entiende la interfaz. Para una API nueva, solo se agrega
// una función al final de este archivo.
// ============================================================


// ---------- WeatherAPI ----------

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

// Nombre común para todas las APIs
fun RespuestaClimaDto.aRespuestaClima(): RespuestaClima = aEntidad()


// ---------- PokeAPI ----------

fun PokemonDto.aRespuestaClima(): RespuestaClima {
    fun stat(nombre: String) =
        estadisticas.firstOrNull { it.estadistica.nombre == nombre }?.valorBase ?: 0

    val tiposTexto = tipos.sortedBy { it.posicion }.joinToString(", ") { it.tipo.nombre }

    val condicion = Condicion(
        text = tiposTexto,
        icon = sprites.frente?.removePrefix("https:") ?: "",
        code = id
    )

    return RespuestaClima(
        location = Ubicacion(
            name = nombre.replaceFirstChar { it.uppercase() },
            region = tiposTexto,
            country = "Pokémon",
            localtime = "#$id",
            lat = 0.0,
            lon = 0.0
        ),
        current = ClimaActual(
            temp_c = altura / 10.0,        // altura en metros
            feelslike_c = peso / 10.0,     // peso en kg
            humidity = stat("hp"),
            wind_kph = stat("speed").toDouble(),
            precip_mm = 0.0,
            uv = 0.0,
            last_updated = "",
            condition = condicion
        ),
        forecast = Pronostico(
            forecastday = estadisticas.map {
                DiaPronostico(
                    date = it.estadistica.nombre,
                    day = ResumenDia(
                        maxtemp_c = it.valorBase.toDouble(),
                        mintemp_c = 0.0,
                        daily_chance_of_rain = it.valorBase.coerceAtMost(100),
                        condition = condicion
                    )
                )
            }
        )
    )
}


// ---------- Próximas APIs ----------
// fun NuevaApiDto.aRespuestaClima(): RespuestaClima { ... }
