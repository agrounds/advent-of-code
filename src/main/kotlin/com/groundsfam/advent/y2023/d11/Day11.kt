package com.groundsfam.advent.y2023.d11

import com.groundsfam.advent.DATAPATH
import com.groundsfam.advent.points.Point
import com.groundsfam.advent.timed
import kotlin.io.path.div
import kotlin.io.path.useLines
import kotlin.math.exp
import kotlin.math.max
import kotlin.math.min


val galaxies = mutableListOf<Point>()
// values of x
val nonemptyCols = mutableSetOf<Int>()
// values of y
val nonemptyRows = mutableSetOf<Int>()

fun dist(galaxy1: Point, galaxy2: Point, expansion: Long): Long {
    val minX = min(galaxy1.x, galaxy2.x)
    val maxX = max(galaxy1.x, galaxy2.x)
    val minY = min(galaxy1.y, galaxy2.y)
    val maxY = max(galaxy1.y, galaxy2.y)

    return maxX - minX + (expansion - 1) * (minX..maxX).filterNot(nonemptyCols::contains).size +
        maxY - minY + (expansion - 1) * (minY..maxY).filterNot(nonemptyRows::contains).size
}

fun totalGalaxyDistance(expansion: Long): Long =
    galaxies.indices.sumOf { i ->
        galaxies.indices.filter { it > i }.sumOf { j ->
            dist(galaxies[i], galaxies[j], expansion)
        }
    }


fun main() = timed {
    (DATAPATH / "2023/day11.txt").useLines { lines ->
        lines.forEachIndexed { y, line ->
            line.forEachIndexed { x, c ->
                if (c == '#') {
                    galaxies.add(Point(x, y))
                    nonemptyCols.add(x)
                    nonemptyRows.add(y)
                }
            }
        }
    }
    println("Part one: ${totalGalaxyDistance(2)}")
    println("Part two: ${totalGalaxyDistance(1_000_000)}")
}
