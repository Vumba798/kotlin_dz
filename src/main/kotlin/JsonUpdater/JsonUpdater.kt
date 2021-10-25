package JsonUpdater
import kotlinx.serialization.json.Json
import java.io.BufferedReader
import java.io.File

abstract class JsonUpdater(
    protected val pathToNotUpdatedJson: String
) {
    protected lateinit var jsonUpdatedString: String
    protected var jsonNotUpdatedString: String
    init {
        val bufReader: BufferedReader = File(pathToNotUpdatedJson).bufferedReader()
        jsonNotUpdatedString = bufReader.use { it.readText() }
    }
    fun printNotUpdatedJson() {
        println(jsonNotUpdatedString)
    }
    fun printUpdatedJson() {
        println(jsonUpdatedString)
    }
    fun saveJson(path: String) {
        File(path).writeText(jsonUpdatedString)
    }
    abstract fun updateJson()
}

val format = Json {
    prettyPrint = true
    ignoreUnknownKeys = true
}
