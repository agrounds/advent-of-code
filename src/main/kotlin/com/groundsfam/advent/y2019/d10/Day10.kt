package com.groundsfam.advent.y2019.d10

import com.groundsfam.advent.DATAPATH
import com.groundsfam.advent.gcd
import com.groundsfam.advent.grids.*
import com.groundsfam.advent.points.*
import com.groundsfam.advent.timed
import kotlin.io.path.div
import kotlin.math.abs

const val ASTEROID_NUM = 200

val quadrantSort: (Point) -> Int = { (x, y) ->
    when {
        x == 0 && y < 0 -> 1
        x > 0 && y < 0 -> 2
        x > 0 && y == 0 -> 3
        x > 0 && y > 0 -> 4
        x == 0 && y > 0 -> 5
        x < 0 && y > 0 -> 6
        x < 0 && y == 0 -> 7
        else -> 8
    }
}
val sortWithinQuadrant: (Point) -> Double = { (x, y) ->
    // angles with x or y = 0 are sorted by quadrantSort already
    // otherwise, within each quadrant, the fraction y/x ranges
    // either from 0 to +inf (upper left and lower right)
    // or -inf to 0 (upper right and lower left) as (x, y)
    // rotates clockwise
    if (x == 0 || y == 0) 0.0
    else y.toDouble() / x
}
val angleSort = compareBy(quadrantSort, sortWithinQuadrant)

fun destroyAsteroids(grid: Grid<Char>): Pair<Int, Int> {
    var bestPos = Point(0, 0)
    var numSeen = 0
    grid.pointIndices.forEach { p ->
        if (grid[p] == '#') {
            val num = grid.pointIndices.count { q ->
                val d = (q - p)
                if (gcd(d.x, d.y) != 1) false
                else {
                    var asteroid = false
                    var q1 = p + d
                    while (q1 in grid) {
                        if (grid[q1] == '#') {
                            asteroid = true
                            break
                        }
                        q1 += d
                    }
                    asteroid
                }
            }
            if (num > numSeen) {
                bestPos = p
                numSeen = num
            }
        }
    }

    val asteroidAngles = grid.pointIndices
        .filter { it != bestPos && grid[it] == '#' }
        .groupingBy { p ->
            (p - bestPos).let { it / gcd(it.x, it.y) }
        }
        .aggregateTo(mutableMapOf<Point, MutableSet<Point>>()) { _, set, elem, _ ->
            (set ?: mutableSetOf()).also { it.add(elem) }
        }

    val anglesToVaporize = ArrayDeque<Point>()
    var vaporizedPoint: Point? = null

    repeat(ASTEROID_NUM) {
        if (anglesToVaporize.isEmpty()) {
            anglesToVaporize.addAll(
                asteroidAngles.keys.sortedWith(angleSort)
            )
        }
        val angle = anglesToVaporize.removeFirst()
        val points = asteroidAngles[angle]!!
        val p = points.minBy { abs(it.x) + abs(it.y) }
        points.remove(p)
        if (points.isEmpty()) {
            asteroidAngles.remove(angle)
        }
        vaporizedPoint = p
    }

    return Pair(numSeen, vaporizedPoint!!.let { (x, y) -> 100 * x + y })
}

fun main() = timed {
    val grid = (DATAPATH / "2019/day10.txt").readGrid()
    val (numSeen, lastVaporized) = destroyAsteroids(grid)
    println("Part one: $numSeen")
    println("Part two: $lastVaporized")
}
