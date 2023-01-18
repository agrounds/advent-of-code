package advent.y2021.d07

import advent.DATAPATH
import kotlin.io.path.div
import kotlin.io.path.useLines
import kotlin.math.abs


fun minFuelOne(positions: List<Int>): Int {
    val median = positions.sorted().let {
        it[it.size / 2]
    }
    return positions.sumOf { abs(it - median) }
}

fun minFuelTwo(positions: List<Int>): Int {
    // hunch: the best position to move to is the mean of the positions
    val mean = positions.sum() / positions.size
    // consider mean + 1 in case we rounded down a lot
    return listOf(mean, mean + 1).minOf { target ->
        positions.sumOf { position ->
            val d = abs(position - target)
            // formula for triangle number
            d * (d + 1) / 2
        }
    }
}

fun main() {
    val positions = (DATAPATH / "2021/day07.txt").useLines { lines ->
        lines.first().split(",").map { it.toInt() }
    }
    println("Part one: ${minFuelOne(positions)}")
    println("Part two: ${minFuelTwo(positions)}")
}
