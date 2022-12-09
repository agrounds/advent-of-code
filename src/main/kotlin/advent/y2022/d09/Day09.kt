package advent.y2022.d09

import advent.DATAPATH
import kotlin.io.path.div
import kotlin.io.path.useLines
import kotlin.math.abs

data class Move(val direction: Char, val times: Int)

fun trackTail(moves: List<Move>): Int {
    var headX = 0
    var headY = 0
    var tailX = 0
    var tailY = 0
    val tailVisited = mutableSetOf(tailX to tailY)
    moves.forEach { move ->
        repeat(move.times) {
            when (move.direction) {
                'U' -> headY++
                'D' -> headY--
                'R' -> headX++
                'L' -> headX--
                else -> throw RuntimeException("Illegal direction ${move.direction}")
            }
            if (abs(headX - tailX) > 1 || abs(headY - tailY) > 1) {
                tailX = when {
                    tailX < headX -> tailX + 1
                    tailX > headX -> tailX - 1
                    else -> tailX
                }
                tailY = when {
                    tailY < headY -> tailY + 1
                    tailY > headY -> tailY - 1
                    else -> tailY
                }
            }
            tailVisited.add(tailX to tailY)
        }
    }
    return tailVisited.size
}


fun main() {
    val moves = (DATAPATH / "2022/day09.txt").useLines { lines ->
        lines.toList().map { line ->
            Move(line.first(), line.substring(2).toInt())
        }
    }
    trackTail(moves)
        .also { println("Part one: $it") }
}
