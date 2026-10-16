# ClimaApp — Android Studio

Proyecto independiente en Kotlin y Jetpack Compose basado en el diagrama adjunto. Incluye entidades, pantallas y conexión a WeatherAPI. No se recibió un proyecto Android previo, por lo que se entrega una base completa para abrir directamente.

## 1. Abrir y ejecutar

1. Descomprime el ZIP en una carpeta de tu computadora.
2. En Android Studio elige **Open** y selecciona **ClimaApp**, la carpeta que contiene `settings.gradle.kts`.
3. Usa **JDK 17** en Settings → Build, Execution, Deployment → Build Tools → Gradle → Gradle JDK.
4. Instala **Android SDK Platform 35** desde SDK Manager si Android Studio lo solicita. Acepta la instalación de las herramientas necesarias.
5. Espera la sincronización de Gradle. La primera vez necesita Internet para descargar dependencias.
6. Ejecuta el módulo `app` en un emulador o teléfono con **Android 8.0 (API 26) o superior**.

Sin clave, la aplicación abre el modo demostración. Los valores son ficticios y se identifican como tales en pantalla. La demostración solo acepta `La Paz` o `La Paz, Bolivia`.

## 2. Activar los datos reales

1. Crea tu cuenta en https://www.weatherapi.com/ y obtén tu API key desde tu cuenta.
2. En la raíz de **ClimaApp**, copia `apikey.properties.example` y llama a la copia **apikey.properties**. En Windows verifica que no termine en `.txt`.
3. Escribe tu clave real después de `=` sin comillas:

```properties
WEATHER_API_KEY=PEGA_AQUI_TU_CLAVE
```

4. Guarda, pulsa **Sync Project with Gradle Files**, detén la app y vuelve a ejecutarla. La clave se incorpora durante la compilación; no basta con cambiar el archivo mientras la app sigue abierta.
5. Desactiva **Modo demostración** si continúa activado y busca, por ejemplo, `La Paz, Bolivia`, `Cochabamba, Bolivia` o `Madrid, Spain`.
6. También puedes consultar coordenadas, por ejemplo `-16.5,-68.15`.

La app consulta `https://api.weatherapi.com/v1/forecast.json` mediante Retrofit, con `key`, `q`, `days=3` y `lang=es`. Presenta los días que entregue tu cuenta; la disponibilidad depende del plan y de la respuesta de WeatherAPI. Los datos se consultan, no se envían como nuevas mediciones a WeatherAPI.

La clave no está incluida en esta entrega. `apikey.properties` está excluido de Git. En una aplicación distribuida públicamente, una clave incluida en el APK se puede extraer; para ese caso conviene realizar las consultas a través de un backend propio.

## 3. Organización según el diagrama

El código está en `app/src/main/java/com/example/clima/`.

| Carpeta / clase | Responsabilidad |
|---|---|
| `entidad/RespuestaClima.kt` | Agrupa ubicación, clima actual y pronóstico. |
| `entidad/Ubicacion.kt` | Ciudad, región, país, hora local y coordenadas. |
| `entidad/ClimaActual.kt` | Temperatura, sensación, humedad, viento, precipitación, UV y condición. |
| `entidad/Pronostico.kt` | Lista de días del pronóstico. |
| `entidad/DiaPronostico.kt` | Fecha y resumen de un día. |
| `entidad/ResumenDia.kt` | Máxima, mínima, probabilidad de lluvia y condición. |
| `entidad/Condicion.kt` | Descripción, icono y código meteorológico. |
| `datos/WeatherApiService.kt` | Interfaz HTTP de Retrofit. |
| `datos/ClimaRepository.kt` | Obtiene datos y traduce los errores del servicio. |
| `presentacion/ClimaViewModel.kt` | Mantiene ciudad, carga, error y respuesta; busca y actualiza. |
| `MainActivity.kt` | Inicia la interfaz y obtiene el ViewModel. |
| `ui/ClimaApp.kt` | Búsqueda y vistas Actual, Pronóstico y Detalles. |
| `datos/ApiClient.kt` | Configura HTTPS, Retrofit y tiempos de espera. |
| `datos/DatosDemo.kt` | Datos ficticios para probar sin clave. |

