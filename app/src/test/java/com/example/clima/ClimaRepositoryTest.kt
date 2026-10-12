package com.example.clima

import com.example.clima.datos.*
import com.example.clima.entidad.RespuestaClima
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit

class ClimaRepositoryTest {
    private lateinit var server: MockWebServer
    private lateinit var api: WeatherApiService

    @Before fun preparar() {
        server = MockWebServer()
        server.start()
        api = Retrofit.Builder().baseUrl(server.url("/v1/"))
            .addConverterFactory(GsonConverterFactory.create()).build()
            .create(WeatherApiService::class.java)
    }

    @After fun cerrar() { server.shutdown() }

    @Test fun convierteJsonYEnviaParametrosCorrectos() = runBlocking {
        val json = javaClass.getResource("/clima.json")!!.readText()
        server.enqueue(MockResponse().setBody(json).addHeader("Content-Type", "application/json"))
        val clima = ClimaRepository(api, "clave-prueba").obtenerClima("  La Paz, Bolivia  ")
        assertEquals("La Paz", clima.location.name)
        assertEquals(-16.5, clima.location.lat, 0.001)
        assertEquals(16.0, clima.current.temp_c, 0.001)
        assertEquals(48, clima.current.humidity)
        assertEquals(1003, clima.current.condition.code)
        assertEquals(3, clima.forecast.forecastday.size)
        assertEquals(75, clima.forecast.forecastday[1].day.daily_chance_of_rain)
        val request = server.takeRequest(2, TimeUnit.SECONDS)!!
        assertEquals("GET", request.method)
        assertEquals("/v1/forecast.json", request.requestUrl!!.encodedPath)
        assertEquals("La Paz, Bolivia", request.requestUrl!!.queryParameter("q"))
        assertEquals("clave-prueba", request.requestUrl!!.queryParameter("key"))
        assertEquals("3", request.requestUrl!!.queryParameter("days"))
        assertEquals("es", request.requestUrl!!.queryParameter("lang"))
    }

    @Test fun rechazaCiudadVaciaSinConsultarServidor() = runBlocking {
        val mensaje = errorDe { ClimaRepository(api, "clave-prueba").obtenerClima("   ") }
        assertTrue(mensaje.contains("Escribe una ciudad"))
        assertEquals(0, server.requestCount)
    }

    @Test fun rechazaClaveVaciaSinConsultarServidor() = runBlocking {
        val mensaje = errorDe { ClimaRepository(api, "").obtenerClima("La Paz") }
        assertTrue(mensaje.contains("Falta la clave"))
        assertEquals(0, server.requestCount)
    }

    @Test fun informaCiudadInexistente() = runBlocking {
        server.enqueue(MockResponse().setResponseCode(400)
            .setBody("""{"error":{"code":1006,"message":"No matching location found."}}"""))
        assertTrue(errorDe { ClimaRepository(api, "clave").obtenerClima("CiudadInexistente") }
            .contains("No encontramos"))
    }

    @Test fun informaLimiteDeConsultas() = runBlocking {
        server.enqueue(MockResponse().setResponseCode(403)
            .setBody("""{"error":{"code":2007,"message":"Quota exceeded"}}"""))
        assertTrue(errorDe { ClimaRepository(api, "clave").obtenerClima("La Paz") }
            .contains("límite"))
    }

    @Test fun informaClaveInvalida() = runBlocking {
        server.enqueue(MockResponse().setResponseCode(401)
            .setBody("""{"error":{"code":2006,"message":"Invalid key"}}"""))
        assertTrue(errorDe { ClimaRepository(api, "clave").obtenerClima("La Paz") }
            .contains("no es válida"))
    }

    @Test fun informaFalloDeConexion() = runBlocking {
        assertTrue(errorDe { ClimaRepository(apiQueFalla(IOException()), "clave").obtenerClima("La Paz") }
            .contains("Internet"))
    }

    @Test fun propagaCancelacionDeConsulta() = runBlocking {
        var cancelada = false
        try {
            ClimaRepository(apiQueFalla(CancellationException()), "clave").obtenerClima("La Paz")
        } catch (e: CancellationException) { cancelada = true }
        assertTrue(cancelada)
    }

    private suspend fun errorDe(accion: suspend () -> Unit): String {
        try { accion() } catch (e: ClimaException) { return e.message.orEmpty() }
        throw AssertionError("Se esperaba ClimaException")
    }

    private fun apiQueFalla(error: Exception) = object : WeatherApiService {
        override suspend fun obtenerPronostico(key: String, ciudad: String, dias: Int,
            idioma: String, calidadAire: String, alertas: String): RespuestaClima {
            throw error
        }
    }
}
