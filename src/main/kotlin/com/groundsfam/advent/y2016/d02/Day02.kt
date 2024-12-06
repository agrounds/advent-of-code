package com.groundsfam.advent.y2016.d02

import com.groundsfam.advent.DATAPATH
import com.groundsfam.advent.Direction
import com.groundsfam.advent.grids.Grid
import com.groundsfam.advent.grids.maybeGet
import com.groundsfam.advent.grids.pointOfFirst
import com.groundsfam.advent.points.go
import com.groundsfam.advent.timed
import com.groundsfam.advent.toDirection
import kotlin.io.path.div
import kotlin.io.path.useLines

val grid1 = Grid(mutableListOf(
    mutableListOf('1', '2', '3'),
    mutableListOf('4', '5', '6'),
    mutableListOf('7', '8', '9'),
))
val grid2 = Grid(mutableListOf(
    mutableListOf(null, null, '1', null, null),
    mutableListOf(null, '2', '3', '4', null),
    mutableListOf('5', '6', '7', '8', '9'),
    mutableListOf(null, 'A', 'B', 'C', null),
    mutableListOf(null, null, 'D', null, null),
))

fun followDirections(directions: List<List<Direction>>, partTwo: Boolean): String {
    val grid =
        if (partTwo) grid2
        else grid1
    var pos = grid.pointOfFirst { it == '5' }
    val ret = mutableListOf<Char>()
    directions.forEach { dirs ->
        dirs.forEach { d ->
            val nextPos = pos.go(d)
            if (grid.maybeGet(nextPos) != null) {
                pos = nextPos
            }
        }
        ret.add(grid[pos]!!)
    }
    return ret.joinToString("")
}

fun main() = timed {
    val directions = (DATAPATH / "2016/day02.txt").useLines { lines ->
        lines.mapTo(mutableListOf()) { line ->
            line.map { it.toDirection()!! }
        }
    }
    println("Part one: ${followDirections(directions, false)}")
    println("Part one: ${followDirections(directions, true)}")
}