Las siete entidades mantienen los nombres y tipos del diagrama, incluidos los campos `temp_c`, `forecastday`, etc. Esto permite convertir el JSON directamente con Gson. Son clases de datos, no tablas de una base de datos. No se añadió una base de datos porque no aparece en el diagrama.

Las pantallas y el estado de interfaz complementan el diagrama. `ClimaUiState` agrupa los valores observables de `ClimaViewModel` para que Compose actualice la pantalla automáticamente. Las relaciones de composición se representan como propiedades de otras clases y listas.

## 4. Funciones de la interfaz

- Buscar una ciudad con el botón Buscar o con la tecla de búsqueda del teclado.
- Actualizar la última consulta realizada. Si editaste el campo sin buscar, Actualizar sigue consultando la ubicación anterior.
- Ver temperatura, sensación térmica, humedad, viento, precipitación e índice UV.
- Ver máxima, mínima y probabilidad de lluvia de cada día disponible.
- Ver coordenadas y horarios de la ciudad consultada.
- Cambiar entre datos reales y demostración, sin mezclar ambos.
- Mostrar carga y errores con reintento.
- Cancelar la consulta anterior cuando comienza una nueva.
- Mantener el estado durante una rotación mediante ViewModel; recuperar texto y modo mediante SavedStateHandle tras recreación del proceso y volver a consultar.
- Adaptarse al tema claro u oscuro del teléfono.

No necesita permiso de GPS porque la ubicación se introduce por texto. Solo requiere acceso a Internet para el modo real. No almacena consultas para uso sin conexión; ante una consulta fallida muestra el error y no presenta resultados antiguos como actuales.

## 5. Verificación

Se revisaron la correspondencia de las siete entidades con el diagrama, el JSON de prueba, los XML, la configuración de archivos y el empaquetado.

**No se pudo compilar ni ejecutar en un emulador en este entorno:** no hay Android SDK y la descarga de Gradle falló con `UnknownHostException: services.gradle.org`. Tampoco se hizo una consulta autenticada real porque no se proporcionó una API key. No se incluye un APK ni se afirma que la compilación o las pruebas hayan pasado.

Se incluyen **ocho pruebas automatizadas**, pendientes de ejecución: conversión JSON y parámetros HTTP, ciudad vacía, clave vacía, ciudad inexistente, límite de consultas, clave inválida, error de conexión y propagación de cancelación. Utilizan un servidor local simulado y no gastan consultas reales.

En la terminal de Android Studio (PowerShell), desde la raíz del proyecto:

```powershell
.\gradlew.bat testDebugUnitTest assembleDebug
```

En macOS/Linux:

```bash
./gradlew testDebugUnitTest assembleDebug
```

Si la compilación termina correctamente, el APK se genera en `app/build/outputs/apk/debug/app-debug.apk` y el informe de pruebas en `app/build/reports/tests/testDebugUnitTest/index.html`.

Comprueba en el teléfono: abrir la demo; cambiar las tres pestañas; buscar una ciudad real con tu clave; buscar una ciudad inexistente; quitar Internet y actualizar; rotar la pantalla; repetir con tema oscuro. Estos pasos de interfaz quedan pendientes de ejecución.

## 6. Si lo incorporas a otro proyecto

La opción más directa es abrir esta carpeta como proyecto independiente. Para incorporarlo a un proyecto existente, copia las carpetas del paquete y adapta `com.example.clima` al namespace real; añade también las dependencias, el permiso de Internet y la configuración de BuildConfig. No reemplaces todo tu Gradle sin revisar las dependencias del proyecto existente.

Versiones fijadas para esta entrega: AGP 8.7.3, Gradle 8.9, Kotlin/Compose compiler 2.0.21, Java 17, compileSdk/targetSdk 35. No usa NavigationEvent ni dependencias que requieran compileSdk 36.

## Referencias

- Diagrama proporcionado: `docs/diagrama_clima_corregido.jpg.jpeg`.
- WeatherAPI, documentación: https://www.weatherapi.com/docs/
- Parámetros: https://www.weatherapi.com/docs/request-parameters/
- Pronóstico: https://www.weatherapi.com/docs/forecast-api/
- Android, AGP 8.7: https://developer.android.com/build/releases/past-releases/agp-8-7-0-release-notes
