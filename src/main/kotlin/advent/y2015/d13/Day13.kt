package advent.y2015.d13

import advent.DATAPATH
import kotlin.io.path.div
import kotlin.io.path.useLines

fun maxHappiness(opinions: Array<IntArray>): Int {
    var max = 0

    return max
}


fun main() {
    val opinions = Array(8) { IntArray(8) }
    (DATAPATH / "2015/day13.txt").useLines { lines ->
        val names = mutableListOf<String>()
        lines.forEach { line ->
            val parts = line.split(" ")
            val person = parts[0]
            val neighbor = parts[10].let { it.substring(0, it.length-1) }  // remove trailing period
            val happiness = parts[3].let {
                if (parts[2] == "lose") -it.toInt()
                else it.toInt()
            }
            if (person !in names) names.add(person)
            if (neighbor !in names) names.add(neighbor)
            opinions[names.indexOf(person)][names.indexOf(neighbor)] = happiness
        }
    }
}
