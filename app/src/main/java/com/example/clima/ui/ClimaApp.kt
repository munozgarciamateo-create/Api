package com.example.clima.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.clima.entidad.*
import com.example.clima.presentacion.ClimaViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun ClimaApp(viewModel: ClimaViewModel) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    var pestana by rememberSaveable { mutableIntStateOf(0) }
    val teclado = LocalSoftwareKeyboardController.current
    val buscar: () -> Unit = {
        teclado?.hide()
        viewModel.buscarClima()
    }
    Scaffold { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).imePadding(),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text("TU DÍA, A UN VISTAZO", color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelLarge)
                Text("Clima", style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold)
                Text("Consulta el tiempo y planifica tu día.", style = MaterialTheme.typography.bodyMedium)
            }
            item {
                OutlinedTextField(
                    value = estado.ciudad, onValueChange = viewModel::cambiarCiudad,
                    modifier = Modifier.fillMaxWidth(), singleLine = true,
                    label = { Text("Ciudad o coordenadas") },
                    placeholder = { Text("La Paz, Bolivia") },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { buscar() }),
                    shape = RoundedCornerShape(16.dp)
                )
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = buscar, enabled = !estado.cargando, modifier = Modifier.weight(1f)) {
                        Text("Buscar")
                    }
                    OutlinedButton(onClick = viewModel::actualizarClima, enabled = !estado.cargando) {
                        Text("Actualizar")
                    }
                }
            }
            item {
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)) {
                    Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text("Modo demostración", fontWeight = FontWeight.SemiBold)
                            Text(if (estado.modoDemo) "Datos ficticios de La Paz. No son mediciones reales."
                                else "Consultas reales a WeatherAPI.", style = MaterialTheme.typography.bodySmall)
                        }
                        Switch(checked = estado.modoDemo, onCheckedChange = viewModel::cambiarModo)
                    }
                }
            }
            if (estado.cargando) {
                item {
                    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Text("Consultando el clima…", modifier = Modifier.padding(top = 8.dp))
                    }
                }
            }
            estado.error?.let { mensaje ->
                item {
                    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)) {
                        Column(Modifier.padding(16.dp)) {
                            Text("No pudimos completar la consulta", fontWeight = FontWeight.Bold)
                            Text(mensaje)
                            TextButton(onClick = viewModel::actualizarClima) { Text("Reintentar") }
                        }
                    }
                }
            }
            estado.respuesta?.let { respuesta ->
                item {
                    Text(respuesta.location.name, style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold)
                    Text(listOf(respuesta.location.region, respuesta.location.country)
                        .filter { it.isNotBlank() }.distinct().joinToString(" · "))
                }
                item {
                    TabRow(selectedTabIndex = pestana) {
                        listOf("Actual", "Pronóstico", "Detalles").forEachIndexed { index, titulo ->
                            Tab(selected = pestana == index, onClick = { pestana = index }, text = { Text(titulo) })
                        }
                    }
                }
                when (pestana) {
                    0 -> item { PantallaActual(respuesta.current) }
                    1 -> {
                        item { Text("Pronóstico disponible · ${respuesta.forecast.forecastday.size} días",
                            style = MaterialTheme.typography.titleMedium) }
                        if (respuesta.forecast.forecastday.isEmpty()) {
                            item { Text("No hay días disponibles en esta respuesta.") }
                        }
                        respuesta.forecast.forecastday.forEach { dia ->
                            item(key = dia.date) { TarjetaDia(dia) }
                        }
                    }
                    2 -> item { PantallaDetalles(respuesta) }
                }
                item {
                    Text(if (estado.modoDemo) "DEMOSTRACIÓN · Valores de ejemplo"
                        else "Datos: WeatherAPI.com · Actualización: ${respuesta.current.last_updated}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
private fun PantallaActual(clima: ClimaActual) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer)) {
            Column(Modifier.fillMaxWidth().padding(24.dp)) {
                Text("TEMPERATURA ACTUAL", style = MaterialTheme.typography.labelLarge)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("${numero(clima.temp_c)}°C", fontSize = 52.sp, fontWeight = FontWeight.Light,
                        modifier = Modifier.weight(1f))
                    IconoCondicion(clima.condition)
                }
                Text(clima.condition.text, style = MaterialTheme.typography.titleLarge)
                Text("Sensación térmica de ${numero(clima.feelslike_c)} °C")
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Dato("Humedad", "${clima.humidity}%", Modifier.weight(1f))
            Dato("Viento", "${numero(clima.wind_kph)} km/h", Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Dato("Precipitación", "${numero(clima.precip_mm)} mm", Modifier.weight(1f))
            Dato("Índice UV", numero(clima.uv), Modifier.weight(1f))
        }
    }
}

@Composable
private fun Dato(titulo: String, valor: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        Column(Modifier.padding(16.dp)) {
            Text(titulo, style = MaterialTheme.typography.labelLarge)
            Text(valor, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun TarjetaDia(dia: DiaPronostico) {
    Card {
        Column(Modifier.fillMaxWidth().padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(fecha(dia.date), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(dia.day.condition.text)
                    Text("Máx. ${numero(dia.day.maxtemp_c)} °C  ·  Mín. ${numero(dia.day.mintemp_c)} °C")
                }
                IconoCondicion(dia.day.condition)
            }
            Text("Probabilidad de lluvia: ${dia.day.daily_chance_of_rain}%",
                color = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
private fun PantallaDetalles(respuesta: RespuestaClima) {
    Card {
        Column(Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Ubicación y medición", style = MaterialTheme.typography.titleLarge)
            Text("Ciudad: ${respuesta.location.name}")
            Text("Región: ${respuesta.location.region.ifBlank { "No informada" }}")
            Text("País: ${respuesta.location.country}")
            Text("Latitud: ${respuesta.location.lat}")
            Text("Longitud: ${respuesta.location.lon}")
            HorizontalDivider()
            Text("Hora local de la ciudad: ${respuesta.location.localtime}")
            Text("Última medición: ${respuesta.current.last_updated}")
            Text("Las horas corresponden a la ciudad consultada.", style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun IconoCondicion(condicion: Condicion) {
    if (condicion.icon.isBlank()) {
        Text(when (condicion.code) { 1000 -> "☀"; 1189 -> "☂"; else -> "☁" }, fontSize = 40.sp)
    } else {
        val url = if (condicion.icon.startsWith("//")) "https:${condicion.icon}"
            else condicion.icon.replace("http://", "https://")
        AsyncImage(model = url, contentDescription = condicion.text, modifier = Modifier.size(64.dp))
    }
}

private fun numero(valor: Double) = String.format(Locale.forLanguageTag("es-BO"), "%.1f", valor)
private fun fecha(valor: String): String = runCatching {
    LocalDate.parse(valor).format(DateTimeFormatter.ofPattern("EEEE d 'de' MMMM", Locale.forLanguageTag("es")))
}.getOrDefault(valor)
