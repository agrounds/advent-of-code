package advent.y2022.d15

import advent.DATAPATH
import kotlin.io.path.div
import kotlin.io.path.useLines
import kotlin.math.abs

// (x, y) - position of this sensor
// (bx, by) - position of closest beacon to this sensor
data class Sensor(val x: Int, val y: Int, val bx: Int, val by: Int) {
    fun noBeacons(row: Int): Pair<Int, Int>? {
        val dist = abs(x - bx) + abs(y - by)
        val dy = abs(y - row)
        return (dist - dy).let {
            when {
                it <= 0 -> null
                by == row -> {
                    when {
                        bx > x -> x - it to x + it - 1
                        bx < x -> x - it + 1 to x + it
                        else -> null
                    }
                }
                else -> x - it to x + it
            }
        }
    }
}
fun parseSensor(line: String): Sensor {
    val parts = line.split(" ")
    return Sensor(
        parts[2].let {
            it.substring(2, it.length - 1).toInt()
        },
        parts[3].let {
            it.substring(2, it.length - 1).toInt()
        },
        parts[8].let {
            it.substring(2, it.length - 1).toInt()
        },
        parts[9].let {
            it.substring(2, it.length).toInt()
        }
    )
}


fun main() {
    val sensors = (DATAPATH / "2022/day15.txt").useLines { lines ->
        lines.mapTo(mutableListOf(), ::parseSensor)
    }
    val noBeaconsRanges = mutableListOf<Pair<Int, Int>>()
    sensors.mapNotNull { it.noBeacons(2_000_000) }
        .sortedBy { it.first }
        .forEach { range ->
            noBeaconsRanges.lastOrNull().let {
                when {
                    it == null || it.second < range.first ->
                        noBeaconsRanges.add(range)
                    else -> {
                        noBeaconsRanges.removeLast()
                        noBeaconsRanges.add(it.first to maxOf(it.second, range.second))
                    }
                }
            }
        }
    noBeaconsRanges.sumOf { it.second - it.first + 1 }
        .also { println("Part one: $it") }
}
