package com.groundsfam.advent.y2024.d18

import com.groundsfam.advent.DATAPATH
import com.groundsfam.advent.points.*
import com.groundsfam.advent.timed
import kotlin.io.path.div
import kotlin.io.path.useLines

val START = Point(0, 0)
const val LIMIT = 70
val EXIT = Point(LIMIT, LIMIT)

data class Path(val p: Point, val len: Int)


fun shortestExit(fallingBytes: List<Point>, numBytes: Int): Int? {
    val obstacles = fallingBytes.take(numBytes).toSet()

    val queue = ArrayDeque<Path>()
    val seen = mutableSetOf<Point>()
    queue.add(Path(START, 0))
    while (queue.isNotEmpty()) {
        val (p, len) = queue.removeFirst()
        if (p == EXIT) return len
        if (!seen.add(p)) continue

        val next = p.adjacents(false)
            .filter { q ->
                val (x, y) = q
                x in 0..LIMIT && y in 0..LIMIT &&
                    q !in seen && q !in obstacles
            }

        next.forEach {
                queue.add(Path(it, len + 1))
            }
    }
    return null
}

fun blockingByte(fallingBytes: List<Point>): Point {
    var a = 1024
    var b = fallingBytes.size
    while (b - a > 1) {
        val mid = (a + b) / 2
        if (shortestExit(fallingBytes, mid) == null) {
            b = mid
        } else {
            a = mid
        }
    }
    return fallingBytes[b - 1]
}

fun main() = timed {
    val fallingBytes = (DATAPATH / "2024/day18.txt").useLines { lines ->
        lines.mapTo(mutableListOf()) { line ->
            line.split(",")
                .let { (a, b) -> Point(a.toInt(), b.toInt()) }
        }
    }
    println("Part one: ${shortestExit(fallingBytes, 1024)}")
    blockingByte(fallingBytes)
        .let { (x, y) -> "$x,$y" }
        .also { println("Part two: $it") }
}
