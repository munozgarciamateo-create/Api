package com.example.clima.datos

import com.example.clima.datos.mapper.aRespuestaClima
import com.example.clima.entidad.RespuestaClima
import kotlinx.coroutines.CancellationException
import retrofit2.HttpException
import java.io.IOException

class PokemonRepository(
    private val api: PokemonApiService = PokemonApiClient.service
) {
    suspend fun obtenerPokemon(nombre: String): RespuestaClima {
        try {
            return api.getPokemon(nombre.trim().lowercase()).aRespuestaClima()
        } catch (e: CancellationException) {
            throw e
        } catch (e: HttpException) {
            if (e.code() == 404) throw ClimaException("Pokémon no encontrado. Prueba con un nombre en inglés, por ejemplo: pikachu.")
            throw ClimaException("PokeAPI respondió con un error (${e.code()}).")
        } catch (e: IOException) {
            throw ClimaException("Sin conexión a internet.")
        }
    }
}