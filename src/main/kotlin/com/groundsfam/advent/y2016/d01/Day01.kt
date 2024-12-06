package com.groundsfam.advent.y2016.d01

import com.groundsfam.advent.DATAPATH
import com.groundsfam.advent.Direction
import com.groundsfam.advent.points.Point
import com.groundsfam.advent.points.go
import com.groundsfam.advent.timed
import kotlin.io.path.div
import kotlin.io.path.readText
import kotlin.math.abs

fun followDirections(directions: List<String>, partTwo: Boolean): Int {
    var pos = Point(0, 0)
    var dir = Direction.UP
    val visited = mutableSetOf(pos)
    directions.forEach { direction ->
        dir =
            if (direction[0] == 'R') dir.cw
            else dir.ccw
        repeat(direction.substring(1).toInt()) {
            pos = pos.go(dir)
            if (partTwo && !visited.add(pos)) {
                return abs(pos.x) + abs(pos.y)
            }
        }
    }
    return abs(pos.x) + abs(pos.y)
}

fun main() = timed {
    val directions = (DATAPATH / "2016/day01.txt")
        .readText()
        .split(""",?\s""".toRegex())
        .filter(String::isNotBlank)
    println("Part one: ${followDirections(directions, false)}")
    println("Part two: ${followDirections(directions, true)}")
}
