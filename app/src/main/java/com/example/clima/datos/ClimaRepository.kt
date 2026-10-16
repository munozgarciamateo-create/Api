package com.example.clima.datos

import com.example.clima.entidad.RespuestaClima
import com.example.clima.datos.mapper.aEntidad
import com.google.gson.JsonParser
import com.google.gson.JsonParseException
import kotlinx.coroutines.CancellationException
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException

class ClimaException(message: String) : Exception(message)

class ClimaRepository(private val api: WeatherApiService, private val key: String) {
    suspend fun obtenerClima(ciudad: String): RespuestaClima {
        val consulta = ciudad.trim()
        if (consulta.isEmpty()) throw ClimaException("Escribe una ciudad para buscar.")
        if (key.isBlank()) throw ClimaException(
            "Falta la clave de WeatherAPI. Configura apikey.properties y vuelve a ejecutar la app."
        )
        try {
            return api.obtenerPronostico(key, consulta, 3).aEntidad()
        } catch (e: CancellationException) {
            throw e
        } catch (e: HttpException) {
            val code = runCatching {
                JsonParser.parseString(e.response()?.errorBody()?.string().orEmpty())
                    .asJsonObject.getAsJsonObject("error").get("code").asInt
            }.getOrNull()
            throw ClimaException(when (code) {
                1006 -> "No encontramos esa ciudad. Prueba escribiendo ciudad y país."
                1002, 2006 -> "La clave de WeatherAPI no es válida. Revisa apikey.properties."
                2007 -> "Se alcanzó el límite de consultas de tu cuenta WeatherAPI."
                2008 -> "La clave de WeatherAPI está deshabilitada."
                2009 -> "Tu plan de WeatherAPI no permite esta consulta."
                else -> "El servicio no pudo responder (HTTP ${e.code()}). Intenta nuevamente."
            })
        } catch (e: SocketTimeoutException) {
            throw ClimaException("La consulta tardó demasiado. Intenta nuevamente.")
        } catch (e: IOException) {
            throw ClimaException("No se pudo conectar. Comprueba tu conexión a Internet.")
        } catch (e: JsonParseException) {
            throw ClimaException("El servicio devolvió datos que no se pudieron interpretar.")
        }
    }
}
