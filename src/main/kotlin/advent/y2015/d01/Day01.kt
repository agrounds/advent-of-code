package advent.y2015.d01

import advent.DATAPATH
import kotlin.io.path.div
import kotlin.io.path.useLines


fun main() {
    val line = (DATAPATH / "2015/day01.txt").useLines { lines ->
        lines.first()
    }
    line
        .filter { it == '(' || it == ')' }
        .partition { it == '(' }
        .also { (opens, closes) ->
            println("Part one: ${opens.length - closes.length}")
        }
    var position = 0
    var firstBasement: Int? = null
    line.onEachIndexed { index, c ->
        when (c) {
            '(' -> position++
            ')' -> {
                position--
                if (position < 0 && firstBasement == null) {
                    firstBasement = index
                }
            }
        }
    }
    firstBasement = firstBasement?.inc()
    println("Part two: $firstBasement")
}