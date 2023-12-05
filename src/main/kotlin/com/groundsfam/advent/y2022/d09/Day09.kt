package com.groundsfam.advent.y2022.d09

import com.groundsfam.advent.DATAPATH
import com.groundsfam.advent.timed
import kotlin.io.path.div
import kotlin.io.path.useLines
import kotlin.math.abs

data class Move(val direction: Char, val times: Int)

fun trackTail(moves: List<Move>, numKnots: Int): Int {
    val knotsX = IntArray(numKnots)
    val knotsY = IntArray(numKnots)

    val tailVisited = mutableSetOf(knotsX.last() to knotsY.last())
    moves.forEach { move ->
        repeat(move.times) {
            when (move.direction) {
                'U' -> knotsY[0]++
                'D' -> knotsY[0]--
                'R' -> knotsX[0]++
                'L' -> knotsX[0]--
                else -> throw RuntimeException("Illegal direction ${move.direction}")
            }
            (1 until numKnots).forEach { i ->
                val headX = knotsX[i-1]
                val headY = knotsY[i-1]
                val tailX = knotsX[i]
                val tailY = knotsY[i]
                if (abs(headX - tailX) > 1 || abs(headY - tailY) > 1) {
                    knotsX[i] = when {
                        tailX < headX -> tailX + 1
                        tailX > headX -> tailX - 1
                        else -> tailX
                    }
                    knotsY[i] = when {
                        tailY < headY -> tailY + 1
                        tailY > headY -> tailY - 1
                        else -> tailY
                    }
                }
            }
            tailVisited.add(knotsX.last() to knotsY.last())
        }
    }
    return tailVisited.size
}


fun main() = timed {
    val moves = (DATAPATH / "2022/day09.txt").useLines { lines ->
        lines.toList().map { line ->
            Move(line.first(), line.substring(2).toInt())
        }
    }
    trackTail(moves, 2)
        .also { println("Part one: $it") }
    trackTail(moves, 10)
        .also { println("Part two: $it") }
}
