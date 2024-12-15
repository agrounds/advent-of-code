package com.groundsfam.advent.y2024.d14

import com.groundsfam.advent.DATAPATH
import com.groundsfam.advent.grids.*
import com.groundsfam.advent.points.*
import com.groundsfam.advent.timed
import kotlin.io.path.div
import kotlin.io.path.useLines

const val WIDTH = 101
const val HEIGHT = 103
const val TIME = 100

// pair = (position, velocity)
fun parse(line: String): Pair<Point, Point> {
    val (p, v) = line.split(" ").map {
        it.substring(2).split(",").map(String::toInt)
    }
    val p1 = p.let { (x, y) -> Point(x, y) }
    val v1 = v.let { (dx, dy) ->
        // change velocities to be positive
        Point(
            if (dx < 0) WIDTH + dx else dx,
            if (dy < 0) HEIGHT + dy else dy,
        )
    }
    return Pair(p1, v1)
}

fun runRobots(robots: List<Pair<Point, Point>>): Long {
    val counts = IntArray(4)

    robots.forEach { (p, v) ->
        val (x, y) = (p + TIME * v).let { (x, y) ->
            Point(x % WIDTH, y % HEIGHT)
        }
        if (x < WIDTH / 2) {
            if (y < HEIGHT / 2) {
                counts[0]++
            }
            if (y > HEIGHT / 2) {
                counts[1]++
            }
        }
        if (x > WIDTH / 2) {
            if (y < HEIGHT / 2) {
                counts[2]++
            }
            if (y > HEIGHT / 2) {
                counts[3]++
            }
        }
    }
    return counts.fold(1L) { prod, n -> prod * n }
}

fun findChristmasTree(robots: List<Pair<Point, Point>>): Int {
    val robotPositions = Array(robots.size) { robots[it].first }

    var count = 0
    while (true) {
        val grid = Grid(HEIGHT, WIDTH) { '.' }
        robotPositions.forEach { p -> grid[p] = '#' }

        robots.forEachIndexed { i, (_, v) ->
            robotPositions[i] = (robotPositions[i] + v).let { (x, y) ->
                Point(x % WIDTH, y % HEIGHT)
            }
        }
        // find set of positions with a straight line in it
        var hasLine = false
        (0 until HEIGHT).forEach { y ->
            var lineCount = 0
            grid.getRow(y).forEach { c ->
                if (c == '#') {
                    lineCount++
                    if (lineCount == 8) {
                        hasLine = true
                    }
                } else {
                    lineCount = 0
                }
            }
        }
        if (hasLine) {
            return count
        }
        count++
    }
}

fun main() = timed {
    val robots = (DATAPATH / "2024/day14.txt").useLines { lines ->
        lines.mapTo(mutableListOf(), ::parse)
    }
    println("Part one: ${runRobots(robots)}")
    println("Part two: ${findChristmasTree(robots)}")
}
