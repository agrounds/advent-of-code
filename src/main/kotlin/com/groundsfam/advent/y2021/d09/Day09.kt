package com.groundsfam.advent.y2021.d09

import com.groundsfam.advent.DATAPATH
import com.groundsfam.advent.Grid
import com.groundsfam.advent.points.Point
import com.groundsfam.advent.points.down
import com.groundsfam.advent.points.left
import com.groundsfam.advent.points.right
import com.groundsfam.advent.points.up
import com.groundsfam.advent.readGrid
import com.groundsfam.advent.timed
import kotlin.io.path.div


class Solution(private val heightMap: Grid<Int>) {
    private val lowPoints = heightMap.pointIndices.filter { p ->
        listOf(p.left, p.right, p.up, p.down).all { n ->
            !heightMap.containsPoint(n) || heightMap[n] > heightMap[p]
        }
    }

    fun lowPointRiskLevel(): Int =
        lowPoints
            .sumOf { heightMap[it] + 1 }

    private fun basinSize(lowPoint: Point): Long {
        val visited = mutableSetOf<Point>()
        val queue = ArrayDeque<Point>()
        queue.add(lowPoint)
        var basinSize: Long = 0

        while (queue.isNotEmpty()) {
            val p = queue.removeFirst()
            if (visited.add(p)) {
                basinSize++
                listOf(p.left, p.right, p.up, p.down).forEach { n ->
                    if (heightMap.containsPoint(n) && heightMap[n] in (heightMap[p] + 1 until 9)) {
                        queue.add(n)
                    }
                }
            }
        }

        return basinSize
    }

    fun basinSizeProduct(): Long =
        lowPoints
            .map(::basinSize)
            .sorted()
            .takeLast(3)
            .reduce { a, b -> a * b }
}

fun main() = timed {
    val solution = (DATAPATH / "2021/day09.txt")
        .readGrid(Char::digitToInt)
        .let(::Solution)
    println("Part one: ${solution.lowPointRiskLevel()}")
    println("Part two: ${solution.basinSizeProduct()}")
}
