package com.example.clima.presentacion

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clima.BuildConfig
import com.example.clima.datos.*
import com.example.clima.entidad.RespuestaClima
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ClimaUiState(
    val ciudad: String = "La Paz, Bolivia",
    val cargando: Boolean = false,
    val error: String? = null,
    val respuesta: RespuestaClima? = null,
    val modoDemo: Boolean = true
)

class ClimaViewModel(private val savedState: SavedStateHandle) : ViewModel() {
    private val repository = ClimaRepository(ApiClient.api, BuildConfig.WEATHER_API_KEY)
    private val _estado = MutableStateFlow(ClimaUiState(
        ciudad = savedState["ciudad"] ?: "La Paz, Bolivia",
        modoDemo = savedState["demo"] ?: BuildConfig.WEATHER_API_KEY.isBlank()
    ))
    val estado = _estado.asStateFlow()
    private var consulta: Job? = null
    private var ultimaConsulta = _estado.value.ciudad

    init { buscarClima(_estado.value.ciudad) }

    fun cambiarCiudad(valor: String) {
        savedState["ciudad"] = valor
        _estado.update { it.copy(ciudad = valor) }
    }

    fun cambiarModo(demo: Boolean) {
        savedState["demo"] = demo
        consulta?.cancel()
        _estado.update { it.copy(modoDemo = demo, respuesta = null, error = null) }
        if (demo) cambiarCiudad("La Paz, Bolivia")
        buscarClima(_estado.value.ciudad)
    }

    fun buscarClima(ciudad: String = _estado.value.ciudad) {
        consulta?.cancel()
        val texto = ciudad.trim()
        ultimaConsulta = texto
        val demo = _estado.value.modoDemo
        _estado.update { it.copy(cargando = true, error = null, respuesta = null) }
        consulta = viewModelScope.launch {
            try {
                if (texto.isBlank()) throw ClimaException("Escribe una ciudad para buscar.")
                val respuesta = if (demo) {
                    if (!texto.equals("La Paz", true) && !texto.equals("La Paz, Bolivia", true)) {
                        throw ClimaException("La demostración incluye solo La Paz. Desactiva el modo demo para buscar otras ciudades con WeatherAPI.")
                    }
                    DatosDemo.laPaz()
                } else repository.obtenerClima(texto)
                _estado.update { it.copy(respuesta = respuesta, cargando = false) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: ClimaException) {
                _estado.update { it.copy(error = e.message, cargando = false) }
            } catch (e: Exception) {
                _estado.update { it.copy(error = "No se pudo mostrar el clima. Intenta nuevamente.", cargando = false) }
            }
        }
    }

    fun actualizarClima() = buscarClima(ultimaConsulta)
}
