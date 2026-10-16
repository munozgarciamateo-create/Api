package com.example.clima.datos

import com.example.clima.datos.mapper.aRespuestaClima
import com.example.clima.entidad.RespuestaClima
import com.google.gson.JsonParseException
import kotlinx.coroutines.CancellationException
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException

class PokemonRepository(private val api: PokemonApiService = PokemonApiClient.api) {
    suspend fun obtenerPokemon(nombre: String): RespuestaClima {
        val consulta = nombre.trim().lowercase()
        if (consulta.isEmpty()) throw ClimaException("Escribe el nombre de un Pokémon para buscar.")
        try {
            return api.obtenerPokemon(consulta).aRespuestaClima()
        } catch (e: CancellationException) {
            throw e
        } catch (e: HttpException) {
            throw ClimaException(when (e.code()) {
                404 -> "No encontramos ese Pokémon. Prueba con el nombre en inglés, por ejemplo: pikachu."
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