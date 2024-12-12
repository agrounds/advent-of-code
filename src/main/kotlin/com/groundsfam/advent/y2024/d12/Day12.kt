package com.groundsfam.advent.y2024.d12

import com.groundsfam.advent.DATAPATH
import com.groundsfam.advent.Direction
import com.groundsfam.advent.grids.*
import com.groundsfam.advent.points.*
import com.groundsfam.advent.timed
import kotlin.io.path.div

fun totalFencePrice(grid: Grid<Char>): Pair<Long, Long> {
    val sums = LongArray(2)
    val seen = mutableSetOf<Point>()

    grid.forEachIndexed { p: Point, c: Char ->
        if (seen.add(p)) {
            // this is a new region
            // find all points in the region to compute area and perimeter
            val queue = ArrayDeque<Point>()
            val regionPoints = mutableSetOf<Point>()
            var area = 0
            var perimeter = 0
            var numSides = 0
            queue.add(p)
            while (queue.isNotEmpty()) {
                val q = queue.removeFirst()
                if (q !in grid || grid[q] != c || q in regionPoints) continue

                area++
                perimeter += 4 - 2 * q.adjacents(false).count { it in regionPoints }
                Direction.entries.forEach { dir ->
                    val forward = q.go(dir)
                    val left = q.go(dir.ccw)
                    val right = q.go(dir.cw)
                    if (forward in regionPoints) {
                        numSides--
                        listOf(left, right).forEach {
                            if (it !in regionPoints && it.go(dir) in regionPoints) {
                                numSides++
                            }
                        }
                    } else {
                        numSides++
                        listOf(left, right).forEach {
                            if (it in regionPoints && it.go(dir) !in regionPoints) {
                                numSides--
                            }
                        }
                    }
                }

                regionPoints.add(q)
                queue.addAll(q.adjacents(false))
            }

            // add all points from this region to seen so we don't recompute cost for this region
            seen.addAll(regionPoints)
            sums[0] += area.toLong() * perimeter
            sums[1] += area.toLong() * numSides
        }
    }

    return sums[0] to sums[1]
}

fun main() = timed {
    val grid = (DATAPATH / "2024/day12.txt").readGrid()
    val (partOne, partTwo) = totalFencePrice(grid)
    println("Part one: $partOne")
    println("Part two: $partTwo")
}
