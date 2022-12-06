package advent.y2015.d12

import advent.DATAPATH
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.intOrNull
import kotlin.io.path.div
import kotlin.io.path.useLines

fun sumNums(jsonString: String): Int {
    var sum = 0
    var i = 0
    while (i < jsonString.length) {
        var j = i
        while (j < jsonString.length && jsonString[j] in "-0123456789") {
            j++
        }
        if (j > i) {
            sum += jsonString.substring(i, j).toInt()
            i = j
        } else {
            i++
        }
    }
    return sum
}

fun sumNums(jsonElement: JsonElement, ignoreValue: String? = null): Int = when (jsonElement) {
    is JsonPrimitive -> jsonElement.intOrNull ?: 0
    is JsonArray -> jsonElement.sumOf { sumNums(it, ignoreValue) }
    is JsonObject -> {
        val stringValues = jsonElement.values.mapNotNull {
            if (it is JsonPrimitive) it.content
            else null
        }
        if (ignoreValue in stringValues) 0
        else jsonElement.values.sumOf { sumNums(it, ignoreValue) }
    }
}


fun main() {
    val jsonString = (DATAPATH / "2015/day12.txt").useLines { lines ->
        lines.first()
    }
    println("Part one, seeking integer substrings: ${sumNums(jsonString)}")
    val jsonElement = Json.parseToJsonElement(jsonString)
    println("Part one, parsing json: ${sumNums(jsonElement)}")
    println("Part two: ${sumNums(jsonElement, "red")}")
}
