package com.groundsfam.advent.y2016.d03

import com.groundsfam.advent.DATAPATH
import com.groundsfam.advent.timed
import kotlin.io.path.div
import kotlin.io.path.useLines

fun isPossibleTriangle(triple: List<Int>): Boolean {
    val (a, b, c) = triple.sorted()
    return a + b > c
}

fun toVerticalTriples(lines: List<List<Int>>): List<List<Int>> {
    val ret = mutableListOf<List<Int>>()
    (lines.indices step 3).forEach { i ->
        (0..2).forEach { j ->
            ret.add((0..2).map { lines[i + it][j] })
        }
    }
    return ret
}

fun main() = timed {
    val triples = (DATAPATH / "2016/day03.txt").useLines { lines ->
        lines.mapTo(mutableListOf()) { line ->
            line
                .trim()
                .split("""\s+""".toRegex())
                .map { it.toInt() }
        }
    }
    println("Part one: ${triples.count(::isPossibleTriangle)}")
    println("Part two: ${toVerticalTriples(triples).count(::isPossibleTriangle)}")
}
