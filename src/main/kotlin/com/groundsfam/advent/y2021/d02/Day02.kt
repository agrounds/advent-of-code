package com.groundsfam.advent.y2021.d02

import com.groundsfam.advent.DATAPATH
import com.groundsfam.advent.Direction
import com.groundsfam.advent.points.Point
import com.groundsfam.advent.asPoint
import com.groundsfam.advent.points.sum
import com.groundsfam.advent.timed
import kotlin.io.path.div
import kotlin.io.path.useLines


fun parseLine(line: String): Point = line.split(" ")
    .let { (dir, amount) ->
        when (dir) {
            "forward" -> Direction.RIGHT
            "down" -> Direction.DOWN
            "up" -> Direction.UP
            else -> throw RuntimeException("Illegal direction $dir")
        }.asPoint() * amount.toInt()
    }

fun followDirectionsWithAim(directions: List<Point>): Point {
    var aim = 0
    var position = Point(0, 0)
    directions.forEach { (x, y) ->
        if (x != 0) {  // forward
            position += Point(x, aim * x)
        } else {  // up or down
            aim += y
        }
    }
    return position
}

fun main() = timed {
    val directions = (DATAPATH / "2021/day02.txt").useLines { lines ->
        lines.toList().map(::parseLine)
    }
    directions.sum()
        .also { println("Part one: ${it.x * it.y}") }
    followDirectionsWithAim(directions)
        .also { println("Part two: ${it.x * it.y}") }
}
