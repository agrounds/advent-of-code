package com.groundsfam.advent.y2015.d14

import com.groundsfam.advent.DATAPATH
import com.groundsfam.advent.timed
import kotlin.io.path.div
import kotlin.io.path.useLines
import kotlin.math.min

const val TIME = 2503

data class Reindeer(val name: String, val speed: Int, val goTime: Int, val restTime: Int)

fun parseLine(line: String): Reindeer {
    val parts = line.split(" ")
    return Reindeer(
        parts[0],
        parts[3].toInt(),
        parts[6].toInt(),
        parts[13].toInt(),
    )
}

fun distanceTraveled(reindeer: Reindeer, time: Int): Int {
    val period = reindeer.goTime + reindeer.restTime
    val cycles = time / period
    val lastGoTime = min(time % period, reindeer.goTime)
    return reindeer.speed * (cycles * reindeer.goTime + lastGoTime)
}

fun winningPoints(reindeerList: List<Reindeer>, time: Int): Int {
    val scores = Array(reindeerList.size) { 0 }
    val positions = Array(reindeerList.size) { 0 }
    repeat(time) { t ->
        reindeerList.forEachIndexed { i, reindeer ->
            val period = reindeer.goTime + reindeer.restTime
            if (t % period < reindeer.goTime) {
                positions[i] += reindeer.speed
            }
        }
        val maxPosition = positions.maxOrNull()!!
        positions.forEachIndexed { i, position ->
            if (position == maxPosition) {
                scores[i]++
            }
        }
    }

    return scores.maxOrNull()!!
}

fun main() = timed {
    val reindeerList = (DATAPATH / "2015/day14.txt").useLines { lines ->
        lines.mapTo(mutableListOf(), ::parseLine)
    }
    reindeerList.maxOfOrNull { distanceTraveled(it, TIME) }
        .also { println("Part one: $it ")}
    println("Part two: ${winningPoints(reindeerList, TIME)}")
}
