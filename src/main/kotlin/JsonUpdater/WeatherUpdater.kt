package JsonUpdater
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import java.net.HttpURLConnection
import java.net.URL

@Serializable
data class WeatherJsonFormat (
    val squadName: String,
    val Date: String,
    val `Currency list`: List<WeatherUpdater.CurrencyFormat>
)

class WeatherUpdater(path: String): JsonUpdater(pathToNotUpdatedJson = path){
    @Serializable
    data class CurrencyFormat (
        val City: String,
        var `Temperature Celsius`: String,
        var `Temperature Fahrenheit`: String
    )

    private var serializedJson: WeatherJsonFormat = format.decodeFromString(jsonNotUpdatedString)

    init {
        updateJson()
    }

    override fun updateJson() {
        for (elem in serializedJson.`Currency list`) {
            val city = elem.City
            val date: String = serializedJson.Date.replace('/', '-') // for get request
            val url= URL("http://api.worldweatheronline.com/premium/v1/past-weather.ashx?key=cf86e64fa55742f9bce90037212510&q=$city&format=json&date=$date")
            val apiOutputString = with(url.openConnection() as HttpURLConnection) {
                requestMethod = "GET"
                inputStream.bufferedReader().use {
                    it.readText()
                }
            }

            val weatherJson = Json.parseToJsonElement(apiOutputString)
            var tmpValue = weatherJson.jsonObject["data"]!!.jsonObject["weather"]!!.jsonArray[0].jsonObject["avgtempC"].toString()
            elem.`Temperature Celsius` = tmpValue.substring(1,tmpValue.lastIndex)
            tmpValue = weatherJson.jsonObject["data"]!!.jsonObject["weather"]!!.jsonArray[0].jsonObject["avgtempF"].toString()
            elem.`Temperature Fahrenheit` = tmpValue.substring(1,tmpValue.lastIndex)
        }

        jsonUpdatedString = format.encodeToString(serializedJson)
        println(jsonUpdatedString)
    }
}