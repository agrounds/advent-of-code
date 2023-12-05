package com.groundsfam.advent.y2023.d03

import com.groundsfam.advent.DATAPATH
import com.groundsfam.advent.timed
import kotlin.io.path.div
import kotlin.io.path.useLines

fun partNumbers(lines: List<String>): List<Int> {
    val ret = mutableListOf<Int>()
    lines.forEachIndexed { y, line ->
        var x = 0
        while (x < line.length) {
            var numEnd = x // exclusive
            while (numEnd < line.length && line[numEnd].isDigit()) {
                numEnd++
            }
            if (numEnd == x) {
                // (x,y) is not part of a number
                x++
            } else {
                val symbolAdjacent = (x - 1..numEnd).any { x1 ->
                    x1 in line.indices && (y - 1..y + 1).any { y1 ->
                        y1 in lines.indices && lines[y1][x1].let { c ->
                            !c.isDigit() && c != '.'
                        }
                    }
                }
                if (symbolAdjacent) {
                    ret.add(line.substring(x until numEnd).toInt())
                }
                x = numEnd
            }
        }
    }
    return ret
}

fun gearRatios(lines: List<String>): List<Long> {
    val ret = mutableListOf<Long>()
    lines.forEachIndexed { y, line ->
        line.mapIndexedNotNull { x, c ->
            if (c == '*') {
                x
            } else {
                null
            }
        }.forEach { x ->
            val adjacentNumbers = mutableSetOf<Pair<Int, Int>>()
            (y - 1..y + 1).forEach { y1 ->
                (x - 1..x + 1).forEach { x1 ->
                    if (y1 in lines.indices && x1 in lines[y1].indices && lines[y1][x1].isDigit()) {
                        if (x1 == x - 1 || x1 - 1 !in lines[y1].indices || !lines[y1][x1 - 1].isDigit()) {
                            adjacentNumbers.add(x1 to y1)
                        }
                    }
                }
            }
            if (adjacentNumbers.size == 2) {
                adjacentNumbers.map { (x1, y1) ->
                    val line1 = lines[y1]
                    var numStart = x1
                    while (numStart in line1.indices && line1[numStart].isDigit()) {
                        numStart--
                    }
                    var numEnd = x1
                    while (numEnd in line1.indices && line1[numEnd].isDigit()) {
                        numEnd++
                    }
                    line1.substring((numStart + 1..<numEnd)).toLong()
                }
                    .reduce { a, b -> a * b }
                    .also { ret.add(it) }
            }
        }
    }
    return ret
}


fun main() = timed {
    val lines = (DATAPATH / "2023/day03.txt").useLines { it.toList() }
    partNumbers(lines)
        .sum()
        .also { println("Part one: $it") }
    gearRatios(lines)
        .sum()
        .also { println("Part two: $it") }
}
