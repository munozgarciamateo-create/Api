package com.example.clima.datos

import com.example.clima.entidad.*
import java.time.LocalDate

// Valores ficticios para probar las pantallas sin clave ni conexión.
object DatosDemo {
    fun laPaz(): RespuestaClima {
        val hoy = LocalDate.now()
        val condiciones = listOf(
            Condicion("Parcialmente nublado", "", 1003),
            Condicion("Lluvia moderada", "", 1189),
            Condicion("Soleado", "", 1000)
        )
        return RespuestaClima(
            Ubicacion("La Paz", "La Paz", "Bolivia", "$hoy 12:00", -16.5, -68.15),
            ClimaActual(16.0, 15.0, 48, 11.0, 0.0, 6.0, "$hoy 12:00", condiciones[0]),
            Pronostico(listOf(
                DiaPronostico(hoy.toString(), ResumenDia(18.0, 5.0, 20, condiciones[0])),
                DiaPronostico(hoy.plusDays(1).toString(), ResumenDia(15.0, 6.0, 75, condiciones[1])),
                DiaPronostico(hoy.plusDays(2).toString(), ResumenDia(19.0, 4.0, 5, condiciones[2]))
            ))
        )
    }
}
