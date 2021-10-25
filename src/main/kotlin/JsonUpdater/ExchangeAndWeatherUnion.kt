package JsonUpdater

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*
import java.io.File

class ExchangeAndWeatherUnion(
    val pathToWeatherJson: String,
    val pathToExchangeJson: String) {

    private lateinit var exchangeAndWeatherJson: JsonObject

    init {
        updateUnion()
    }
    fun updateUnion() {
        var bufReader = File(pathToWeatherJson).bufferedReader()
        val weatherData = bufReader.use { it.readText() }
        bufReader = File(pathToExchangeJson).bufferedReader()
        val exchangeData = bufReader.use { it.readText() }

        val weatherJson = format.decodeFromString<WeatherJsonFormat>(weatherData)
        val exchangeJson = format.decodeFromString<ExchangeJsonFormat>(exchangeData)

        if (weatherJson.Date != exchangeJson.Date) {
            throw Exception("Dates of exchange file and weather file are not equal")
        }
        exchangeAndWeatherJson = buildJsonObject {
            put("Date", exchangeJson.Date)
            put("Exchange base", exchangeJson.Country)
            putJsonArray("Weather") {
                for (elem in weatherJson.`Currency list`) {
                    addJsonObject {
                        put("City", elem.City)
                        put("Temperature Celsius", elem.`Temperature Celsius`)
                        put("Temperature Fahrenheit", elem.`Temperature Fahrenheit`)
                    }
                }
            }
            putJsonArray("Exchange rates") {
                for (elem in exchangeJson.`Currency list`) {
                    addJsonObject {
                        put("CharCode", elem.CharCode)
                        put("Name", elem.Name)
                        put("Value", elem.Value)
                    }
                }
            }
        }
    }
    fun getJsonString(): String {
        return format.encodeToString(exchangeAndWeatherJson)
    }
    fun getForecast(city: String): JsonElement {
        val cityWithQuotes = "\"$city\""
        return exchangeAndWeatherJson.jsonObject["Weather"]!!.jsonArray
            .first {
                it.jsonObject["City"].toString() == cityWithQuotes
            }
    }
    fun getExchange(charCode: String): JsonElement {
        val charCodeWithQuotes = "\"$charCode\""
        return exchangeAndWeatherJson.jsonObject["Exchange rates"]!!.jsonArray
            .first {
                it.jsonObject["CharCode"].toString() == charCodeWithQuotes
            }
    }
}