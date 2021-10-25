package JsonUpdater

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import java.io.File
import java.net.HttpURLConnection
import java.net.URL



@Serializable
data class ExchangeJsonFormat(
    val squadName: String,
    val Country: String,
    val Date: String,
    val `Currency list`: List<CurrencyUpdater.CurrencyFormat>
)


class CurrencyUpdater(path: String) : JsonUpdater(pathToNotUpdatedJson = path) {

    @Serializable
    data class CurrencyFormat(
        val CharCode: String,
        var Name: String,
        var Value: String
    )
    @Serializable
    data class HistoricalJsonFormat(
        val disclaimer: String,
        val license: String,
        val timestamp: Int,
        val base: String,
        val rates: Map<String, Float>
    )
    private lateinit var serializedJson: ExchangeJsonFormat

    init {
        jsonNotUpdatedString = File(pathToNotUpdatedJson).bufferedReader().readText()
        updateJson()
    }

    override fun updateJson() {
        serializedJson = format.decodeFromString<ExchangeJsonFormat>(jsonNotUpdatedString)
        val date: String = serializedJson.Date.replace('/', '-') // for get request

        // for filling 'Value' field
        try {
            var url =
                URL("https://openexchangerates.org/api/historical/$date.json?app_id=d1290d44145b4d62b6760da7db9446e8")
            var apiOutputString = with(url.openConnection() as HttpURLConnection) {
                requestMethod = "GET"
                inputStream.bufferedReader().use {
                    it.readText()
                }
            }
            val historicalJson = format.decodeFromString<HistoricalJsonFormat>(apiOutputString)
            val rubCost = historicalJson.rates["RUB"]!!.toFloat()

            // for filling 'Name' field
            url = URL("https://openexchangerates.org/api/currencies.json?app_id=d1290d44145b4d62b6760da7db9446e8")
            apiOutputString = with(url.openConnection() as HttpURLConnection) {
                requestMethod = "GET"
                inputStream.bufferedReader().use {
                    it.readText()
                }
            }
            val currenciesJson = Json.parseToJsonElement(apiOutputString)
            for (elem in serializedJson.`Currency list`) {
                try {
                    if (elem.Name == "") {
                        val newName = currenciesJson.jsonObject[elem.CharCode].toString()
                        if (newName == "null") {
                            throw NoSuchElementException("There is no any element with charCode \"${elem.CharCode}\"")
                        }
                        elem.Name = newName.substring(1, newName.lastIndex) // to ignore \" symbols
                    }
                    if (elem.Value == "") {
                        val newValue = historicalJson.rates[elem.CharCode]
                            ?: throw NoSuchElementException("There is no any element with charCode \"${elem.CharCode}\"")
                        elem.Value = (newValue * rubCost).toString().replace('.', ',')
                    }
                } catch (exception: Exception) {
                    println(exception.message)
                }
            }
            jsonUpdatedString = format.encodeToString(serializedJson)
        } catch (exception: Exception) {
            println(exception.message)
        }
    }

    fun getUpdatedSerializedJson(): ExchangeJsonFormat {
        return serializedJson
    }
}