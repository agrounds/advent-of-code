package com.groundsfam.advent.y2022.d15

import com.groundsfam.advent.DATAPATH
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

fun findNoBeaconsRanges(sensors: List<Sensor>, row: Int): List<Pair<Int, Int>> {
    val noBeaconsRanges = mutableListOf<Pair<Int, Int>>()
    sensors.mapNotNull { it.noBeacons(row) }
        .sortedBy { it.first }
        .forEach { range ->
            noBeaconsRanges.lastOrNull().let {
                when {
                    it == null || it.second < range.first - 1 ->
                        noBeaconsRanges.add(range)
                    else -> {
                        noBeaconsRanges.removeLast()
                        noBeaconsRanges.add(it.first to maxOf(it.second, range.second))
                    }
                }
            }
        }
    return noBeaconsRanges
}


fun main() {
    val sensors = (DATAPATH / "2022/day15.txt").useLines { lines ->
        lines.mapTo(mutableListOf(), ::parseSensor)
    }
    val knownBeacons = sensors.map { it.bx to it.by }.toSet()
    findNoBeaconsRanges(sensors, 2_000_000).sumOf { it.second - it.first + 1 }
        .also { println("Part one: $it") }
    for (row in 0..4_000_000) {
        findNoBeaconsRanges(sensors, row).forEach { range ->
            if (range.second in 1 until 4_000_000) {
                val beacon = range.second + 1 to row
                if (beacon !in knownBeacons) {
                    println("Part two: ${beacon.first.toLong() * 4_000_000 + beacon.second.toLong()}")
                }
            }
        }
    }
}
