package com.groundsfam.advent.y2024.d08

import com.groundsfam.advent.DATAPATH
import com.groundsfam.advent.gcd
import com.groundsfam.advent.grids.Grid
import com.groundsfam.advent.grids.contains
import com.groundsfam.advent.grids.forEachIndexed
import com.groundsfam.advent.grids.readGrid
import com.groundsfam.advent.points.Point
import com.groundsfam.advent.points.times
import com.groundsfam.advent.timed
import kotlin.io.path.div

fun countAntinodes(grid: Grid<Char>, partTwo: Boolean): Int {
    val antennas = mutableMapOf<Char, MutableList<Point>>()
    grid.forEachIndexed { p: Point, c: Char ->
        if (c != '.') {
            val points = antennas[c]
                ?: mutableListOf<Point>().also { antennas[c] = it }
            points.add(p)
        }
    }
    val antinodes = antennas.keys.fold(mutableSetOf()) { antinodes: MutableSet<Point>, frequency: Char ->
        val antennaList = antennas[frequency]!!
        antennaList.forEachIndexed { i, p ->
            (i + 1 until antennaList.size).forEach { j ->
                val q = antennaList[j]
                if (partTwo) {
                    // vector pointing parallel to the p-q line, reduced to
                    // coprime coordinates
                    // if we did not reduce d, we might end up skipping points
                    // on the p-q line
                    val d = (p - q).let {
                        it / gcd(it.x, it.y)
                    }
                    var r = p
                    while (r in grid) {
                        antinodes.add(r)
                        r += d
                    }
                    r = p - d
                    while (r in grid) {
                        antinodes.add(r)
                        r -= d
                    }
                } else {
                    val maybeAntinodes = listOf(2 * p - q, 2 * q - p)
                    maybeAntinodes.forEach {
                        if (it in grid) {
                            antinodes.add(it)
                        }
                    }
                }
            }
        }
        antinodes
    }
    return antinodes.size
}

fun main() = timed {
    val grid = (DATAPATH / "2024/day08.txt").readGrid()
    println("Part one: ${countAntinodes(grid, false)}")
    println("Part two: ${countAntinodes(grid, true)}")
}
