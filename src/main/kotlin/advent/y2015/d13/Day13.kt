package advent.y2015.d13

import advent.DATAPATH
import kotlin.io.path.div
import kotlin.io.path.useLines

fun maxHappiness(opinions: Array<IntArray>): Int {
    // happiness  - total happiness from seatings so far
    // prevPerson - most recent seated person
    // needToSeat - set of people still needing to be seated
    // the first and last person (circular table) is always 0 by convention
    fun helper(happiness: Int, prevPerson: Int, needToSeat: Set<Int>): Int {
        if (needToSeat.isEmpty()) {
            return happiness + opinions[0][prevPerson] + opinions[prevPerson][0]
        }
        return needToSeat.maxOf { person ->
            helper(happiness + opinions[prevPerson][person] + opinions[person][prevPerson], person, needToSeat - person)
        }
    }

    return helper(0, 0, (1 until opinions.size).toSet())
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
    println("Part one: ${maxHappiness(opinions)}")
    val opinions2 = Array(9) { IntArray(9) }
    opinions.forEachIndexed { i, row ->
        row.forEachIndexed { j, happiness ->
            opinions2[i][j] = happiness
        }
    }
    println("Part two: ${maxHappiness(opinions2)}")
}
