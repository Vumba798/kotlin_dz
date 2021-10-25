import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import java.io.File
import java.io.BufferedReader
import java.net.HttpURLConnection
import java.net.URL
import JsonUpdater.*
import java.io.FileNotFoundException


fun main() {
    try {
        val currency = CurrencyUpdater("ДЗ.json")
        currency.printUpdatedJson()
        currency.saveJson("DZ.json")
    } catch(exception: FileNotFoundException){
        println("CurrencyUpdater init error: " + exception.message)
    }
    println("///////////////////////////////////////////////////////////")
    try {
        val weather = WeatherUpdater("ДЗ 2.json")
        weather.printUpdatedJson()
        weather.saveJson("DZ 2.json")
    } catch(exception: FileNotFoundException){
        println("WeatherUpdater init error: " + exception.message)
    }

    println("//////////////////////////////////////////////////////////")
    try {
        val json = ExchangeAndWeatherUnion("DZ 2.json", "DZ.json")
        println(json.getJsonString())
        println(format.encodeToString(json.getForecast("Moscow")))
        println(format.encodeToString(json.getExchange("USD")))
    }catch(exception: Exception) {
        println(exception.message)
    }
}