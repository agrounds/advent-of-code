package com.groundsfam.advent.y2021.d09

import com.groundsfam.advent.DATAPATH
import com.groundsfam.advent.Grid
import com.groundsfam.advent.points.down
import com.groundsfam.advent.points.left
import com.groundsfam.advent.points.right
import com.groundsfam.advent.points.up
import com.groundsfam.advent.readGrid
import com.groundsfam.advent.timed
import kotlin.io.path.div


fun findLowPoints(heightMap: Grid<Int>): Int =
    heightMap.pointIndices.sumOf { p ->
        val lowPoint = listOf(p.left, p.right, p.up, p.down).all { n ->
            !heightMap.containsPoint(n) || heightMap[n] > heightMap[p]
        }
        if (lowPoint) heightMap[p] + 1
        else 0
    }

fun main() = timed {
    val heightMap = (DATAPATH / "2021/day09.txt")
        .readGrid(Char::digitToInt)
    println("Part one: ${findLowPoints(heightMap)}")
}
